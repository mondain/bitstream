package net.sf.fmj.media.renderer.video;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.ResourceUnavailableException;
import javax.media.format.RGBFormat;
import javax.media.renderer.VideoRenderer;

/**
 * A VideoRenderer that handles packed integer RGB formats.
 * 
 * It uses Java2D to render incoming RGB images.
 * 
 * TODO: support byte-array based images. See ImageToBuffer.
 * 
 * @author Warren Bloomer
 * 
 */
public class Java2dRenderer implements VideoRenderer {

	private String name = "Java2D Video Renderer";

	/** supported input formats */
	private Format[] supportedFormats;

	/** the actual input format */
	private RGBFormat inputFormat;

	/** the visual component to render video into */
	private JVideoComponent component;

	/** bounds for the input frame */
	private Rectangle bounds = new Rectangle(0, 0, 10, 10);

	/** an offscreen image to write to */
	private BufferedImage bufferedImage;

	/**
	 * Constructor
	 */
	public Java2dRenderer() {

		int rMask = 0x00FF0000;
		int gMask = 0x0000FF00;
		int bMask = 0x000000FF;

		supportedFormats = new Format[] {

		// RGB format
				new RGBFormat(null, // size
						Format.NOT_SPECIFIED, // maxDataLength
						Format.intArray, // buffer type
						Format.NOT_SPECIFIED, // frame rate
						32, // bitsPerPixel
						rMask, gMask, bMask, // component masks
						1, // pixel stride
						Format.NOT_SPECIFIED, // line stride
						Format.FALSE, // flipped
						Format.NOT_SPECIFIED // endian
				),

				// BGR format
				new RGBFormat(null, // size
						Format.NOT_SPECIFIED, // maxDataLength
						Format.intArray, // buffer type
						Format.NOT_SPECIFIED, // frame rate
						32, // bitsPerPixel
						bMask, gMask, rMask, // component masks
						1, // pixel stride
						Format.NOT_SPECIFIED, // line stride
						Format.FALSE, // flipped
						Format.NOT_SPECIFIED // endian
				) };

	}

	/*------------------- PlugIn interface ------------------- */

	/**
	 * Return the name of the plugin
	 */
	public String getName() {
		return name;
	}

	/**
	 * Opens the plugin
	 */
	public void open() throws ResourceUnavailableException {
		createImage();
	}

	/**
	 * Resets the state of the plug-in. Typically at end of media or when media
	 * is repositioned.
	 */
	public void reset() {
		// Nothing to do
	}

	/**
	 * Close the plugin.
	 */
	public synchronized void close() {
		bufferedImage = null;
	}

	/*------------------------- Controls interface ------------------------ */

	/**
	 * Returns an array of supported controls
	 **/
	public Object[] getControls() {
		return new Object[] {};
	}

	/**
	 * Return the control based on a control type for the PlugIn.
	 */
	public Object getControl(String controlType) {
		return null;
	}

	/* ----------------------- Renderer interface -------------------------- */

	/**
	 * Start the renderer.
	 */
	public void start() {
		// nothing to do
	}

	/**
	 * Stop the renderer.
	 */
	public void stop() {
		// nothing to do
	}

	/**
	 * Lists the possible input formats supported by this plug-in.
	 */
	public Format[] getSupportedInputFormats() {
		return supportedFormats;
	}

	/**
	 * Set the data input format.
	 */
	public Format setInputFormat(Format format) {

		for (int i = 0; i < supportedFormats.length; i++) {
			if (format.matches(supportedFormats[i])) {
				this.inputFormat = (RGBFormat) format;
				Dimension size = inputFormat.getSize();
				if (size != null) {
					this.bounds.setSize(size);
				}
				getComponent().setPreferredSize(size);

				return format;
			}
		}
		return null;
	}

	/**
	 * Processes the data and renders it to a component
	 */
	public int process(Buffer buffer) {

		if (component == null) {
			return BUFFER_PROCESSED_FAILED;
		}

		Format inFormat = buffer.getFormat();
		if (inFormat == null) {
			return BUFFER_PROCESSED_FAILED;
		}

		if (inFormat != inputFormat || !inFormat.equals(inputFormat)) {
			// format has changed
			if (setInputFormat(inFormat) != null) {
				return BUFFER_PROCESSED_FAILED;
			}
			// recreate bufferedImage
			createImage();
		}

		Object data = buffer.getData();

		if (data == null) {
			return BUFFER_PROCESSED_FAILED;
		}
		if (inFormat.getDataType() != Format.intArray) {
			// wrong data type
			return BUFFER_PROCESSED_FAILED;
		}

		Dimension size = inputFormat.getSize();

		synchronized (component) {
			// write data to buffered image
			bufferedImage.getRaster().setDataElements(0, 0, size.width,
					size.height, data);

			// repaint the component
			component.setImage(bufferedImage);
		}

		return BUFFER_PROCESSED_OK;
	}

	/* ---------------------- VideoRenderer interface ---------------------- */

	/**
	 * Try to set the component to render to.
	 */
	public boolean setComponent(Component comp) {
		return false;
	}

	/**
	 * Get the visual component.
	 */
	public Component getComponent() {
		if (component == null) {
			component = new JVideoComponent();
		}

		return component;
	}

	/**
	 * Get the bounds of the component.
	 */
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Set the bounds of the visual component.
	 */
	public void setBounds(Rectangle rect) {
		this.bounds.setBounds(rect);
	}

	/* -------------------- private utilities ----------------------- */

	/**
	 * Create the buffered image to write raster data to.
	 */
	private void createImage() {
		if (inputFormat == null) {
			// can not create image
			return;
		}

		Dimension size = inputFormat.getSize();
		if (size == null) {
			// can not create image
			return;
		}

		// look at RGB masks to determine image type.
		int imageType;
		if (inputFormat.getRedMask() == 0x000000FF) {
			imageType = BufferedImage.TYPE_INT_BGR;
		} else {
			imageType = BufferedImage.TYPE_INT_RGB;
		}

		bufferedImage = new BufferedImage(size.width, size.height, imageType);
	}

}
