package net.sf.fmj.ejmf.toolkit.gui.controls;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * 
 * @author Ken Larson
 * 
 */
public class BasicIconButton extends JButton implements SwingConstants {

	private final ImageIcon icon;
	private final ImageIcon disabledIcon;
	private final Dimension size;

	public BasicIconButton(ImageIcon icon, ImageIcon disabledIcon) {
		// setBackground(UIManager.getColor("control"));
		this.icon = icon;
		this.disabledIcon = disabledIcon;
		this.size = new Dimension(icon.getIconWidth(), icon.getIconHeight());
	}

	public void paint(Graphics g) {
		final boolean isPressed = getModel().isPressed();
		final boolean isEnabled = isEnabled();

		g.setColor(UIManager.getColor("control"));
		g.fillRect(0, 0, size.width, size.height);
		if (isEnabled)
			g.drawImage(icon.getImage(), 0, 0, size.width, size.height, null);
		else
			g.drawImage(disabledIcon.getImage(), 0, 0, size.width, size.height,
					null);
	}

	public Dimension getPreferredSize() {
		return size;
	}

	/**
	 * Don't let button get so small that icon is unrecognizable.
	 */
	public Dimension getMinimumSize() {
		return size;
	}

	/**
	 * Always return false.
	 */
	public boolean isFocusTraversable() {
		return false;
	}
}
