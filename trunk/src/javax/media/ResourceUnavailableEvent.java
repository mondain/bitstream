package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class ResourceUnavailableEvent extends ControllerErrorEvent {

	public ResourceUnavailableEvent(Controller from, String why) {
		super(from, why);
	}

	public ResourceUnavailableEvent(Controller from) {
		super(from);
	}

}
