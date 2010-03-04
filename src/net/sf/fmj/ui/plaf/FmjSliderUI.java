package net.sf.fmj.ui.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSliderUI;

import net.sf.fmj.ui.control.FmjSlider;
import net.sf.fmj.ui.images.Images;

/**
 * A slider look-and-feel that is nice(ish)
 * 
 * @author Warren Bloomer
 * 
 */
public class FmjSliderUI extends BasicSliderUI {

	private final ImageIcon vertIcon;
	private final ImageIcon horizIcon;

	private Dimension vertIconSize = new Dimension(20, 20);
	private Dimension horizIconSize = new Dimension(20, 20);

	public static ComponentUI createUI(JComponent component) {
		return new FmjSliderUI((JSlider) component);
	}

	private FmjSliderUI(JSlider component) {
		super(component);

		horizIcon = Images.get(Images.SLIDER_THUMB_HORIZ);
		horizIconSize = new Dimension(horizIcon.getIconWidth(), horizIcon
				.getIconHeight());

		vertIcon = Images.get(Images.SLIDER_THUMB_VERT);
		vertIconSize = new Dimension(vertIcon.getIconWidth(), vertIcon
				.getIconHeight());
	}

	public void installUI(JComponent c) {
		super.installUI(c);
	}

	public void paintThumb(Graphics g) {
		// super.paintThumb(g);

		Graphics2D g2d = (Graphics2D) g;

		int orientation = slider.getOrientation();

		Image image;

		switch (orientation) {
		case SwingConstants.VERTICAL:
			image = vertIcon.getImage();
			break;

		case SwingConstants.HORIZONTAL:
		default:
			image = horizIcon.getImage();
		}

		g2d.drawImage(image, thumbRect.x, thumbRect.y, thumbRect.width,
				thumbRect.height, null);
	}

	public void paintTrack(Graphics g) {
		// super.paintTrack(g);

		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.LIGHT_GRAY);

		// logger.fine("trackLength: " + getTrackLength() + " + trackRect: " +
		// trackRect);
		// logger.fine("thumbOverhang: " + getThumbOverhang() +
		// ", trackBuffer: " + trackBuffer);

		float x = trackRect.x;
		float y = trackRect.y;
		float w = trackRect.width;
		float h = trackRect.height;

		int orientation = slider.getOrientation();
		if (orientation == SwingConstants.HORIZONTAL) {
			x -= trackBuffer;
			w += (2 * trackBuffer) - 1;
		} else {
			y -= trackBuffer;
			h += (2 * trackBuffer) - 1;
		}

		// if ()
		float arcw = 20;
		float arch = 20;
		RoundRectangle2D rectangle = new RoundRectangle2D.Float(x, y, w, h,
				arcw, arch);

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.fill(rectangle);

		g2d.setColor(getShadowColor());
		if (orientation == SwingConstants.HORIZONTAL) {
			rectangle.setFrame(x, y, w - 1, h - 1);
		} else {
			rectangle.setFrame(x, y, w - 1, h - 1);
		}
		g2d.draw(rectangle);
	}

	public void paintFocus(Graphics g) {
		if (((FmjSlider) slider).getPaintFocus()) {
			super.paintFocus(g);
		}
	}

	protected Dimension getThumbSize() {
		int orientation = slider.getOrientation();
		if (orientation == SwingConstants.HORIZONTAL) {
			// horizThumbIcon = horizIcon;
			return horizIconSize;
		} else {
			// vertThumbIcon = vertIcon;
			return vertIconSize;
		}
	}
}
