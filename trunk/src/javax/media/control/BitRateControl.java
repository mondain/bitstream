package javax.media.control;

import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface BitRateControl extends Control {
	public int getBitRate();

	public int setBitRate(int bitrate);

	public int getMinSupportedBitRate();

	public int getMaxSupportedBitRate();
}
