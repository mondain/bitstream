package javax.media.rtp.event;

import javax.media.rtp.Participant;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class SendStreamEvent extends RTPEvent {
	private SendStream sendStream;
	private Participant participant;

	public SendStreamEvent(SessionManager from, SendStream stream,
			Participant participant) {
		super(from);
		this.sendStream = stream;
		this.participant = participant;

	}

	public SendStream getSendStream() {
		return sendStream;
	}

	public Participant getParticipant() {
		return participant;
	}
}
