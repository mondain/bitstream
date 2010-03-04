package net.sf.fmj.media.codec.video.jpeg;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.media.Buffer;
import javax.media.Codec;
import javax.media.Format;
import javax.media.format.VideoFormat;

import net.sf.fmj.media.AbstractCodec;
import net.sf.fmj.media.util.BufferToImage;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * Interesting that JMF doesn't include such an encoder in cross-platform JMF.
 * 
 * @author Ken Larson
 * 
 */
public class JpegEncoder extends AbstractCodec implements Codec {
	private final Format[] supportedInputFormats = new Format[] {
			new VideoFormat(VideoFormat.RGB, null, -1, Format.byteArray, -1.0f),
			new VideoFormat(VideoFormat.RGB, null, -1, Format.intArray, -1.0f), };
	private final Format[] supportedOutputFormats = new Format[] { new VideoFormat(
			VideoFormat.JPEG, null, -1, Format.byteArray, -1.0f), };

	public Format[] getSupportedInputFormats() {
		return supportedInputFormats;
	}

	public Format[] getSupportedOutputFormats(Format input) {
		if (input == null)
			return supportedOutputFormats;
		VideoFormat inputCast = (VideoFormat) input;
		final Format[] result = new Format[] { new VideoFormat(
				VideoFormat.JPEG, inputCast.getSize(), -1, Format.byteArray,
				-1.0f) };

		return result;
	}

	private BufferToImage bufferToImage;

	public Format setInputFormat(Format format) {
		final VideoFormat videoFormat = (VideoFormat) format;
		if (videoFormat.getSize() == null)
			return null; // must set a size.
			// logger.fine("FORMAT: " + MediaCGUtils.formatToStr(format));
		// TODO: check VideoFormat and compatibility
		bufferToImage = new BufferToImage((VideoFormat) format);
		return super.setInputFormat(format);
	}

	public int process(Buffer input, Buffer output) {
		final BufferedImage image = (BufferedImage) bufferToImage
				.createImage(input);

		try {
			// TODO: this is very inefficient - it allocates a new byte array
			// (or more) every time

			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			final JPEGEncodeParam param = JPEGCodec
					.getDefaultJPEGEncodeParam(image);
			// TODO: trying to get good compression of safexmas.avi frames, but
			// they end up being
			// 10k each at 50% quality. JMF sends them at about 3k each with 74%
			// quality.
			// I think the reason is that JMF is probably encoding the YUV in
			// the jpeg, rather
			// than the 24-bit RGB that FMJ would use when using the ffmpeg-java
			// demux.

			// TODO: we should also use a JPEGFormat explicitly, and honor those
			// params.

			param.setQuality(0.74f, true);
			final JPEGImageEncoder jpeg = JPEGCodec
					.createJPEGEncoder(os, param);
			jpeg.encode(image);
			os.close();

			final byte[] ba = os.toByteArray();
			output.setData(ba);
			output.setOffset(0);
			output.setLength(ba.length);
			// System.out.println("Encoded jpeg to len: " + ba.length);
			return BUFFER_PROCESSED_OK;

		} catch (IOException e) {
			output.setDiscard(true);
			output.setLength(0);
			return BUFFER_PROCESSED_FAILED;
		}

	}
}
