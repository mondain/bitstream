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
public class InactiveReceiveStreamEvent extends ReceiveStreamEvent {
	private boolean laststream;

	public InactiveReceiveStreamEvent(SessionManager from,
			Participant participant, ReceiveStream recvStream,
			boolean laststream) {
		super(from, recvStream, participant);
		this.laststream = laststream;
	}

	public boolean isLastStream() {
		return laststream;
	}
}
