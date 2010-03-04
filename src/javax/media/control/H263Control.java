package javax.media.control;

import javax.media.Control;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface H263Control extends Control {
	public boolean isUnrestrictedVectorSupported();

	public boolean setUnrestrictedVector(boolean newUnrestrictedVectorMode);

	public boolean getUnrestrictedVector();

	public boolean isArithmeticCodingSupported();

	public boolean setArithmeticCoding(boolean newArithmeticCodingMode);

	public boolean getArithmeticCoding();

	public boolean isAdvancedPredictionSupported();

	public boolean setAdvancedPrediction(boolean newAdvancedPredictionMode);

	public boolean getAdvancedPrediction();

	public boolean isPBFramesSupported();

	public boolean setPBFrames(boolean newPBFramesMode);

	public boolean getPBFrames();

	public boolean isErrorCompensationSupported();

	public boolean setErrorCompensation(boolean newtErrorCompensationMode);

	public boolean getErrorCompensation();

	public int getHRD_B();

	public int getBppMaxKb();
}
