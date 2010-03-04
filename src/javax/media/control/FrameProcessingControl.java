package javax.media.control;

import javax.media.Control;

/**
 * 
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface FrameProcessingControl extends Control {
	public int getFramesDropped();

	public void setFramesBehind(float numFrames);

	public boolean setMinimalProcessing(boolean newMinimalProcessing);
}
