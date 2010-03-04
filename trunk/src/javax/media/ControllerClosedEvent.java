package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class ControllerClosedEvent extends ControllerEvent {

	protected String message;

	public ControllerClosedEvent(Controller from) {
		super(from);

	}

	public ControllerClosedEvent(Controller from, String why) {
		super(from);
		this.message = why;

	}

	public String getMessage() {
		return message;
	}
}
