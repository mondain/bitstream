package javax.media.rtp;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface OutputDataStream {
	public int write(byte[] buffer, int offset, int length);
}
