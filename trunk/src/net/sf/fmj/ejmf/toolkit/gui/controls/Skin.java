package net.sf.fmj.ejmf.toolkit.gui.controls;

import java.awt.Component;

import javax.swing.AbstractButton;

/**
 * 
 * @author Ken Larson
 * 
 */
public interface Skin {
	public Component createStartButton();

	public Component createStopButton();

	public Component createFastForwardButton();

	public Component createGainMeterButton();

	public Component createPauseButton();

	public Component createReverseButton();

	public AbstractButton createVolumeControlButton_Increase();

	public AbstractButton createVolumeControlButton_Decrease();

	// public Component createTimeDisplayControl();
	public Component createProgressSlider();
}
