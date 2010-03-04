package net.sf.fmj.media.parser;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.media.BadHeaderException;
import javax.media.Buffer;
import javax.media.Duration;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.ResourceUnavailableException;
import javax.media.Time;
import javax.media.Track;
import javax.media.format.JPEGFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PullDataSource;
import javax.media.protocol.PullSourceStream;

import net.sf.fmj.media.AbstractDemultiplexer;
import net.sf.fmj.media.AbstractTrack;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * Parser for multipart/x-mixed-replace - used in some cases for streaming
 * jpegs. TODO: check out the jipcam project, which has Mjpeg parsing info. This
 * project also has some ip camera info:
 * http://www.codeproject.com/cs/media/cameraviewer.asp?print=true Some camera
 * links from that project: Not responding:
 * http://chipmunk.uvm.edu/cgi-bin/webcam/nph-update.cgi?dummy=garb Works:
 * http://webcam-1.duesseldorf.it-on.net/cgi-bin/nph-update.cgi Works:
 * http://webcam-2.duesseldorf.it-on.net/cgi-bin/nph-update.cgi Works:
 * http://towercam.uu.edu/axis-cgi/mjpg/video.cgi Works:
 * http://136.165.99.86/axis-cgi/mjpg/video.cgi Works:
 * http://217.114.115.192/axis-cgi/mjpg/video.cgi Works:
 * http://129.78.249.81/axis-cgi/mjpg/video.cgi
 * 
 * Others: Works:
 * http://camera.baywatch.tv/axis-cgi/mjpg/video.cgi?camera=1&resolution
 * =352x240&compression=50 Works:
 * http://www.surfshooterhawaii.com//cgi-bin/axispush555.cgi?dummy=garb
 * 
 * More camera links: http://www.axis.com/solutions/video/gallery.htm
 * 
 * @author Ken Larson
 * 
 */
public class MultipartMixedReplaceParser extends AbstractDemultiplexer {
	private static final Logger logger = LoggerSingleton.logger;

	private ContentDescriptor[] supportedInputContentDescriptors = new ContentDescriptor[] { new ContentDescriptor(
			"multipart.x_mixed_replace") };

	private PullDataSource source;

	private PullSourceStreamTrack[] tracks;

	public MultipartMixedReplaceParser() {
		super();
	}

	@Override
	public ContentDescriptor[] getSupportedInputContentDescriptors() {
		return supportedInputContentDescriptors;
	}

	@Override
	public Track[] getTracks() throws IOException, BadHeaderException {
		return tracks;
	}

	@Override
	public void setSource(DataSource source) throws IOException,
			IncompatibleSourceException {
		final String protocol = source.getLocator().getProtocol();

		if (!(source instanceof PullDataSource))
			throw new IncompatibleSourceException();

		this.source = (PullDataSource) source;

	}

	// @Override
	@Override
	public void open() throws ResourceUnavailableException {
		try {
			source.connect();
			source.start(); // TODO: stop/disconnect on stop/close.

			final PullSourceStream[] streams = source.getStreams();

			tracks = new PullSourceStreamTrack[streams.length];

			for (int i = 0; i < streams.length; ++i) {
				tracks[i] = new VideoTrack(streams[i]);

			}

		} catch (IOException e) {
			logger.log(Level.WARNING, "" + e, e);
			throw new ResourceUnavailableException("" + e);
		}

		super.open();

	}

	@Override
	public void close() {

		if (tracks != null) {
			for (int i = 0; i < tracks.length; ++i) {
				if (tracks[i] != null) {
					tracks[i].deallocate();
					tracks[i] = null;
				}
			}
			tracks = null;
		}

		super.close();
	}

	// @Override
	@Override
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

	@Override
	public boolean isPositionable() {
		return false; // TODO
	}

	// @Override
	// public Time setPosition(Time where, int rounding)
	// {
	// }

	@Override
	public boolean isRandomAccess() {
		return super.isRandomAccess(); // TODO: can we determine this from the
										// data source?
	}

	private abstract class PullSourceStreamTrack extends AbstractTrack {
		public abstract void deallocate();

	}

	private class VideoTrack extends PullSourceStreamTrack {
		// TODO: track listener

		private final PullSourceStream stream;
		private final VideoFormat format;

		public VideoTrack(PullSourceStream stream)
				throws ResourceUnavailableException {
			super();

			this.stream = stream;
			// set format

			// read first frame to determine format
			final Buffer buffer = new Buffer();
			readFrame(buffer);
			if (buffer.isDiscard() || buffer.isEOM())
				throw new ResourceUnavailableException(
						"Unable to read first frame");
			// TODO: catch runtime exception too?

			// parse jpeg
			final java.awt.Image image;
			try {
				image = ImageIO.read(new ByteArrayInputStream((byte[]) buffer
						.getData(), buffer.getOffset(), buffer.getLength()));
			} catch (IOException e) {
				logger.log(Level.WARNING, "" + e, e);
				throw new ResourceUnavailableException();
			}

			if (image == null) {
				logger.log(Level.WARNING,
						"Failed to read image (ImageIO.read returned null).");
				throw new ResourceUnavailableException();
			}

			format = new JPEGFormat(new Dimension(image.getWidth(null), image
					.getHeight(null)), Format.NOT_SPECIFIED, Format.byteArray,
					-1.f, Format.NOT_SPECIFIED, Format.NOT_SPECIFIED);

			// TODO: this discards first image. save and return first time
			// readFrame is called.

		}

		@Override
		public void deallocate() {
		}

		/**
		 * 
		 * @return nanos skipped, 0 if unable to skip.
		 * @throws IOException
		 */
		public long skipNanos(long nanos) throws IOException {
			return 0; // TODO

		}

		public boolean canSkipNanos() {
			return false;
		}

		@Override
		public Format getFormat() {
			return format;
		}

		/** handles pushbacks. */
		private byte[] pushbackBuffer;
		private int pushbackBufferLen;
		private int pushbackBufferOffset;

		/** push bytes back, to be read again later. */
		private void pushback(byte[] bytes, int len) {
			if (pushbackBufferLen == 0) {
				pushbackBuffer = bytes; // TODO: copy?
				pushbackBufferLen = len;
				pushbackBufferOffset = 0;
			} else {
				final byte[] newPushbackBuffer = new byte[pushbackBufferLen
						+ len];
				System.arraycopy(pushbackBuffer, 0, newPushbackBuffer, 0,
						pushbackBufferLen);
				System.arraycopy(bytes, 0, newPushbackBuffer,
						pushbackBufferLen, len);
				pushbackBuffer = newPushbackBuffer;
				pushbackBufferLen = pushbackBufferLen + len;
				pushbackBufferOffset = 0;
			}
		}

		/** supports pushback. */
		private int read(byte[] buffer, int offset, int length)
				throws IOException {
			if (pushbackBufferLen > 0) { // read from pushback buffer
				final int lenToCopy = length < pushbackBufferLen ? length
						: pushbackBufferLen;
				System.arraycopy(pushbackBuffer, pushbackBufferOffset, buffer,
						offset, lenToCopy);
				pushbackBufferLen -= lenToCopy;
				pushbackBufferOffset += lenToCopy;
				return lenToCopy;
			} else {
				return stream.read(buffer, offset, length);
			}
		}

		private static final int MAX_LINE_LENGTH = 255;

		private class MaxLengthExceededException extends IOException {
		}

		/** return null on eom */
		private String readLine(int max) throws IOException {
			final byte[] buffer = new byte[max];
			int offset = 0;
			final int length = 1;
			while (true) {
				if (offset >= max)
					throw new MaxLengthExceededException(); // no newline up to
															// max.
				final int lenRead = read(buffer, offset, length);
				if (lenRead < 0)
					return null; // EOS
				if (buffer[offset] == '\n') {
					if (offset > 0 && buffer[offset - 1] == '\r')
						offset -= 1; // don't include \r
					return new String(buffer, 0, offset);
				}
				offset += 1;
			}
		}

		private byte[] readFully(int bytes) throws IOException {
			final byte[] buffer = new byte[bytes];
			int offset = 0;
			int length = bytes;

			while (true) {
				final int lenRead = read(buffer, offset, length);
				if (lenRead < 0)
					return null; // EOS
				if (lenRead == length)
					return buffer;
				length -= lenRead;
				offset += lenRead;
			}
		}

		// We put a limit on how much we will read, to prevent running out of
		// memory in case something goes wrong.
		private final int MAX_IMAGE_SIZE = 1000000;

		/** Read until we see the specified boundary. */
		private byte[] readUntil(String boundary) throws IOException {
			// TODO: is there a blank line before the boundary?
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			final byte[] boundaryBytes = boundary.getBytes();
			final byte[] matchBuffer = new byte[boundaryBytes.length];

			int matchOffset = 0; // will be nonzero when checking a potential
									// match
			while (true) {
				if (os.size() >= MAX_IMAGE_SIZE)
					throw new IOException("No boundary found in "
							+ MAX_IMAGE_SIZE + " bytes.");
				// TODO: read more efficiently, not 1 at a time.
				final int lenRead = read(matchBuffer, matchOffset, 1);
				if (lenRead < 0)
					return null; // EOS
				if (matchBuffer[matchOffset] == boundaryBytes[matchOffset]) {
					if (matchOffset == boundaryBytes.length - 1) { // found the
																	// boundary
						pushback(matchBuffer, matchOffset + 1); // push it back
																// to be read
																// again
						break;
					} else {
						// keep matching the boundary
						++matchOffset;
					}

				} else {
					if (matchOffset > 0) { // we had a partial, but not full
											// match - dump it all into the
											// buffer
						os.write(matchBuffer, 0, matchOffset + 1);
						matchOffset = 0;
					} else {

						// completely nonmatching byte
						os.write(matchBuffer, 0, 1);
					}
				}
			}

			final byte[] result = os.toByteArray();
			return result;

			// // the crlf before the boundary is part of the boundary, see
			// // http://www.w3.org/Protocols/rfc1341/7_2_Multipart.html
			// // TODO: we should basically add this to the boundary
			//
			// System.out.println("\\r=" + Integer.toHexString('\r'));
			// System.out.println("\\n=" + Integer.toHexString('\n'));
			// System.out.println("" +
			// Integer.toHexString(result[result.length-3]));
			// System.out.println("" +
			// Integer.toHexString(result[result.length-2]));
			// System.out.println("" +
			// Integer.toHexString(result[result.length-1]));
			//			
			// final int trim = 2;
			// final byte[] trimmedResult = new byte[result.length - trim];
			// System.arraycopy(result, 0, trimmedResult, 0,
			// trimmedResult.length);
			//			
			//
			//			
			// return trimmedResult;
		}

		/** Property keys converted to all uppercase. */
		private boolean parseProperty(String line, Properties properties) {
			final int index = line.indexOf(':');
			if (index < 0)
				return false;
			final String key = line.substring(0, index).trim();
			final String value = line.substring(index + 1).trim();
			properties.setProperty(key.toUpperCase(), value);
			return true;

		}

		private String boundary; // content boundary

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

		@Override
		public void readFrame(Buffer buffer) {
			// example data:
			// --ssBoundary8345
			// Content-Type: image/jpeg
			// Content-Length: 114587

			try {
				String line;
				// eat leading blank lines
				while (true) {
					line = readLine(MAX_LINE_LENGTH);
					if (line == null) {
						buffer.setEOM(true);
						buffer.setLength(0);
						return;
					}

					if (!line.trim().equals(""))
						break; // end of header

				}

				if (boundary == null) {
					boundary = line.trim(); // TODO: we should be able to get
											// this from the content type, but
											// the content type has this
											// stripped out. So we'll just take
											// the first nonblank line to be the
											// boundary.
					// System.out.println("boundary: " + boundary);
				} else {
					if (!line.trim().equals(boundary))
						throw new IOException("Expected boundary: " + line);
				}

				final Properties properties = new Properties();

				while (true) {
					line = readLine(MAX_LINE_LENGTH);
					if (line == null) {
						buffer.setEOM(true);
						buffer.setLength(0);
						return;
					}

					if (line.trim().equals(""))
						break; // end of header

					if (!parseProperty(line, properties))
						throw new IOException("Expected property: " + line);

				}

				final String contentType = properties
						.getProperty("Content-Type".toUpperCase());
				if (contentType == null) {
					logger.warning("Header properties: " + properties);
					throw new IOException("Expected Content-Type in header");
				}

				final byte[] data;

				final String contentLenStr = properties
						.getProperty("Content-Length".toUpperCase());
				if (contentLenStr != null) { // if we know the content length,
												// use it
					final int contentLen;
					try {
						contentLen = Integer.parseInt(contentLenStr);
					} catch (NumberFormatException e) {
						throw new IOException("Invalid content length: "
								+ contentLenStr);
					}

					// now, read the content-length bytes
					data = readFully(contentLen); // TODO: don't realloc each
													// time
				} else {
					// if we don't know the content length, just read until we
					// find the boundary.
					// Some IP cameras don't specify it, like
					// http://webcam-1.duesseldorf.it-on.net/cgi-bin/nph-update.cgi
					data = readUntil(boundary);

				}

				if (data == null) {
					buffer.setEOM(true);
					buffer.setLength(0);
					return;
				}

				buffer.setData(data);
				buffer.setOffset(0);
				buffer.setLength(data.length);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}

		}

		@Override
		public Time mapFrameToTime(int frameNumber) {
			return TIME_UNKNOWN;
		}

		@Override
		public int mapTimeToFrame(Time t) {
			return FRAME_UNKNOWN;
		}

		@Override
		public Time getDuration() {
			return Duration.DURATION_UNKNOWN; // TODO
		}

	}

}
