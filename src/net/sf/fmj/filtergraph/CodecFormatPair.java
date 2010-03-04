package net.sf.fmj.filtergraph;

import javax.media.Codec;
import javax.media.Format;

/**
 * Pairs a codec and a format. Used to be able to order our search through
 * codecs and formats.
 * 
 * @author Ken Larson
 * 
 */
public class CodecFormatPair {
	private final Codec codec;
	private final Format format;

	public CodecFormatPair(final Codec codec, final Format format) {
		super();
		this.codec = codec;
		this.format = format;
	}

	public Codec getCodec() {
		return codec;
	}

	public Format getFormat() {
		return format;
	}

}
