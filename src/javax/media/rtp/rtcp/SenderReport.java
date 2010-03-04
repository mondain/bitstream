package javax.media.rtp.rtcp;

import javax.media.rtp.RTPStream;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface SenderReport extends Report {

	public RTPStream getStream();

	public long getSenderPacketCount();

	public long getSenderByteCount();

	public long getNTPTimeStampMSW();

	public long getNTPTimeStampLSW();

	public long getRTPTimeStamp();

	public Feedback getSenderFeedback();
}
