package javax.media.control;

import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface FrameRateControl extends Control {

	public float getFrameRate();

	public float setFrameRate(float newFrameRate);

	public float getMaxSupportedFrameRate();

	public float getPreferredFrameRate();

}
