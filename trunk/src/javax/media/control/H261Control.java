package javax.media.control;

import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface H261Control extends Control {
	public boolean isStillImageTransmissionSupported();

	public boolean setStillImageTransmission(boolean newStillImageTransmission);

	public boolean getStillImageTransmission();
}
