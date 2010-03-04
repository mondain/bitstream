package javax.media.rtp.event;

import javax.media.rtp.SendStream;
import javax.media.rtp.SessionManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class StreamClosedEvent extends SendStreamEvent {
	public StreamClosedEvent(SessionManager from, SendStream sendStream) {
		super(from, sendStream, null);
	}
}
