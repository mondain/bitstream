package net.sf.fmj.media.renderer.video;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.renderer.VideoRenderer;
import javax.swing.JComponent;

import net.sf.fmj.media.AbstractVideoRenderer;
import net.sf.fmj.media.util.BufferToImage;
import net.sf.fmj.utility.FPSCounter;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * 
 * The simplest possible Swing Renderer. Same as SimpleAWTRenderer, but uses a
 * JComponent
 * 
 * @author Ken Larson
 * 
 */
public class SimpleSwingRenderer extends AbstractVideoRenderer implements
		VideoRenderer {
	private static final Logger logger = LoggerSingleton.logger;

	private static final boolean PAINT_IMMEDIATELY = false;
	private static final boolean TRACE_FPS = false;

	private final Format[] supportedInputFormats = new Format[] {
	// RGB, 32-bit, Masks=16711680:65280:255, LineStride=-1, class [I
			new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff0000,
					0xff00, 0xff, 1, -1, 0, -1),

			// RGB, 32-bit, Masks=255:65280:16711680, LineStride=-1, class [I
			new RGBFormat(null, -1, Format.intArray, -1.0f, 32, 0xff, 0xff00,
					0xff0000, 1, -1, 0, -1),

			new RGBFormat(null, -1, Format.byteArray, -1.0f, 32, 1, 2, 3),

			new RGBFormat(null, -1, Format.byteArray, -1.0f, 32, 3, 2, 1),

			new RGBFormat(null, -1, Format.byteArray, -1.0f, 24, 1, 2, 3),

			new RGBFormat(null, -1, Format.byteArray, -1.0f, 24, 3, 2, 1)

	};

	// @Override
	public String getName() {
		return "Simple Swing Renderer";
	}

	// @Override
	public Format[] getSupportedInputFormats() {
		return supportedInputFormats;
	}

	private SwingVideoComponent component = new SwingVideoComponent();

	public Component getComponent() {
		return component;
	}

	private Object[] controls = new Object[] { this };

	public Object[] getControls() {
		return controls;
	}

	private BufferToImage bufferToImage;

	// @Override
	public Format setInputFormat(Format format) {
		// logger.fine("FORMAT: " + MediaCGUtils.formatToStr(format));
		// TODO: check VideoFormat and compatibility
		bufferToImage = new BufferToImage((VideoFormat) format);
		return super.setInputFormat(format);
	}

	private final FPSCounter fpsCounter = new FPSCounter();

	@Override
	public int doProcess(Buffer buffer) {
		// if (buffer.isDiscard())
		// return BUFFER_PROCESSED_OK; // TODO: where do we check for this?

		if (buffer.getData() == null) {
			return BUFFER_PROCESSED_FAILED; // TODO: check for EOM?
		}

		java.awt.Image image = bufferToImage.createImage(buffer);
		component.setImage(image);

		if (TRACE_FPS) {
			fpsCounter.nextFrame();
			if (fpsCounter.getNumFrames() >= 50) {
				System.out.println(fpsCounter);
				fpsCounter.reset();
			}
		}

		return BUFFER_PROCESSED_OK;
	}

	/**
	 * Component used for rendering video images.
	 */
	private class SwingVideoComponent extends JComponent {
		private Image image;

		public SwingVideoComponent() {
			setDoubleBuffered(false);
		}

		public void setImage(Image image) {
			this.image = image;
			if (PAINT_IMMEDIATELY)
				paintImmediately(getVideoRect(scale)); // TODO: this has to be
														// done in swing thread.
			else
				repaint();

		}

		public Dimension getPreferredSize() {
			if (inputFormat == null) {
				return super.getPreferredSize();
			}
			VideoFormat videoFormat = (VideoFormat) inputFormat;
			return videoFormat.getSize();
		}

		private Rectangle getVideoRect(final boolean scale) {
			final int x, y;
			final int w, h;
			final Dimension preferredSize = getPreferredSize();
			final Dimension size = getSize();

			if (!scale) {
				if (preferredSize.width <= size.width) {
					x = (size.width - preferredSize.width) / 2;
					w = preferredSize.width;
				} else {
					x = 0;
					w = preferredSize.width;
				}

				if (preferredSize.height <= size.height) {
					y = (size.height - preferredSize.height) / 2;
					h = preferredSize.height;
				} else {
					y = 0;
					h = preferredSize.height;
				}
			} else {
				if ((float) size.width / preferredSize.width < (float) size.height
						/ preferredSize.height) {
					w = size.width;
					h = size.width * preferredSize.height / preferredSize.width;
					x = 0;
					y = (size.height - h) / 2;
				} else {
					w = size.height * preferredSize.width
							/ preferredSize.height;
					h = size.height;
					x = (size.width - w) / 2;
					y = 0;
				}
			}
			return new Rectangle(x, y, w, h);
		}

		private boolean scale = true;
		private BufferedImage biCompatible;

		public void paint(Graphics g) {
			if (image != null) {
				Rectangle rect = getVideoRect(scale);

				// long start = System.currentTimeMillis();

				try {
					if (biCompatible == null) // try drawing directly, unless we
												// already know that won't work
												// (biCompatible is set)
					{
						g.drawImage(image, rect.x, rect.y, rect.width,
								rect.height, null);
						return;
					}
				} catch (java.awt.image.ImagingOpException e) {
					// some images do not seem to be able to be scaled directly.
					// this appears to only happen when the image has a
					// ComponentColorModel.
					// the graphics destination appears to use a
					// DirectColorModel, and for scaling,
					// they are incompatible. Civil uses ComponentColorModel in
					// many cases, so
					// this is generally when we have this problem.

					// fall through and convert manually.

					// no idea why AWT/Java2d doesn't just do this for us behind
					// the scenes.
				}

				getCompatibleBufferedImage();
				biCompatible.getGraphics().drawImage(image, 0, 0,
						image.getWidth(null), image.getHeight(null), null);

				g.drawImage(biCompatible, rect.x, rect.y, rect.width,
						rect.height, null);

			}
			// else {
			// Dimension size = getSize();
			// g.setColor(getBackground());
			// g.fillRect(0, 0, size.width, size.height);
			// }
		}

		private BufferedImage getCompatibleBufferedImage() {
			if (biCompatible == null
					|| biCompatible.getWidth() != image.getWidth(null)
					|| biCompatible.getHeight() != image.getHeight(null))
				biCompatible = this.getGraphicsConfiguration()
						.createCompatibleImage(image.getWidth(null),
								image.getHeight(null));
			return biCompatible;
		}

		public void update(Graphics g) {
			paint(g);
		}
	}

}
