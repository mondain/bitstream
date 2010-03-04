package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class ControllerErrorEvent extends ControllerClosedEvent {

	public ControllerErrorEvent(Controller from, String why) {
		super(from, why);
	}

	public ControllerErrorEvent(Controller from) {
		super(from);
	}

	public String toString() {
		return getClass().getName() + "[source=" + getSource() + ",message="
				+ message + "]";

	}

}
