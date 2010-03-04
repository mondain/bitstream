package com.sun.media.codec.audio;

import javax.media.Format;
import javax.media.format.AudioFormat;

import com.sun.media.BasicCodec;

/**
 * TODO: Still partially a stub - enough so that so that FOBS will work. Also
 * com.sun.media.codec.audio.rc.RateCvrt will work.
 * 
 * @author Ken Larson
 * 
 */
public abstract class AudioCodec extends BasicCodec {
	protected AudioFormat inputFormat;
	protected AudioFormat outputFormat;

	public AudioCodec() {
		super();

	}

	public Format setInputFormat(Format format) {
		super.setInputFormat(format);
		this.inputFormat = (AudioFormat) format;
		return format;
	}

	public Format setOutputFormat(Format format) {
		super.setOutputFormat(format);
		this.outputFormat = (AudioFormat) format;
		return format;
	}

	public boolean checkFormat(Format format) {
		return true; // TODO: FOBS extends this class so we have to do something
						// here. if we return false, FOBS audio won't work.
						// TODO: what needs to be checked here?
	}
}
