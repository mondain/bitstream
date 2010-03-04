package net.sf.fmj.media.codec.audio.alaw;

import java.util.logging.Logger;

import javax.media.Format;
import javax.media.format.AudioFormat;

import net.sf.fmj.media.AbstractDePacketizer;
import net.sf.fmj.media.BonusAudioFormatEncodings;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * 
 * DePacketizer for ALAW/RTP. Doesn't have to do much, just copies input to
 * output. Uses buffer-swapping observed in debugging and seen in other
 * open-source DePacketizer implementations.
 * 
 * @author Ken Larson
 * 
 */
public class DePacketizer extends AbstractDePacketizer {
	private static final Logger logger = LoggerSingleton.logger;

	public String getName() {
		return "ALAW DePacketizer";
	}

	public DePacketizer() {
		super();
		this.inputFormats = new Format[] { new AudioFormat(
				BonusAudioFormatEncodings.ALAW_RTP, -1.0, -1, -1, -1, -1, -1,
				-1.0, Format.byteArray) };
	}

	// TODO: move to base class?
	protected Format[] outputFormats = new Format[] { new AudioFormat(
			AudioFormat.ALAW, -1.0, -1, -1, -1, -1, -1, -1.0, Format.byteArray) };

	public Format[] getSupportedOutputFormats(Format input) {
		if (input == null)
			return outputFormats;
		else {
			if (!(input instanceof AudioFormat)) {
				logger
						.warning(this.getClass().getSimpleName()
								+ ".getSupportedOutputFormats: input format does not match, returning format array of {null} for "
								+ input); // this can cause an NPE in JMF if it
											// ever happens.
				return new Format[] { null };
			}
			final AudioFormat inputCast = (AudioFormat) input;
			if (!inputCast.getEncoding().equals(
					BonusAudioFormatEncodings.ALAW_RTP)) {
				logger
						.warning(this.getClass().getSimpleName()
								+ ".getSupportedOutputFormats: input format does not match, returning format array of {null} for "
								+ input); // this can cause an NPE in JMF if it
											// ever happens.
				return new Format[] { null };
			}
			final AudioFormat result = new AudioFormat(AudioFormat.ALAW,
					inputCast.getSampleRate(), inputCast.getSampleSizeInBits(),
					inputCast.getChannels(), inputCast.getEndian(), inputCast
							.getSigned(), inputCast.getFrameSizeInBits(),
					inputCast.getFrameRate(), inputCast.getDataType());

			return new Format[] { result };
		}
	}

	public void open() {
	}

	public void close() {
	}

	public Format setInputFormat(Format arg0) {
		return super.setInputFormat(arg0);
	}

	public Format setOutputFormat(Format arg0) {
		return super.setOutputFormat(arg0);
	}

}
