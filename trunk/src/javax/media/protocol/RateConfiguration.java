package javax.media.protocol;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface RateConfiguration {
	public RateRange getRate();

	public SourceStream[] getStreams();
}
