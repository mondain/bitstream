package net.sf.fmj.ejmf.toolkit.gui.controls;

import java.awt.GridLayout;

import javax.swing.AbstractButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * A panel containing two buttons for manipulating gain.
 * <p>
 * Subclasses provide buttons but supplying definitions of the following two
 * methods:
 * <p>
 * AbstractButton createGainIncreaseButton(); AbstractButton
 * createGainDecreaseButton();
 */

public abstract class AbstractGainButtonPanel extends JPanel {
	protected AbstractButton gainIncreaseButton;
	protected AbstractButton gainDecreaseButton;

	public AbstractGainButtonPanel(Skin skin) {
		GridLayout grid;
		setLayout(grid = new GridLayout(2, 1));
		grid.setVgap(0);
		grid.setHgap(0);
		setBorder(new EmptyBorder(0, 0, 0, 0));
		add(gainIncreaseButton = createGainIncreaseButton(skin));
		add(gainDecreaseButton = createGainDecreaseButton(skin));
	}

	/**
	 * Get button repsonsible for increasing gain
	 * 
	 * @return An AbstractButton
	 */
	public AbstractButton getGainIncreaseButton() {
		return gainIncreaseButton;
	}

	/**
	 * Get button repsonsible for decreasing gain
	 * 
	 * @return An AbstractButton
	 */
	public AbstractButton getGainDecreaseButton() {
		return gainDecreaseButton;
	}

	/**
	 * Create a button for increasing gain.
	 * 
	 * @return An AbstractButton
	 */
	protected abstract AbstractButton createGainIncreaseButton(Skin skin);

	/**
	 * Create a button for decreasing gain.
	 */
	protected abstract AbstractButton createGainDecreaseButton(Skin skin);
}
