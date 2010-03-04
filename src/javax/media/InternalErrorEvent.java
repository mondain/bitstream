package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class InternalErrorEvent extends ControllerErrorEvent {

	public InternalErrorEvent(Controller from, String why) {
		super(from, why);
	}

	public InternalErrorEvent(Controller from) {
		super(from);
	}

}
