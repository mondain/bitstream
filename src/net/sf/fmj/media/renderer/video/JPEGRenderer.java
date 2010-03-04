package net.sf.fmj.media.renderer.video;

import java.awt.Component;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.JPEGFormat;
import javax.media.format.VideoFormat;
import javax.media.renderer.VideoRenderer;

import net.sf.fmj.media.AbstractVideoRenderer;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * 
 * @author Ken Larson
 * 
 */
public class JPEGRenderer extends AbstractVideoRenderer implements
		VideoRenderer {
	private static final Logger logger = LoggerSingleton.logger;

	private boolean scale;

	private final Format[] supportedInputFormats = new Format[] { new JPEGFormat() };

	// @Override
	public String getName() {
		return "JPEG Renderer";
	}

	// @Override
	public Format[] getSupportedInputFormats() {
		return supportedInputFormats;
	}

	private JVideoComponent component = new JVideoComponent();

	public Component getComponent() {
		return component;
	}

	private Object[] controls = new Object[] { this };

	public Object[] getControls() {
		return controls;
	}

	// @Override
	public Format setInputFormat(Format format) {
		VideoFormat chosenFormat = (VideoFormat) super.setInputFormat(format);
		if (chosenFormat != null) {
			getComponent().setPreferredSize(chosenFormat.getSize());
		}
		return chosenFormat;
	}

	@Override
	public int doProcess(Buffer buffer) {
		if (buffer.getData() == null)
			return BUFFER_PROCESSED_FAILED; // TODO: check for EOM?

		if (buffer.isDiscard()) {
			logger.warning("JPEGRenderer passed buffer with discard flag");
			return BUFFER_PROCESSED_FAILED;
		}

		final java.awt.Image image;
		try {
			image = ImageIO.read(new ByteArrayInputStream((byte[]) buffer
					.getData(), buffer.getOffset(), buffer.getLength()));
		} catch (IOException e) {
			logger.log(Level.WARNING, "" + e, e);
			return BUFFER_PROCESSED_FAILED;
		}

		if (image == null) {
			logger.log(Level.WARNING,
					"Failed to read image (ImageIO.read returned null).");
			return BUFFER_PROCESSED_FAILED;
		}

		try {
			component.setImage(image);
		} catch (Exception e) {
			logger.log(Level.WARNING, "" + e, e);
			return BUFFER_PROCESSED_FAILED;
		}
		return BUFFER_PROCESSED_OK;
	}
}
