package javax.media.control;

import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface KeyFrameControl extends Control {

	public int setKeyFrameInterval(int frames);

	public int getKeyFrameInterval();

	public int getPreferredKeyFrameInterval();
}
