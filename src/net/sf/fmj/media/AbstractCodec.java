package net.sf.fmj.media;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;

import net.sf.fmj.codegen.MediaCGUtils;

/**
 * 
 * @author Ken Larson
 * 
 */
public abstract class AbstractCodec extends AbstractPlugIn implements Codec {

	protected Format inputFormat = null;
	protected Format outputFormat = null;
	protected boolean opened = false;
	protected Format[] inputFormats = new Format[0];

	public Format[] getSupportedInputFormats() {
		return inputFormats;
	}

	public abstract Format[] getSupportedOutputFormats(Format input);

	public abstract int process(Buffer input, Buffer output);

	public Format setInputFormat(Format format) {
		this.inputFormat = format;
		return inputFormat;
	}

	public Format setOutputFormat(Format format) {
		this.outputFormat = format;
		return outputFormat;
	}

	protected Format getInputFormat() {
		return inputFormat;
	}

	protected Format getOutputFormat() {
		return outputFormat;
	}

	protected boolean checkInputBuffer(Buffer b) {
		return true; // TODO
	}

	protected boolean isEOM(Buffer b) {
		return b.isEOM();
	}

	protected void propagateEOM(Buffer b) {
		b.setEOM(true);
	}

	protected final void dump(String label, Buffer buffer) {
		System.out.println(label + ": " + MediaCGUtils.bufferToStr(buffer));

	}

}
