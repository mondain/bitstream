package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class DataStarvedEvent extends StopEvent {

	public DataStarvedEvent(Controller from, int previous, int current,
			int target, Time mediaTime) {
		super(from, previous, current, target, mediaTime);
	}

}
