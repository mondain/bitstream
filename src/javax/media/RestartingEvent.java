package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class RestartingEvent extends StopEvent {

	public RestartingEvent(Controller from, int previous, int current,
			int target, Time mediaTime) {
		super(from, previous, current, target, mediaTime);
	}

}
