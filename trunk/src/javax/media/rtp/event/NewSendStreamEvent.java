package javax.media.rtp.event;

import javax.media.rtp.SendStream;
import javax.media.rtp.SessionManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class NewSendStreamEvent extends SendStreamEvent {

	public NewSendStreamEvent(SessionManager from, SendStream sendStream) {
		super(from, sendStream, null);
	}
}
