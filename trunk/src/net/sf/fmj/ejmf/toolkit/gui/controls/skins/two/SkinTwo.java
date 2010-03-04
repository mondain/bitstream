package net.sf.fmj.ejmf.toolkit.gui.controls.skins.two;

import java.awt.Component;

import javax.swing.AbstractButton;

import net.sf.fmj.ejmf.toolkit.gui.controls.Skin;
import net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf.GainMeterButton;
import net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf.ProgressSlider;
import net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf.ReverseButton;
import net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf.VolumeControlButton;

/**
 * 
 * @author Ken Larson
 * 
 */
public class SkinTwo implements Skin {
	public Component createStartButton() {
		return new StartButton();
	}

	public Component createStopButton() {
		return new StopButton();
	}

	public Component createFastForwardButton() {
		return new FastForwardButton();
	}

	public Component createGainMeterButton() {
		return new GainMeterButton();
	}

	public Component createPauseButton() {
		return new PauseButton();
	}

	public Component createProgressSlider() {
		return new ProgressSlider();
	}

	public Component createReverseButton() {
		return new ReverseButton();
	}

	public AbstractButton createVolumeControlButton_Increase() {
		return new VolumeControlButton(VolumeControlButton.INCREASE);
	}

	public AbstractButton createVolumeControlButton_Decrease() {
		return new VolumeControlButton(VolumeControlButton.DECREASE);
	}
}
