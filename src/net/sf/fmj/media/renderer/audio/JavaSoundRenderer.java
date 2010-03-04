package net.sf.fmj.media.renderer.audio;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.Renderer;
import javax.media.ResourceUnavailableException;
import javax.media.format.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.CompoundControl;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.Control.Type;

import javazoom.spi.mpeg.sampled.file.MpegAudioFormat;
import javazoom.spi.mpeg.sampled.file.MpegEncoding;
import javazoom.spi.vorbis.sampled.file.VorbisAudioFormat;
import javazoom.spi.vorbis.sampled.file.VorbisEncoding;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.ObjectCollection;

/**
 * net.sf.fmj.media.renderer.audio.FmjAudioRenderer
 * 
 * @author Warren Bloomer
 * 
 */
public class JavaSoundRenderer implements Renderer {

	private static final Logger logger = LoggerSingleton.logger;

	private String name = "FMJ Audio Renderer";

	/** the selected mixer to use */
	private Mixer mixer;

	/** the DataLine to write audio data to. */
	private SourceDataLine sourceLine;

	/** javax.media version of audio format */
	private AudioFormat inputFormat;

	/** javax.sound version of audio format */
	private javax.sound.sampled.AudioFormat sampledFormat;

	/** set of controls */
	private final ObjectCollection controls = new ObjectCollection();

	// To support ULAW, we use a codec which can convert from ULAW to LINEAR.
	// JMF's renderer can do this, although it may be overkill to use a codec.
	// TODO: support ULAW directly by simply converting the samples.
	// Same for ALAW.
	private Codec codec; // in case we need to do any conversions
	private final Buffer codecBuffer = new Buffer();

	/* ----------------------- Renderer interface ------------------------- */

	/**
	 * Returns the name of the pluging.
	 */
	public String getName() {
		return name;
	}

	private Format[] supportedInputFormats = new Format[] {
			new AudioFormat(AudioFormat.LINEAR, -1.0, -1, -1, -1, -1, -1, -1.0,
					Format.byteArray),
			new AudioFormat(AudioFormat.ULAW, -1.0, -1, -1, -1, -1, -1, -1.0,
					Format.byteArray), // TODO: our codec doesn't support all
										// ULAW input formats.
			new AudioFormat(AudioFormat.ALAW, -1.0, -1, -1, -1, -1, -1, -1.0,
					Format.byteArray), // TODO: our codec doesn't support all
										// ALAW input formats.
	};

	/**
	 * Set supported input formats for the default or selected Mixer. Perhaps
	 * just list generic LINEAR, ALAW and ULAW. At the moment, we are returning
	 * all the formats handled by the current default mixer.
	 */
	public Format[] getSupportedInputFormats() {

		return supportedInputFormats; // JMF doesn't return all the details.

		// Vector supportedFormats = new Vector();
		//		
		// Mixer.Info mixerInfo = null; // default mixer
		// Mixer mixer = AudioSystem.getMixer(mixerInfo);
		//
		// Line.Info[] lineInfos = mixer.getSourceLineInfo();
		// for (int i=0; i<lineInfos.length; i++) {
		// DataLine.Info lineInfo = (DataLine.Info) lineInfos[i];
		// javax.sound.sampled.AudioFormat[] formats = lineInfo.getFormats();
		// for (int j=0; j<formats.length; j++) {
		// AudioFormat format = convertFormat(formats[j]);
		// supportedFormats.add(format);
		// }
		// }
		//		
		// return (Format[]) supportedFormats.toArray(new Format[]{});
	}

	public Format setInputFormat(Format format) {
		logger.info("Setting input format to: " + format);
		if (!(format instanceof AudioFormat)) {
			return null;
		}

		this.inputFormat = (AudioFormat) format;

		return inputFormat;
	}

	public Object getControl(String controlType) {
		return controls.getControl(controlType);
	}

	public Object[] getControls() {
		return controls.getControls();
	}

	/**
	 * Open the plugin. Must be called after the formats have been determined
	 * and before "process" is called.
	 * 
	 * Open the DataLine.
	 */
	public void open() throws ResourceUnavailableException {
		javax.sound.sampled.AudioFormat audioFormat = convertFormat(inputFormat);
		logger.info("opening with javax.sound format: " + audioFormat);
		try {

			if (!inputFormat.getEncoding().equals(AudioFormat.LINEAR)) {
				logger
						.info("JavaSoundRenderer: Audio format is not linear, creating conversion");

				if (inputFormat.getEncoding().equals(AudioFormat.ULAW))
					codec = new net.sf.fmj.media.codec.audio.ulaw.Decoder(); // much
																				// more
																				// efficient
																				// than
																				// JavaSoundCodec
				else if (inputFormat.getEncoding().equals(AudioFormat.ALAW))
					codec = new net.sf.fmj.media.codec.audio.alaw.Decoder(); // much
																				// more
																				// efficient
																				// than
																				// JavaSoundCodec
				else
					throw new ResourceUnavailableException(
							"Unsupported input format encoding: "
									+ inputFormat.getEncoding());
				// codec = new net.sf.fmj.media.codec.JavaSoundCodec();
				codec.setInputFormat(inputFormat);
				final Format[] outputFormats = codec
						.getSupportedOutputFormats(inputFormat);
				if (outputFormats.length < 1)
					throw new ResourceUnavailableException(
							"Unable to get an output format for input format: "
									+ inputFormat);
				final AudioFormat codecOutputFormat = (AudioFormat) outputFormats[0]; // TODO:
																						// choose
																						// the
																						// best
																						// quality
																						// one.
				codec.setOutputFormat(codecOutputFormat);
				audioFormat = convertFormat(codecOutputFormat);

				codec.open();

				logger
						.info("JavaSoundRenderer: Audio format is not linear, created conversion from "
								+ inputFormat + " to " + codecOutputFormat);

			}

			sourceLine = getSourceDataLine(audioFormat);
			sourceLine.open(audioFormat);

			{
				FloatControl gainFloatControl = null;
				BooleanControl muteBooleanControl = null;

				try {
					gainFloatControl = (FloatControl) sourceLine
							.getControl(FloatControl.Type.MASTER_GAIN);
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					muteBooleanControl = (BooleanControl) sourceLine
							.getControl(BooleanControl.Type.MUTE);
				} catch (Exception e) {
					e.printStackTrace();
				}

				// TODO add other controls
				JavaSoundGainControl gainControl = new JavaSoundGainControl(
						gainFloatControl, muteBooleanControl);
				controls.addControl(gainControl);
			}

			logControls(sourceLine.getControls());
		} catch (LineUnavailableException e) {
			throw new ResourceUnavailableException(e.getMessage());
		}
	}

	/**
	 * Created to work around the fact that AudioSystem.getSourceDataLine is not
	 * available in 1.4.
	 */
	private static SourceDataLine getSourceDataLine(
			javax.sound.sampled.AudioFormat format)
			throws LineUnavailableException {
		// 1.5:
		// return AudioSystem.getSourceDataLine(format);
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		return (SourceDataLine) AudioSystem.getLine(info);

	}

	/**
	 * Free the data line.
	 */
	public void close() {
		logger.info("closing...");
		controls.clear();
		if (codec != null) {
			codec.close();
			codec = null;
		}
		sourceLine.close();
		sourceLine = null;
	}

	/**
	 * Reset the state of the plugin. The reset method is typically called if
	 * the end of media is reached or the media is repositioned.
	 */
	public void reset() {
		logger.info("resetting...");
	}

	/**
	 * Start the rendering process
	 */
	public void start() {
		logger.info("starting...");
		sourceLine.start();
	}

	/**
	 * Stop the rendering process.
	 */
	public void stop() {
		logger.info("stopping...");
		sourceLine.stop();
	}

	// the problem with not blocking is that we can get choppy audio. This would
	// be
	// solved theoretically by having the filter graph infrastructure pre-buffer
	// some
	// data. The other problem with non-blocking is that the filter graph has to
	// repeatedly call process, and it has no idea when it can call again and
	// have some
	// input consumed. This is, I think, kind of a rough spot in the JMF
	// architecture.
	// the filter graph could sleep, but how long should it sleep?
	// the problem with blocking, is that (if we allow it, which we don't) stop
	// will interrupt any write to sourceLine,
	// and basically, data will be lost. This will result in a gap in the audio
	// upon
	// start. If we don't interrupt with a stop, then the track can only fully
	// stop after process
	// has written all of the data.
	private static final boolean NON_BLOCKING = false;

	/**
	 * Write the buffer to the SourceDataLine.
	 */
	public int process(Buffer buffer) {

		// if we need to convert the format, do so using the codec.
		if (codec != null) {
			final int codecResult = codec.process(buffer, codecBuffer);
			if (codecResult == BUFFER_PROCESSED_FAILED)
				return BUFFER_PROCESSED_FAILED;
			if (codecResult == OUTPUT_BUFFER_NOT_FILLED)
				return BUFFER_PROCESSED_OK;
			buffer = codecBuffer;
		}

		int length = buffer.getLength();
		int offset = buffer.getOffset();

		final Format format = buffer.getFormat();

		final Class type = format.getDataType();
		if (type != Format.byteArray) {
			return BUFFER_PROCESSED_FAILED;
		}

		final byte[] data = (byte[]) buffer.getData();

		final boolean bufferNotConsumed;
		final int newBufferLength; // only applicable if bufferNotConsumed
		final int newBufferOffset; // only applicable if bufferNotConsumed

		if (NON_BLOCKING) {
			// TODO: handle sourceLine.available(). This code currently causes
			// choppy audio.

			if (length > sourceLine.available()) {
				// we should only write sourceLine.available() bytes, then
				// return INPUT_BUFFER_NOT_CONSUMED.
				length = sourceLine.available(); // don't try to write more than
													// available
				bufferNotConsumed = true;
				newBufferLength = buffer.getLength() - length;
				newBufferOffset = buffer.getOffset() + length;

			} else {
				bufferNotConsumed = false;
				newBufferLength = length;
				newBufferOffset = offset;
			}
		} else {
			bufferNotConsumed = false;
			newBufferLength = 0;
			newBufferOffset = 0;
		}

		if (length == 0) {
			logger
					.finer("Buffer has zero length, flags = "
							+ buffer.getFlags());

		}

		// make sure all the bytes are written.
		while (length > 0) {

			// logger.fine("Available: " + sourceLine.available());
			// logger.fine("length: " + length);
			// logger.fine("sourceLine.getBufferSize(): " +
			// sourceLine.getBufferSize());

			final int n = sourceLine.write(data, offset, length); // TODO: this
																	// can block
																	// for a
																	// very long
																	// time if
																	// it
																	// doesn't
			if (n >= length)
				break;
			else if (n == 0) {
				// TODO: we could choose to handle a write failure this way,
				// assuming that it is considered legal to call stop while
				// process is being called.
				// however, that seems like a bad idea in general.
				// if (!sourceLine.isRunning())
				// {
				// buffer.setLength(offset);
				// buffer.setOffset(length);
				// return INPUT_BUFFER_NOT_CONSUMED; // our write was
				// interrupted.
				// }

				logger.warning("sourceLine.write returned 0, offset=" + offset
						+ "; length=" + length + "; available="
						+ sourceLine.available() + "; frame size in bytes"
						+ sourceLine.getFormat().getFrameSize()
						+ "; sourceLine.isActive() = " + sourceLine.isActive()
						+ "; " + sourceLine.isOpen()
						+ "; sourceLine.isRunning()=" + sourceLine.isRunning());
				return BUFFER_PROCESSED_FAILED; // sourceLine.write docs
												// indicate that this will only
												// happen if there is an error.

			} else {
				offset += n;
				length -= n;
			}

		}

		if (bufferNotConsumed) {
			// return INPUT_BUFFER_NOT_CONSUMED if not all bytes were written

			buffer.setLength(newBufferLength);
			buffer.setOffset(newBufferOffset);
			return INPUT_BUFFER_NOT_CONSUMED;
		}

		if (buffer.isEOM()) {
			// TODO: the proper way to do this is to implement Drainable, and
			// let the processor call our drain method.
			sourceLine.drain(); // we need to ensure that the media finishes
								// playing, otherwise the EOM event will
			// be posted before the media finishes playing.
		}

		return BUFFER_PROCESSED_OK;
	}

	/* ----------------------------- */

	public int hashCode() {
		return super.hashCode() ^ 0xAD; // TODO: this hash code change is
										// useless.
		// TODO: for putting entries into the plugin manager,
		// PlugInManager.addPlugIn appears to
		// create a hash only based on the full class name, and only the last 22
		// chars of it.
		// that is,
		// ClassNameInfo.makeHashValue("com.sun.media.renderer.audio.JavaSoundRenderer")
		// and
		// ClassNameInfo.makeHashValue("net.sf.fmj.media.renderer.audio.JavaSoundRenderer")
		// both return the same value, as does
		// ClassNameInfo.makeHashValue("udio.JavaSoundRenderer")

		// therefore, this trick of creating a different hash code for this
		// class, does nothing to avoid the
		// warnings, when JMF is ahead in the classpath:

		// Problem adding net.sf.fmj.media.renderer.audio.JavaSoundRenderer to
		// plugin table.
		// Already hash value of 1262232571547748861 in plugin table for class
		// name of com.sun.media.renderer.audio.JavaSoundRenderer

	}

	/* -------------------- private methods ----------------------- */

	/**
	 * Convert javax.sound.sampled.AudioFormat to
	 * javax.media.format.AudioFormat.
	 */
	public static AudioFormat convertFormat(
			javax.sound.sampled.AudioFormat format) {

		Encoding encoding = format.getEncoding();
		int channels = format.getChannels();
		float frameRate = format.getFrameRate();
		int frameSize = format.getFrameSize() < 0 ? format.getFrameSize()
				: (format.getFrameSize() * 8);
		float sampleRate = format.getSampleRate();
		int sampleSize = format.getSampleSizeInBits();

		int endian = format.isBigEndian() ? AudioFormat.BIG_ENDIAN
				: AudioFormat.LITTLE_ENDIAN;

		int signed = AudioFormat.NOT_SPECIFIED;
		String encodingString = AudioFormat.LINEAR;

		if (encoding == Encoding.PCM_SIGNED) {
			signed = AudioFormat.SIGNED;
			encodingString = AudioFormat.LINEAR;
		} else if (encoding == Encoding.PCM_UNSIGNED) {
			signed = AudioFormat.UNSIGNED;
			encodingString = AudioFormat.LINEAR;
		} else if (encoding == Encoding.ALAW) {
			encodingString = AudioFormat.ALAW;
		} else if (encoding == Encoding.ULAW) {
			encodingString = AudioFormat.ULAW;
		} else {
			encodingString = encoding.toString();

		}

		AudioFormat jmfFormat = new AudioFormat(encodingString,
				(double) sampleRate, sampleSize, channels, endian, signed,
				frameSize, frameRate, AudioFormat.byteArray);

		return jmfFormat;
	}

	/**
	 * 
	 * @return null if doesn't match any mpeg encoding
	 */
	private static Encoding toMpegEncoding(String encodingStr) {
		// TODO: perhaps we should use reflection to avoid class not found
		// problems if javazoom is not in the classpath.

		final Encoding[] mpegEncodings = new Encoding[] { MpegEncoding.MPEG1L1,
				MpegEncoding.MPEG1L2, MpegEncoding.MPEG1L3,
				MpegEncoding.MPEG2DOT5L1, MpegEncoding.MPEG2DOT5L2,
				MpegEncoding.MPEG2DOT5L3, MpegEncoding.MPEG2L1,
				MpegEncoding.MPEG2L2, MpegEncoding.MPEG2L3,

		};

		for (int i = 0; i < mpegEncodings.length; ++i) {
			if (encodingStr.equals(mpegEncodings[i].toString()))
				return mpegEncodings[i];
		}
		return null;

	}

	/**
	 * 
	 * @return null if doesn't match any vorbis encoding
	 */
	private static Encoding toVorbisEncoding(String encodingStr) {
		// TODO: perhaps we should use reflection to avoid class not found
		// problems if javazoom is not in the classpath.

		final Encoding[] vorbisEncodings = new Encoding[] { VorbisEncoding.VORBISENC

		};

		for (int i = 0; i < vorbisEncodings.length; ++i) {
			if (encodingStr.equals(vorbisEncodings[i].toString()))
				return vorbisEncodings[i];
		}
		return null;

	}

	public static javax.sound.sampled.AudioFormat convertFormat(
			AudioFormat format) {

		String encodingString = format.getEncoding();
		int channels = format.getChannels();
		double frameRate = format.getFrameRate();
		int frameSize = format.getFrameSizeInBits() / 8;
		double sampleRate = format.getSampleRate();
		int sampleSize = format.getSampleSizeInBits();
		boolean endian = (format.getEndian() == AudioFormat.BIG_ENDIAN);
		int signed = format.getSigned();

		Encoding encoding;
		if (AudioFormat.LINEAR.equals(encodingString)) {
			switch (signed) {
			case AudioFormat.SIGNED:
				encoding = Encoding.PCM_SIGNED;
				break;
			case AudioFormat.UNSIGNED:
				encoding = Encoding.PCM_UNSIGNED;
				break;
			default:
				encoding = Encoding.PCM_SIGNED; // TODO: return null
			}
		} else if (AudioFormat.ALAW.equals(encodingString)) {
			encoding = Encoding.ALAW;
		} else if (AudioFormat.ULAW.equals(encodingString)) {
			encoding = Encoding.ULAW;
		} else if (toMpegEncoding(encodingString) != null) {

			encoding = toMpegEncoding(encodingString);

		} else if (toVorbisEncoding(encodingString) != null) {

			encoding = toVorbisEncoding(encodingString);

		} else {
			encoding = new CustomEncoding(encodingString);
		}

		final javax.sound.sampled.AudioFormat sampledFormat;

		if (encoding == Encoding.PCM_SIGNED) {
			sampledFormat = new javax.sound.sampled.AudioFormat(
					(float) sampleRate, sampleSize, channels, true, endian);

		} else if (encoding == Encoding.PCM_UNSIGNED) {
			sampledFormat = new javax.sound.sampled.AudioFormat(
					(float) sampleRate, sampleSize, channels, false, endian);
		} else if (encoding instanceof MpegEncoding) {
			// TODO: perhaps we should use reflection to avoid class not found
			// problems if javazoom is not in the classpath.
			return new MpegAudioFormat(encoding, (float) sampleRate,
					sampleSize, channels,
					// signed,
					frameSize, (float) frameRate, endian, new HashMap());
		} else if (encoding instanceof VorbisEncoding) {
			// TODO: perhaps we should use reflection to avoid class not found
			// problems if javazoom is not in the classpath.
			return new VorbisAudioFormat(encoding, (float) sampleRate,
					sampleSize, channels,
					// signed,
					frameSize, (float) frameRate, endian, new HashMap());
		} else {
			sampledFormat = new javax.sound.sampled.AudioFormat(encoding,
					(float) sampleRate, sampleSize, channels, frameSize,
					(float) frameRate, endian);
		}

		return sampledFormat;
	}

	private static List getMixers() {
		Vector mixers = new Vector();
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (int i = 0; i < mixerInfos.length; i++) {
			Mixer mixer = AudioSystem.getMixer(mixerInfos[i]);
			mixers.add(mixer);
		}
		return mixers;
	}

	private static void getMixer(String name) {
		Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
		for (int i = 0; i < mixerInfos.length; i++) {
			// mixerInfos[i];
		}
	}

	/*
	 * private void logControls(SourceDataLine line) {
	 * sourceLine.getControl(BooleanControl.Type.MUTE);
	 * sourceLine.getControl(FloatControl.Type.VOLUME);
	 * sourceLine.getControl(FloatControl.Type.BALANCE);
	 * sourceLine.getControl(FloatControl.Type.PAN);
	 * 
	 * Control[] audioControls = line.getControls(); }
	 */

	private void logControls(Control[] controls) {
		for (int i = 0; i < controls.length; i++) {
			Control control = controls[i];
			logger.info("control: " + control);
			Type controlType = control.getType();
			if (controlType instanceof CompoundControl.Type) {
				logControls(((CompoundControl) control).getMemberControls());
			}
		}
	}
}
