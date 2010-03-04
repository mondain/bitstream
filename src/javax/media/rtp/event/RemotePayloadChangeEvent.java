package javax.media.rtp.event;

import javax.media.rtp.ReceiveStream;
import javax.media.rtp.SessionManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class RemotePayloadChangeEvent extends ReceiveStreamEvent {

	private int oldpayload; // strange, no getter.
	private int newpayload;

	public RemotePayloadChangeEvent(SessionManager from,
			ReceiveStream recvStream, int oldpayload, int newpayload) {
		super(from, recvStream, null);
		this.newpayload = newpayload;
		this.oldpayload = oldpayload;
	}

	public int getNewPayload() {
		return newpayload;
	}
}
