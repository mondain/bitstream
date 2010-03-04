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
public class ApplicationEvent extends ReceiveStreamEvent {
	private int appSubtype;

	private String appString;

	private byte[] appData;

	public ApplicationEvent(SessionManager from, Participant participant,
			ReceiveStream recvStream, int appSubtype, String appString,
			byte[] appData) {
		super(from, recvStream, participant);
		this.appSubtype = appSubtype;
		this.appString = appString;
		this.appData = appData;
	}

	public int getAppSubType() {
		return appSubtype;
	}

	public String getAppString() {
		return appString;
	}

	public byte[] getAppData() {
		return appData;
	}

}
