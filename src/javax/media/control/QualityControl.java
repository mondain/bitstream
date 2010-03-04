package javax.media.control;

import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface QualityControl extends Control {

	public float getQuality();

	public float setQuality(float newQuality);

	public float getPreferredQuality();

	public boolean isTemporalSpatialTradeoffSupported();
}
