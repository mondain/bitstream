package javax.media.rtp;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface GlobalTransmissionStats {
	public int getRTPSent();

	public int getBytesSent();

	public int getRTCPSent();

	public int getLocalColls();

	public int getRemoteColls();

	public int getTransmitFailed();

}
