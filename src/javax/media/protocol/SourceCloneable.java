package javax.media.protocol;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface SourceCloneable {
	/**
	 * Based on JMF testing, the clone is in the same state as the original
	 * (opened and connected if the original is), but at the beginning of the
	 * media, not whatever position the original is.
	 */
	public DataSource createClone();
}
