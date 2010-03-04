package javax.media.control;

import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface PacketSizeControl extends Control {

	public int setPacketSize(int numBytes);

	public int getPacketSize();
}
