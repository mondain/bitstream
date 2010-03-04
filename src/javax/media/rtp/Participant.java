package javax.media.rtp;

import java.util.Vector;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface Participant {
	public Vector getStreams();

	public Vector getReports();

	public String getCNAME();

	public Vector getSourceDescription();
}
