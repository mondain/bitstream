package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class StopAtTimeEvent extends StopEvent {

	public StopAtTimeEvent(Controller from, int previous, int current,
			int target, Time mediaTime) {
		super(from, previous, current, target, mediaTime);
	}

}
