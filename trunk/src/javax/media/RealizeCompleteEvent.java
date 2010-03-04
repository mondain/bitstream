package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class RealizeCompleteEvent extends TransitionEvent {
	public RealizeCompleteEvent(Controller from, int previous, int current,
			int target) {
		super(from, previous, current, target);
	}
}
