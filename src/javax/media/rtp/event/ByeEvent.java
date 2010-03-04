package javax.media.rtp.event;

import javax.media.rtp.Participant;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.SessionManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class ByeEvent extends TimeoutEvent {
	private String reason;

	public ByeEvent(SessionManager from, Participant participant,
			ReceiveStream recvStream, String reason, boolean participantBye) {
		super(from, participant, recvStream, participantBye);
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}
}
