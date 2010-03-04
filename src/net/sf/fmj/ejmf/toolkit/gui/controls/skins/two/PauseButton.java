package net.sf.fmj.ejmf.toolkit.gui.controls.skins.two;

import javax.swing.ImageIcon;

import net.sf.fmj.ejmf.toolkit.gui.controls.BasicIconButton;

/**
 * 
 * @author Ken Larson
 * 
 */
public class PauseButton extends BasicIconButton {
	public PauseButton() {
		super(new ImageIcon(StartButton.class
				.getResource("resources/control_pause_blue.png")),
				new ImageIcon(StartButton.class
						.getResource("resources/control_pause.png")));
	}
}
