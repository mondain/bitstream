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
public class TimeoutEvent extends ReceiveStreamEvent {
	private boolean participantBye;

	public TimeoutEvent(SessionManager from, Participant participant,
			ReceiveStream recvStream, boolean participantBye) {
		super(from, recvStream, participant);
		this.participantBye = participantBye;
	}

	public boolean participantLeaving() {
		return participantBye;
	}
}
