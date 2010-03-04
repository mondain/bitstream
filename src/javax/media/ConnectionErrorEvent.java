package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class ConnectionErrorEvent extends ControllerErrorEvent {

	public ConnectionErrorEvent(Controller from, String why) {
		super(from, why);
	}

	public ConnectionErrorEvent(Controller from) {
		super(from);
	}

}
