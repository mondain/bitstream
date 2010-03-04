package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class ControllerEvent extends MediaEvent {

	Controller eventSrc;

	public ControllerEvent(Controller from) {
		super(from);
		eventSrc = from;
	}

	public Controller getSourceController() {
		return eventSrc;
	}

	public Object getSource() {
		return eventSrc;
	}

	public String toString() {
		return getClass().getName() + "[source=" + eventSrc + "]";
	}
}