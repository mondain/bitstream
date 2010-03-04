package javax.media.rtp.event;

import javax.media.rtp.SessionManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class RemoteCollisionEvent extends RemoteEvent {
	private long collidingSSRC;

	public RemoteCollisionEvent(SessionManager from, long ssrc) {
		super(from);
		this.collidingSSRC = ssrc;
	}

	public long getSSRC() {
		return collidingSSRC;
	}
}
