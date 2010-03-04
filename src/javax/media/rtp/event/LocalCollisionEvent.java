package javax.media.rtp.event;

import javax.media.rtp.ReceiveStream;
import javax.media.rtp.SessionManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class LocalCollisionEvent extends SessionEvent {

	private ReceiveStream recvStream;
	private long newSSRC;

	public LocalCollisionEvent(SessionManager from, ReceiveStream recvStream,
			long newSSRC) {
		super(from);
		this.recvStream = recvStream;
		this.newSSRC = newSSRC;
	}

	public ReceiveStream getReceiveStream() {
		return recvStream;
	}

	public long getNewSSRC() {
		return newSSRC;
	}
}
