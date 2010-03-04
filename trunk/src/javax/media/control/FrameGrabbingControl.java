package javax.media.control;

import javax.media.Buffer;
import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface FrameGrabbingControl extends Control {
	public Buffer grabFrame();
}
