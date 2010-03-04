package javax.media.rtp;

import javax.media.rtp.rtcp.SourceDescription;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface LocalParticipant extends Participant {
	public void setSourceDescription(SourceDescription[] sourceDesc);
}
