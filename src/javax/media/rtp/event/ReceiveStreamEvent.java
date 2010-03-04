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
public class ReceiveStreamEvent extends RTPEvent {
	private ReceiveStream recvStream;
	private Participant participant;

	public ReceiveStreamEvent(SessionManager from, ReceiveStream stream,
			Participant participant) {
		super(from);
		this.recvStream = stream;
		this.participant = participant;

	}

	public ReceiveStream getReceiveStream() {
		return recvStream;
	}

	public Participant getParticipant() {
		return participant;
	}
}
