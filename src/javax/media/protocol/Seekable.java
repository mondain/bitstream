package javax.media.protocol;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface Seekable {
	public long seek(long where);

	public long tell();

	public boolean isRandomAccess();
}
