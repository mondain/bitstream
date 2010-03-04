package javax.media.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class FileTypeDescriptor extends ContentDescriptor {

	public static final String QUICKTIME = "video.quicktime";
	public static final String MSVIDEO = "video.x_msvideo";
	public static final String MPEG = "video.mpeg";
	public static final String VIVO = "video.vivo";
	public static final String BASIC_AUDIO = "audio.basic";
	public static final String WAVE = "audio.x_wav";
	public static final String AIFF = "audio.x_aiff";
	public static final String MIDI = "audio.midi";
	public static final String RMF = "audio.rmf";
	public static final String GSM = "audio.x_gsm";
	public static final String MPEG_AUDIO = "audio.mpeg";

	public FileTypeDescriptor(String cdName) {
		super(cdName);
	}

	public String toString() {

		final Map strings = new HashMap();
		{
			// TODO: externalize.
			strings.put(QUICKTIME, "QuickTime");
			strings.put(MSVIDEO, "AVI");
			strings.put(MPEG, "MPEG Video");
			strings.put(VIVO, "Vivo");
			strings.put(BASIC_AUDIO, "Basic Audio (au)");
			strings.put(WAVE, "WAV");
			strings.put(AIFF, "AIFF");
			strings.put(MIDI, "MIDI");
			strings.put(RMF, "RMF");
			strings.put(GSM, "GSM");
			strings.put(MPEG_AUDIO, "MPEG Audio");
		}

		final String result = (String) strings.get(getContentType());
		if (result == null)
			return super.toString();
		return result;
	}

}
