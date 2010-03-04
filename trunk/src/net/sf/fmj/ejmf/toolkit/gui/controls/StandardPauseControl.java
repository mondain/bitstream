package net.sf.fmj.ejmf.toolkit.gui.controls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

import javax.media.Controller;

/**
 * Pause Control for StandardControlPanel.
 */

public class StandardPauseControl extends ActionListenerControl {

	/**
	 * Create a StandardPauseControl and associate it with a Controller.
	 * 
	 * @param controller
	 *            A Controller with which control is associated.
	 */
	public StandardPauseControl(Skin skin, Controller controller) {
		super(skin);
		getControlComponent().setEnabled(false);
	}

	/** Create a StandardPauseControl */
	public StandardPauseControl(Skin skin) {
		super(skin);
		getControlComponent().setEnabled(false);
	}

	/**
	 * Create PauseButton.
	 * 
	 * @return The component that acts as pause button.
	 * @see net.sf.fmj.ejmf.toolkit.gui.controls.skins.ejmf.PauseButton
	 */
	@Override
	protected Component createControlComponent(Skin skin) {
		return skin.createPauseButton();
	}

	/**
	 * Create and return an ActionListener that implements pause semantics.
	 * 
	 * @return An ActionListener for pausing controller.
	 */
	protected EventListener createControlListener() {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getController().stop();
			}
		};
	}
}
