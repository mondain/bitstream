package javax.media.rtp.event;

import javax.media.rtp.SendStream;
import javax.media.rtp.SessionManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class LocalPayloadChangeEvent extends SendStreamEvent {
	private int oldpayload; // strange, no getter
	private int newpayload;

	public LocalPayloadChangeEvent(SessionManager from, SendStream sendStream,
			int oldpayload, int newpayload) {
		super(from, sendStream, null);
		this.newpayload = newpayload;
		this.oldpayload = oldpayload;
	}

	public int getNewPayload() {
		return newpayload;
	}
}
