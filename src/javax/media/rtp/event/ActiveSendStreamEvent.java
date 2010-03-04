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
public class ActiveSendStreamEvent extends SendStreamEvent {

	public ActiveSendStreamEvent(SessionManager from, Participant participant,
			SendStream stream) {
		super(from, stream, participant);
	}

}
