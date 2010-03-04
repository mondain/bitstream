package net.sf.fmj.media;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.Renderer;

/**
 * Abstract implementation of Renderer, useful for subclassing.
 * 
 * @author Ken Larson
 * 
 */
public abstract class AbstractRenderer extends AbstractPlugIn implements
		Renderer {

	public abstract Format[] getSupportedInputFormats();

	public abstract int process(Buffer buffer);

	protected Format inputFormat;

	public Format setInputFormat(Format format) {
		this.inputFormat = format;
		return inputFormat;
	}

	public void start() {
	}

	public void stop() {
	}

}
