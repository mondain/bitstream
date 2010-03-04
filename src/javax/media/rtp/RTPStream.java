package javax.media.rtp;

import javax.media.protocol.DataSource;
import javax.media.rtp.rtcp.SenderReport;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface RTPStream {

	public Participant getParticipant();

	public SenderReport getSenderReport();

	public long getSSRC();

	public DataSource getDataSource();

}
