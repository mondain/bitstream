package javax.media.control;

import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface MonitorControl extends Control {
	public boolean setEnabled(boolean on);

	public float setPreviewFrameRate(float rate);
}
