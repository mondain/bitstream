package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class StopByRequestEvent extends StopEvent {

	public StopByRequestEvent(Controller from, int previous, int current,
			int target, Time mediaTime) {
		super(from, previous, current, target, mediaTime);
	}

}
