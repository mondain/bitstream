package net.sf.fmj.media.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;

/**
 * TODO: need to take into account line stride, if it is different from what one
 * would expect.
 * 
 * @author Ken Larson
 * 
 */
public class ImageToBuffer {
	public ImageToBuffer() {
		super();
		// no reason to ever instantiate this
	}

	public static Buffer createBuffer(java.awt.Image image, float frameRate) {
		final BufferedImage bi;
		if (image instanceof BufferedImage) {
			bi = (BufferedImage) image;
		} else {
			bi = convert(image);
		}

		final DataBuffer dataBuffer = bi.getRaster().getDataBuffer();

		final Object pixels;
		final int pixelsLength;
		final Class dataType;

		if (dataBuffer instanceof DataBufferInt) {
			final int[] intPixels = ((DataBufferInt) dataBuffer).getData();
			pixels = intPixels;
			pixelsLength = intPixels.length;
			dataType = Format.intArray;
		} else if (dataBuffer instanceof DataBufferByte) {
			final byte[] bytePixels = ((DataBufferByte) dataBuffer).getData();
			pixels = bytePixels;
			pixelsLength = bytePixels.length;
			dataType = Format.byteArray;
		} else {
			throw new IllegalArgumentException(
					"Unknown or unsupported data buffer type: " + dataBuffer);
		}

		final int bufferedImageType = bi.getType();

		final Buffer result = new Buffer();
		final Dimension size = new Dimension(bi.getWidth(), bi.getHeight());
		final int maxDataLength = -1; // TODO
		final int bitsPerPixel;

		final int red;
		final int green;
		final int blue;

		if (bufferedImageType == BufferedImage.TYPE_3BYTE_BGR) {
			bitsPerPixel = 24;
			red = 1;
			green = 2;
			blue = 3;
		} else if (bufferedImageType == BufferedImage.TYPE_INT_BGR) {
			bitsPerPixel = 32;
			// TODO: test
			red = 0xFF;
			green = 0xFF00;
			blue = 0xFF0000;
		} else if (bufferedImageType == BufferedImage.TYPE_INT_RGB) {
			bitsPerPixel = 32;
			red = 0xFF0000;
			green = 0xFF00;
			blue = 0xFF;
		} else if (bufferedImageType == BufferedImage.TYPE_INT_ARGB) {
			bitsPerPixel = 32;
			red = 0xFF0000;
			green = 0xFF00;
			blue = 0xFF;
			// just ignore alpha
		} else
			throw new IllegalArgumentException(
					"Unsupported buffered image type: " + bufferedImageType);

		result.setFormat(new RGBFormat(size, maxDataLength, dataType,
				frameRate, bitsPerPixel, red, green, blue));
		result.setData(pixels);
		result.setLength(pixelsLength);
		result.setOffset(0);

		return result;
	}

	private static BufferedImage convert(Image im) {
		BufferedImage bi = new BufferedImage(im.getWidth(null), im
				.getHeight(null), BufferedImage.TYPE_INT_RGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(im, 0, 0, null);
		bg.dispose();
		return bi;
	}
}
