package javax.media.control;

import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface BufferControl extends Control {
	public static final long DEFAULT_VALUE = -1;
	public static final long MAX_VALUE = -2;

	public long getBufferLength();

	public long setBufferLength(long time);

	public long getMinimumThreshold();

	public long setMinimumThreshold(long time);

	public void setEnabledThreshold(boolean b);

	public boolean getEnabledThreshold();
}
