package net.sf.fmj.ejmf.toolkit.gui.controls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.media.Controller;
import javax.media.Time;

/*
 * Stop Control for StandardControlPanel.
 */
public class StandardStopControl extends ActionListenerControl {

	public StandardStopControl(Skin skin) {
		super(skin);
		getControlComponent().setEnabled(false);
	}

	public StandardStopControl(Skin skin, Controller controller) {
		super(skin, controller);
		getControlComponent().setEnabled(false);
	}

	/**
	 * Create StopButton
	 * 
	 * @see net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf.StopButton
	 */
	@Override
	protected Component createControlComponent(Skin skin) {
		return skin.createStopButton();
	}

	/**
	 * Create ActionListener to respond to StopButton clicks.
	 */
	protected EventListener createControlListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Controller controller = getController();
				controller.stop();
				controller.setMediaTime(new Time(0.0));
			}
		};
	}
}
