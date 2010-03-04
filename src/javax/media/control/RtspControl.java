package javax.media.control;

import javax.media.Control;
import javax.media.rtp.RTPManager;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface RtspControl extends Control {
	public RTPManager[] getRTPManagers();
}
