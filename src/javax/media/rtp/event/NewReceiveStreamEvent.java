package javax.media.rtp.event;

import javax.media.rtp.ReceiveStream;
import javax.media.rtp.SessionManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class NewReceiveStreamEvent extends ReceiveStreamEvent {

	public NewReceiveStreamEvent(SessionManager from, ReceiveStream recvStream) {
		super(from, recvStream, null);
	}
}
