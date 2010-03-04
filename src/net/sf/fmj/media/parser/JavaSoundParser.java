package net.sf.fmj.media.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.BadHeaderException;
import javax.media.Buffer;
import javax.media.Duration;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.Track;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;
import javax.media.protocol.SourceCloneable;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import net.sf.fmj.media.AbstractDemultiplexer;
import net.sf.fmj.media.AbstractTrack;
import net.sf.fmj.media.PullSourceStreamInputStream;
import net.sf.fmj.media.renderer.audio.JavaSoundRenderer;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * 
 * @author Ken Larson
 * 
 */
public class JavaSoundParser extends AbstractDemultiplexer {
	private static final Logger logger = LoggerSingleton.logger;

	/*
	 * If readFrame reads from pullSourceStreamInputStreamForReadFrame without
	 * audioInputStreamForReadFrame being opened, then the codec is getting the
	 * raw data WITH the header. So it is able to create a converted
	 * AudioInputStream. But if we don't use a codec, then the renderer will get
	 * the header, which will come out as a short click or noise before the
	 * sound.
	 * 
	 * I see two options: 1. the header data gets passed using some special
	 * mechanism, like in a buffer header or in a buffer with a special flag
	 * set, so the codec knows to use it but a renderer will ignore it. 2. the
	 * codec reconstructs a header based on the format.
	 * 
	 * However, it is potentially worse than that, since WAV files are
	 * potentially stored as chunks, meaning there is more than just one header
	 * up front. So I don't see that option 1 is very good.
	 * 
	 * Another possibility is we could change the reported output format to not
	 * be a standard AudioFormat, but to be a WAV audio format, then let there
	 * be a specific codec for that.
	 * 
	 * With #2, we could have some luck, because any internal headers will be
	 * stripped out by the audio input stream. So we don't have to have the
	 * codec put on the exact correct header, it just has to be one that allows
	 * getAudioInputStream to work.
	 * 
	 * Method 2 works.
	 */

	private ContentDescriptor[] supportedInputContentDescriptors = new ContentDescriptor[] {
			new ContentDescriptor(FileTypeDescriptor.WAVE), // .wav
			new ContentDescriptor(FileTypeDescriptor.BASIC_AUDIO), // .au
			new ContentDescriptor(FileTypeDescriptor.AIFF), // .aiff
			new ContentDescriptor(FileTypeDescriptor.MPEG_AUDIO), // .mp3
																	// (requires
																	// mp3 SPI)
																	// - TODO:
																	// can we
																	// determine
																	// from
																	// JavaSound
																	// whether
																	// we have
																	// this SPI?
			new ContentDescriptor("audio.ogg"), // .ogg (requires ogg SPI)
			new ContentDescriptor("application.ogg"), // this has been observed
														// in the wild as well,
														// as an alias for
														// audio.ogg

	};

	// AudioSystem.getAudioFileTypes() does not return .mp3, even if the mp3 SPI
	// is in the classpath. So how can we
	// find out if it is present?

	// we need to open two streams, one is a clone of the other, because
	// determining the format
	// changes the stream position.
	private PullDataSource sourceForFormat;
	private PullDataSource sourceForReadFrame;

	private PullSourceStreamTrack[] tracks;

	// @Override
	public ContentDescriptor[] getSupportedInputContentDescriptors() {
		return supportedInputContentDescriptors;
	}

	// @Override
	public Track[] getTracks() throws IOException, BadHeaderException {
		return tracks;
	}

	// if we do the open in setSource, then we can identify an incompatible
	// source
	// right away. This is useful because the JMF/FMJ infrastructure (via the
	// Manager) will
	// try a new Demuliplexer in this case. If open fails, the Manager will not
	// retry.
	// this is useful because in the case of .ogg, this parser will only handle
	// audio, not video.
	// we need to use another Demultiplxer if there is video.
	private static final boolean OPEN_IN_SET_SOURCE = true;

	// @Override
	public void setSource(DataSource source) throws IOException,
			IncompatibleSourceException {
		if (!(source instanceof PullDataSource))
			throw new IncompatibleSourceException();
		if (!(source instanceof SourceCloneable))
			throw new IncompatibleSourceException();

		this.sourceForFormat = (PullDataSource) source;

		if (OPEN_IN_SET_SOURCE) {
			try {
				doOpen();
			} catch (UnsupportedAudioFileException e) {
				logger.log(Level.WARNING, "" + e, e);
				throw new IncompatibleSourceException("" + e);
			} catch (IOException e) {
				logger.log(Level.WARNING, "" + e, e);
				throw e;
			}

		}

	}

	private void doOpen() throws IOException, UnsupportedAudioFileException {
		sourceForReadFrame = (PullDataSource) ((SourceCloneable) sourceForFormat)
				.createClone();
		sourceForReadFrame.connect();
		sourceForReadFrame.start();

		sourceForFormat.start();
		final PullSourceStream[] streamsForFormat = sourceForFormat
				.getStreams();
		final PullSourceStream[] streamsForReadFrame = sourceForReadFrame
				.getStreams();

		tracks = new PullSourceStreamTrack[streamsForFormat.length];
		for (int i = 0; i < streamsForFormat.length; ++i) {
			tracks[i] = new PullSourceStreamTrack(streamsForFormat[i],
					streamsForReadFrame[i]);

		}
	}

	// @Override
	public void open() throws ResourceUnavailableException {
		if (!OPEN_IN_SET_SOURCE) {
			try {
				doOpen();
			} catch (UnsupportedAudioFileException e) {
				logger.log(Level.WARNING, "" + e, e);
				throw new ResourceUnavailableException("" + e);
			} catch (IOException e) {
				logger.log(Level.WARNING, "" + e, e);
				throw new ResourceUnavailableException("" + e);
			}

		}
	}

	// @Override
	public void start() throws IOException {

	}

	// TODO: should we stop data source in stop?
	// // @Override
	// public void stop()
	// {
	// try
	// {
	// source.stop();
	// } catch (IOException e)
	// {
	// logger.log(Level.WARNING, "" + e, e);
	// }
	// }

	// @Override
	public boolean isPositionable() {
		return true;
	}

	// @Override
	public boolean isRandomAccess() {
		return super.isRandomAccess(); // TODO: can we determine this from the
										// data source?
	}

	// @Override
	public Time setPosition(Time where, int rounding) {
		// TODO: maybe we should just set a variable, and have the readFrame
		// method handle the skip.
		// TODO: what do do about mp3/ogg?
		// TODO: what to do with rounding info?

		// if we can't skip based on nanos, then we can't seek. This happens for
		// mp3/ogg - compressed formats.
		for (int i = 0; i < tracks.length; ++i) {
			if (!tracks[i].canSkipNanos())
				return super.setPosition(where, rounding);
		}

		try {
			logger
					.fine("JavaSoundParser: cloning, reconnecting, and restarting source");
			// just clone the data source again, start at zero, then skip to the
			// position we want.
			sourceForReadFrame = (PullDataSource) ((SourceCloneable) sourceForFormat)
					.createClone();
			sourceForReadFrame.connect();
			sourceForReadFrame.start();

			for (int i = 0; i < tracks.length; ++i) {
				tracks[i]
						.setPssForReadFrame(sourceForReadFrame.getStreams()[i]);
				if (where.getNanoseconds() > 0)
					tracks[i].skipNanos(where.getNanoseconds()); // TODO: check
																	// result
			}

			return where; // TODO:
		} catch (IOException e) {
			logger.log(Level.WARNING, "" + e, e);
			throw new RuntimeException(e); // TODO: how to handle
		} catch (UnsupportedAudioFileException e) {
			logger.log(Level.WARNING, "" + e, e);
			throw new RuntimeException(e); // TODO: how to handle
		}
	}

	private class PullSourceStreamTrack extends AbstractTrack {
		// TODO: track listener

		private final javax.media.format.AudioFormat format;
		private final javax.sound.sampled.AudioFormat javaSoundInputFormat;
		private final long frameLength; // length of media in frames

		private PullSourceStream pssForReadFrame;
		private PullSourceStreamInputStream pssisForReadFrame;
		private AudioInputStream aisForReadFrame;

		private long totalBytesRead = 0L; // keep track of total bytes so we can
											// compute current timestamp

		public PullSourceStreamTrack(PullSourceStream pssForFormat,
				PullSourceStream pssForReadFrame)
				throws UnsupportedAudioFileException, IOException {
			super();

			// determine format and frame size.
			{
				final AudioInputStream aisForFormat;
				final PullSourceStreamInputStream pssisForFormat;

				pssisForFormat = new PullSourceStreamInputStream(pssForFormat);
				aisForFormat = AudioSystem
						.getAudioInputStream(markSupportedInputStream(pssisForFormat));
				this.javaSoundInputFormat = aisForFormat.getFormat();
				this.frameLength = aisForFormat.getFrameLength();
				this.format = JavaSoundRenderer
						.convertFormat(javaSoundInputFormat);

				logger.fine("JavaSoundParser: java sound format: "
						+ javaSoundInputFormat);
				logger.fine("JavaSoundParser: jmf format: " + format);
				logger.fine("JavaSoundParser: Frame length=" + frameLength);

				aisForFormat.close();
				pssisForFormat.close();
			}

			setPssForReadFrame(pssForReadFrame);

		}

		/**
		 * 
		 * @return nanos skipped, 0 if unable to skip.
		 * @throws IOException
		 */
		public long skipNanos(long nanos) throws IOException {
			final long bytes = nanosToBytes(nanos);
			if (bytes <= 0) {
				logger.fine("JavaSoundParser: skipping nanos: " + 0);
				return 0;
			}
			final long bytesSkipped = aisForReadFrame.skip(bytes);
			totalBytesRead += bytesSkipped;
			if (bytesSkipped == bytes) {
				logger.fine("JavaSoundParser: skipping nanos: " + nanos);
				return nanos;
			} else {
				final long result = bytesToNanos(bytesSkipped);
				logger.fine("JavaSoundParser: skipping nanos: " + result);
				return result;
			}

		}

		public boolean canSkipNanos() {
			if (javaSoundInputFormat.getFrameSize() > 0
					&& javaSoundInputFormat.getFrameRate() > 0.f)
				return true;
			else
				return false;
		}

		// we can use this to set a new stream
		public void setPssForReadFrame(PullSourceStream pssForReadFrame)
				throws UnsupportedAudioFileException, IOException {
			this.pssForReadFrame = pssForReadFrame;
			this.pssisForReadFrame = new PullSourceStreamInputStream(
					pssForReadFrame);
			this.aisForReadFrame = AudioSystem
					.getAudioInputStream(markSupportedInputStream(pssisForReadFrame));
			this.totalBytesRead = 0;

		}

		public Format getFormat() {
			return format;
		}

		/**
		 * 
		 * @return -1L if cannot convert, because frame size and frame rate are
		 *         not known.
		 */
		private long bytesToNanos(long bytes) {
			if (javaSoundInputFormat.getFrameSize() > 0
					&& javaSoundInputFormat.getFrameRate() > 0.f) {
				final long frames = bytes / javaSoundInputFormat.getFrameSize();
				final double seconds = frames
						/ javaSoundInputFormat.getFrameRate();
				final double nanos = secondsToNanos(seconds);
				return (long) nanos;
			} else
				return -1L;
		}

		/**
		 * 
		 * @return -1L if cannot convert, because frame size and frame rate are
		 *         not known.
		 */
		private long nanosToBytes(long nanos) {
			if (javaSoundInputFormat.getFrameSize() > 0
					&& javaSoundInputFormat.getFrameRate() > 0.f) {
				// TODO: optimize this to avoid loss of precision.
				final double seconds = nanosToSeconds(nanos);
				final double frames = seconds
						* javaSoundInputFormat.getFrameRate();
				final double bytes = frames
						* javaSoundInputFormat.getFrameSize();
				return (long) bytes;
			} else
				return -1L;
		}

		// TODO: from JAVADOC:
		// This method might block if the data for a complete frame is not
		// available. It might also block if the stream contains intervening
		// data for a different interleaved Track. Once the other Track is read
		// by a readFrame call from a different thread, this method can read the
		// frame. If the intervening Track has been disabled, data for that
		// Track is read and discarded.
		//
		// Note: This scenario is necessary only if a PullDataSource
		// Demultiplexer implementation wants to avoid buffering data locally
		// and copying the data to the Buffer passed in as a parameter.
		// Implementations might decide to buffer data and not block (if
		// possible) and incur data copy overhead.

		public void readFrame(Buffer buffer) {
			// format.getFrameSizeInBits() * format.getFrameRate();
			// TODO: make buffer size an integral number of frames.

			final int BUFFER_SIZE = 10000; // TODO: how do we determine this
											// size? Is this what the
											// BufferControl control is for?
			if (buffer.getData() == null)
				buffer.setData(new byte[BUFFER_SIZE]);
			final byte[] bytes = (byte[]) buffer.getData();

			// TODO: set other buffer fields, like format or sequenceNumber?
			try {
				final int result = aisForReadFrame.read(bytes, 0, bytes.length);
				if (result < 0) {
					buffer.setEOM(true);
					buffer.setLength(0);
					return;
				}

				// calculate timestamp for this buffer, if we can.
				if (javaSoundInputFormat.getFrameSize() > 0
						&& javaSoundInputFormat.getFrameRate() > 0.f) {
					buffer.setTimeStamp(bytesToNanos(totalBytesRead));
					buffer.setDuration(bytesToNanos(result));
				}
				// TODO: otherwise, set the timestamp/duration to what? 0? -1?

				totalBytesRead += result;
				buffer.setLength(result);
				buffer.setOffset(0);
			} catch (IOException e) {
				buffer.setEOM(true); // TODO
				buffer.setDiscard(true); // TODO
				buffer.setLength(0);
				logger.log(Level.WARNING, "" + e, e);
			}

		}

		public Time mapFrameToTime(int frameNumber) {
			return TIME_UNKNOWN; // this is what audio tracks are supposed to
									// return
		}

		public int mapTimeToFrame(Time t) {
			return FRAME_UNKNOWN; // this is what audio tracks are supposed to
									// return
		}

		public Time getDuration() {
			// TODO: for ogg/mp3, we end up with -1 frame length and return
			// unknown. Maybe only the codec can figure this out.
			// or maybe we need to open our own converted stream to find the
			// length.

			final long lengthInFrames = frameLength;
			if (lengthInFrames < 0) {
				logger
						.fine("PullSourceStreamTrack: returning Duration.DURATION_UNKNOWN (1)");
				return Duration.DURATION_UNKNOWN;
			}
			final double lengthInSeconds = lengthInFrames
					/ javaSoundInputFormat.getFrameRate();
			if (lengthInSeconds < 0.0) {
				logger
						.fine("PullSourceStreamTrack: returning Duration.DURATION_UNKNOWN (2)");
				return Duration.DURATION_UNKNOWN;

			}

			final double lengthInNanos = secondsToNanos(lengthInSeconds);

			logger.fine("PullSourceStreamTrack: returning "
					+ ((long) lengthInNanos));

			return new Time((long) lengthInNanos);

		}

	}

	// AudioSystem.getAudioInputStream needs to use marks to determine the
	// format.
	private static InputStream markSupportedInputStream(InputStream is) {
		if (is.markSupported())
			return is;
		else
			return new BufferedInputStream(is);
	}

	private static final double secondsToNanos(double secs) {
		return secs * 1000000000.0;
	}

	private static final double nanosToSeconds(double nanos) {
		return nanos / 1000000000.0;
	}

	// private static void dumpHeader()
	// {
	// // test code to find the header size
	// {
	// File file = new File(source.getLocator().getRemainder());
	//			
	// RandomAccessFile raf = new RandomAccessFile(file, "r");
	// final long fileLength = raf.length();
	// logger.fine("file size: " + fileLength);
	// raf.close();
	//			
	// InputStream fis = markSupportedInputStream(new FileInputStream(file));
	//
	//			
	// AudioSystem.getAudioInputStream(fis);
	// int fisLen = 0;
	// while (true)
	// {
	// if (fis.read() == -1)
	// break;
	// else
	// ++fisLen;
	// }
	//			
	// logger.fine("current pos: " + fisLen);
	// final long headerLen = fileLength - fisLen;
	// logger.fine("header len: " + headerLen);
	//			
	// FileInputStream fis2 = new FileInputStream(file);
	// System.out.print("new byte[] {");
	// for (int i = 0; i < headerLen; ++i)
	// {
	// int b = fis2.read();
	// if (b > 127)
	// System.out.print("(byte) " + b + ", ");
	// else
	// System.out.print(" " + b + ", ");
	// }
	// logger.fine("};");
	// }
	// }

}
