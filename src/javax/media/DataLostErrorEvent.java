package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class DataLostErrorEvent extends ControllerClosedEvent {

	public DataLostErrorEvent(Controller from, String why) {
		super(from, why);
	}

	public DataLostErrorEvent(Controller from) {
		super(from);
	}

}
