package javax.media.rtp.event;

import javax.media.rtp.SessionManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class RemoteEvent extends RTPEvent {

	public RemoteEvent(SessionManager from) {
		super(from);
	}

}
