package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class TransitionEvent extends ControllerEvent {
	int previousState;
	int currentState;
	int targetState;

	public TransitionEvent(Controller from, int previousState,
			int currentState, int targetState) {
		super(from);
		this.previousState = previousState;
		this.currentState = currentState;
		this.targetState = targetState;
	}

	public int getPreviousState() {
		return previousState;
	}

	public int getCurrentState() {
		return currentState;
	}

	public int getTargetState() {
		return targetState;
	}

	public String toString() {
		return getClass().getName() + "[source=" + getSource()
				+ ",previousState=" + previousState + ",currentState="
				+ currentState + ",targetState=" + targetState + "]";
	}
}
