package javax.media.rtp.rtcp;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface Feedback {
	public long getSSRC();

	public int getFractionLost();

	public long getNumLost();

	public long getXtndSeqNum();

	public long getJitter();

	public long getLSR();

	public long getDLSR();

}
