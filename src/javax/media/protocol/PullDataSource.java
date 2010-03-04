package javax.media.protocol;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public abstract class PullDataSource extends DataSource {
	public abstract PullSourceStream[] getStreams();
}
