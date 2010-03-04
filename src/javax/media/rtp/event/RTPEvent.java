package javax.media.rtp.event;

import javax.media.MediaEvent;
import javax.media.rtp.SessionManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class RTPEvent extends MediaEvent {
	private SessionManager eventSrc;

	public RTPEvent(SessionManager from) {
		super(from);
		this.eventSrc = from;
	}

	public Object getSource() {
		return eventSrc;
	}

	public SessionManager getSessionManager() {
		return eventSrc;
	}

	public String toString() {
		return getClass().getName() + "[source = " + eventSrc + "]";
	}
}
