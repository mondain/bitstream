package net.sf.fmj.ejmf.toolkit.gui.controls;

import javax.swing.AbstractButton;

/**
 * Provides up/down arrow buttons for increasing/decreasing Player gain. This
 * panel is used by StandardGainControl.
 */

class StandardGainButtonPanel extends AbstractGainButtonPanel {

	public StandardGainButtonPanel(Skin skin) {
		super(skin);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create button for increasing gain.
	 * 
	 * @return An AbstractButton that acts as gain increase control.
	 */
	public AbstractButton createGainIncreaseButton(Skin skin) {
		return (skin.createVolumeControlButton_Increase());
	}

	/**
	 * Create button for decreasing gain.
	 * 
	 * @return An AbstractButton that acts as gain decrease control.
	 */
	public AbstractButton createGainDecreaseButton(Skin skin) {
		return (skin.createVolumeControlButton_Decrease());
	}
}
