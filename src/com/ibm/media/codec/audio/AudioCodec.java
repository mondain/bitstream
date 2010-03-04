package com.ibm.media.codec.audio;

import java.util.ArrayList;
import java.util.List;

import javax.media.Format;
import javax.media.format.AudioFormat;

import com.sun.media.BasicCodec;

/**
 * 
 * @author Ken Larson
 * 
 */
public abstract class AudioCodec extends BasicCodec {

	protected String PLUGIN_NAME;

	protected AudioFormat[] defaultOutputFormats;
	protected AudioFormat[] supportedInputFormats;
	protected AudioFormat[] supportedOutputFormats;
	protected AudioFormat inputFormat;
	protected AudioFormat outputFormat;
	protected final boolean DEBUG = true;

	public AudioCodec() {
		super();
	}

	public String getName() {
		return PLUGIN_NAME;
	}

	public Format[] getSupportedInputFormats() {
		return supportedInputFormats;
	}

	public Format setInputFormat(Format format) {
		if (!(format instanceof AudioFormat))
			return null;
		for (int i = 0; i < supportedInputFormats.length; ++i) {
			if (format.matches(supportedInputFormats[i])) {
				inputFormat = (AudioFormat) format;
				return inputFormat;
			}
		}
		return null;
	}

	public Format setOutputFormat(Format format) {
		if (!(format instanceof AudioFormat))
			return null;

		final Format[] formats = getMatchingOutputFormats(inputFormat);

		for (int i = 0; i < formats.length; ++i) {
			if (format.matches(formats[i])) {
				outputFormat = (AudioFormat) format;
				return outputFormat;
			}
		}
		return null;
	}

	protected Format getInputFormat() {
		return inputFormat;
	}

	protected Format getOutputFormat() {
		return outputFormat;
	}

	protected Format[] getMatchingOutputFormats(Format in) {
		return new Format[0]; // this seems to be always overridden by
								// subclasses
	}

	public Format[] getSupportedOutputFormats(Format in) {
		if (in == null)
			return defaultOutputFormats;

		// TODO: test this code.
		final List result = new ArrayList();

		if (in == null) {
			for (int i = 0; i < supportedInputFormats.length; ++i) {
				inputFormat = supportedInputFormats[i];

				Format[] matching = getMatchingOutputFormats(inputFormat);
				for (int j = 0; j < matching.length; ++j) {
					result.add(matching[j]);
				}
			}
		} else {
			for (int i = 0; i < supportedInputFormats.length; ++i) {
				if (in.matches(supportedInputFormats[i])) {
					inputFormat = (AudioFormat) in;

					Format[] matching = getMatchingOutputFormats(in);
					for (int j = 0; j < matching.length; ++j) {
						result.add(matching[j]); // TODO: since we are calling
													// getMatchingOutputFormats
													// with in, not
													// supportedInputFormats[i],
													// we can take this out of
													// the loop.
					}
				}
			}
		}

		final Format[] arrayResult = new Format[result.size()];
		for (int i = 0; i < result.size(); ++i) {
			arrayResult[i] = (Format) result.get(i);
		}

		return arrayResult;
	}

	public boolean checkFormat(Format format) {
		return true; // TODO
	}
}
