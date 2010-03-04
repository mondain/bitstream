package net.sf.fmj.media.multiplexer;

import javax.media.Format;
import javax.media.protocol.ContentDescriptor;

/**
 * 
 * Not tested yet. Not sure if useful for anything.
 * 
 * @author Ken Larson
 * 
 */
public class RawMux extends AbstractStreamCopyMux {

	public RawMux() {
		super(new ContentDescriptor(ContentDescriptor.RAW));
	}

	public Format[] getSupportedInputFormats() {
		throw new UnsupportedOperationException(); // TODO
	}
}
