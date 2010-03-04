package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class DeallocateEvent extends StopEvent {

	public DeallocateEvent(Controller from, int previous, int current,
			int target, Time mediaTime) {
		super(from, previous, current, target, mediaTime);
	}

}
