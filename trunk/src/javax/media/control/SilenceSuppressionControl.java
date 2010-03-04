package javax.media.control;

import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface SilenceSuppressionControl extends Control {
	public boolean getSilenceSuppression();

	public boolean setSilenceSuppression(boolean newSilenceSuppression);

	public boolean isSilenceSuppressionSupported();
}
