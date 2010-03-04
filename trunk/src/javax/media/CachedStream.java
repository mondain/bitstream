package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface CachedStream {
	public void setEnabledBuffering(boolean b);

	public boolean getEnabledBuffering();

	public boolean willReadBytesBlock(long offset, int numBytes);

	public boolean willReadBytesBlock(int numBytes);

	public void abortRead();
}
