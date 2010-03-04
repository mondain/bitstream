package javax.media.rtp;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface ReceptionStats {
	public int getPDUlost();

	public int getPDUProcessed();

	public int getPDUMisOrd();

	public int getPDUInvalid();

	public int getPDUDuplicate();

}
