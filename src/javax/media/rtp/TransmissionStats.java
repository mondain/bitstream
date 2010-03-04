package javax.media.rtp;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface TransmissionStats {
	public int getPDUTransmitted();

	public int getBytesTransmitted();

	public int getRTCPSent();

}
