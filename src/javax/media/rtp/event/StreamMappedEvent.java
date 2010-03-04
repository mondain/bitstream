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
public class StreamMappedEvent extends ReceiveStreamEvent {

	public StreamMappedEvent(SessionManager from, ReceiveStream stream,
			Participant participant) {
		super(from, stream, participant);
	}

}
