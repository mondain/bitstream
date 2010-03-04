package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class StopEvent extends TransitionEvent {

	private Time mediaTime;

	public StopEvent(Controller from, int previous, int current, int target,
			Time mediaTime) {
		super(from, previous, current, target);
		this.mediaTime = mediaTime;
	}

	public Time getMediaTime() {
		return mediaTime;
	}

	public String toString() {
		return getClass().getName() + "[source=" + getSource()
				+ ",previousState=" + getPreviousState() + ",currentState="
				+ getCurrentState() + ",targetState=" + getTargetState()
				+ ",mediaTime=" + mediaTime + "]";
	}
}
