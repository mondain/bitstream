package javax.media.rtp.rtcp;

import java.util.Vector;

import javax.media.rtp.Participant;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface Report {
	public Participant getParticipant();

	public long getSSRC();

	public Vector getFeedbackReports();

	public Vector getSourceDescription();
}
