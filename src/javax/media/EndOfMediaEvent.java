package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class EndOfMediaEvent extends StopEvent {

	public EndOfMediaEvent(Controller from, int previous, int current,
			int target, Time mediaTime) {
		super(from, previous, current, target, mediaTime);
	}

}
