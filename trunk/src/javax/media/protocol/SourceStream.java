package javax.media.protocol;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface SourceStream extends Controls {

	public static final long LENGTH_UNKNOWN = -1L;

	public ContentDescriptor getContentDescriptor();

	public long getContentLength();

	public boolean endOfStream();
}
