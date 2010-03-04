package javax.media.control;

import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface StreamWriterControl extends Control {

	public boolean setStreamSizeLimit(long numOfBytes);

	public long getStreamSize();
}
