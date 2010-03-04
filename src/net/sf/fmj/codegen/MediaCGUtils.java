package net.sf.fmj.codegen;

import java.util.ArrayList;
import java.util.List;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.PlugIn;
import javax.media.format.AudioFormat;
import javax.media.format.H261Format;
import javax.media.format.H263Format;
import javax.media.format.IndexedColorFormat;
import javax.media.format.JPEGFormat;
import javax.media.format.RGBFormat;
import javax.media.format.VideoFormat;
import javax.media.format.YUVFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.FileTypeDescriptor;

import net.sf.fmj.utility.StringUtils;

/**
 * Code generation utilities for JMF classes, useful for constructing unit
 * tests.
 * 
 * @author Ken Larson
 * 
 */
public class MediaCGUtils {
	public static String formatToStr(Format f) {
		if (f == null)
			return "null";
		final Class c = f.getClass();
		if (c == RGBFormat.class) {
			final RGBFormat o = (RGBFormat) f;
			return "new RGBFormat(" + toLiteral(o.getSize()) + ", "
					+ o.getMaxDataLength() + ", "
					+ dataTypeToStr(o.getDataType()) + ", "
					+ CGUtils.toLiteral(o.getFrameRate()) + ", "
					+ o.getBitsPerPixel() + ", "
					+ CGUtils.toHexLiteral(o.getRedMask()) + ", "
					+ CGUtils.toHexLiteral(o.getGreenMask()) + ", "
					+ CGUtils.toHexLiteral(o.getBlueMask()) + ", "
					+ o.getPixelStride() + ", " + o.getLineStride() + ", "
					+ o.getFlipped() + ", " + o.getEndian() + ")";

		} else if (c == YUVFormat.class) {

			final YUVFormat o = (YUVFormat) f;
			return "new YUVFormat(" + toLiteral(o.getSize()) + ", "
					+ o.getMaxDataLength() + ", "
					+ dataTypeToStr(o.getDataType()) + ", "
					+ CGUtils.toLiteral(o.getFrameRate())
					+ ", "
					+ o.getYuvType()
					+ // TODO: use constants
					", " + o.getStrideY() + ", " + o.getStrideUV() + ", "
					+ o.getOffsetY() + ", " + o.getOffsetU() + ", "
					+ o.getOffsetV() + ")";

		} else if (c == JPEGFormat.class) {

			final JPEGFormat o = (JPEGFormat) f;
			return "new JPEGFormat(" + toLiteral(o.getSize()) + ", "
					+ o.getMaxDataLength() + ", "
					+ dataTypeToStr(o.getDataType()) + ", "
					+ CGUtils.toLiteral(o.getFrameRate()) + ", "
					+ o.getQFactor() + ", " + o.getDecimation() + ")";

		} else if (c == IndexedColorFormat.class) {

			final IndexedColorFormat o = (IndexedColorFormat) f;
			return "new IndexedColorFormat(" + toLiteral(o.getSize()) + ", "
					+ o.getMaxDataLength() + ", "
					+ dataTypeToStr(o.getDataType())
					+ ", "
					+ CGUtils.toLiteral(o.getFrameRate())
					+ ", "
					+ o.getLineStride()
					+ // TODO: use constants
					", " + o.getMapSize() + ", "
					+ CGUtils.toLiteral(o.getRedValues()) + ", "
					+ CGUtils.toLiteral(o.getGreenValues()) + ", "
					+ CGUtils.toLiteral(o.getBlueValues()) + ")";

		} else if (c == H263Format.class) {

			final H263Format o = (H263Format) f;
			return "new H263Format(" + toLiteral(o.getSize()) + ", "
					+ o.getMaxDataLength() + ", "
					+ dataTypeToStr(o.getDataType()) + ", "
					+ CGUtils.toLiteral(o.getFrameRate()) + ", "
					+ o.getAdvancedPrediction() + ", "
					+ o.getArithmeticCoding() + ", " + o.getErrorCompensation()
					+ ", " + o.getHrDB() + ", " + o.getPBFrames() + ", "
					+ o.getUnrestrictedVector() + ")";

		} else if (c == H261Format.class) {

			final H261Format o = (H261Format) f;
			return "new H261Format(" + toLiteral(o.getSize()) + ", "
					+ o.getMaxDataLength() + ", "
					+ dataTypeToStr(o.getDataType()) + ", "
					+ CGUtils.toLiteral(o.getFrameRate()) + ", "
					+ o.getStillImageTransmission() + ")";

		} else if (c == AudioFormat.class) {
			final AudioFormat o = (AudioFormat) f;
			return "new AudioFormat(" + CGUtils.toLiteral(o.getEncoding())
					+ ", " + CGUtils.toLiteral(o.getSampleRate()) + ", "
					+ CGUtils.toLiteral(o.getSampleSizeInBits()) + ", "
					+ CGUtils.toLiteral(o.getChannels()) + ", "
					+ CGUtils.toLiteral(o.getEndian()) + ", "
					+ CGUtils.toLiteral(o.getSigned()) + ", "
					+ CGUtils.toLiteral(o.getFrameSizeInBits()) + ", "
					+ CGUtils.toLiteral(o.getFrameRate()) + ", "
					+ dataTypeToStr(o.getDataType()) + ")";

		} else if (c == VideoFormat.class) {

			final VideoFormat o = (VideoFormat) f;
			return "new VideoFormat(" + CGUtils.toLiteral(o.getEncoding())
					+ ", " + toLiteral(o.getSize()) + ", "
					+ o.getMaxDataLength() + ", "
					+ dataTypeToStr(o.getDataType()) + ", "
					+ CGUtils.toLiteral(o.getFrameRate()) + ")";

		} else if (c == Format.class) {

			final Format o = (Format) f;
			return "new Format(" + CGUtils.toLiteral(o.getEncoding()) + ", "
					+ dataTypeToStr(o.getDataType()) + ")";

		} else if (c == FileTypeDescriptor.class) {
			final FileTypeDescriptor o = (FileTypeDescriptor) f;
			return "new FileTypeDescriptor("
					+ CGUtils.toLiteral(o.getEncoding()) + ")";
		} else if (c == ContentDescriptor.class) {
			final ContentDescriptor o = (ContentDescriptor) f;
			return "new ContentDescriptor("
					+ CGUtils.toLiteral(o.getEncoding()) + ")";
		} else if (c == com.sun.media.format.WavAudioFormat.class) {
			// TODO: are the parameters correct?
			final com.sun.media.format.WavAudioFormat o = (com.sun.media.format.WavAudioFormat) f;
			return "new com.sun.media.format.WavAudioFormat("
					+ CGUtils.toLiteral(o.getEncoding())
					+ ", "
					+ CGUtils.toLiteral(o.getSampleRate())
					+
					// ", -1" + // int arg TODO - what is this?
					", " + CGUtils.toLiteral(o.getSampleSizeInBits()) + ", "
					+ CGUtils.toLiteral(o.getChannels()) + ", "
					+ CGUtils.toLiteral(o.getFrameSizeInBits()) + ", "
					+ CGUtils.toLiteral(o.getAverageBytesPerSecond()) + ", "
					+ CGUtils.toLiteral(o.getEndian()) + ", "
					+ CGUtils.toLiteral(o.getSigned()) + ", "
					+ CGUtils.toLiteral((float) o.getFrameRate()) + ", "
					+ dataTypeToStr(o.getDataType()) + ", "
					+ CGUtils.toLiteral(o.getCodecSpecificHeader()) + ")";
		} else {
			throw new IllegalArgumentException("" + f.getClass());
			// System.err.println(f.getClass());
			// return "(" + CGUtils.toNameDotClass(f.getClass()) + ") " +
			// "null " + "/*" + f + "*/";
		}
	}

	public static String toLiteral(java.awt.Dimension size) {
		if (size == null)
			return "null";
		else
			return "new java.awt.Dimension(" + size.width + ", " + size.height
					+ ")";
	}

	public static String dataTypeToStr(Class dataType) {
		if (dataType == null)
			return "null";
		else if (dataType == Format.byteArray)
			return "Format.byteArray";
		else if (dataType == Format.shortArray)
			return "Format.shortArray";
		if (dataType == Format.intArray)
			return "Format.intArray";
		else
			throw new IllegalArgumentException();
	}

	public static String plugInResultToStr(int result) {

		switch (result) {
		case PlugIn.BUFFER_PROCESSED_OK:
			return "BUFFER_PROCESSED_OK";
		case PlugIn.BUFFER_PROCESSED_FAILED:
			return "BUFFER_PROCESSED_FAILED";
		case PlugIn.INPUT_BUFFER_NOT_CONSUMED:
			return "INPUT_BUFFER_NOT_CONSUMED";
		case PlugIn.OUTPUT_BUFFER_NOT_FILLED:
			return "OUTPUT_BUFFER_NOT_FILLED";
		case PlugIn.PLUGIN_TERMINATED:
			return "PLUGIN_TERMINATED";
		default:
			return "" + result;
		}
	}

	// TODO: move to another class. Not used for code generation.
	public static String bufferToStr(Buffer buffer) {
		if (buffer == null)
			return "null";
		StringBuffer b = new StringBuffer();
		b.append(buffer);

		b.append(" seq=" + buffer.getSequenceNumber());
		b.append(" off=" + buffer.getOffset());
		b.append(" len=" + buffer.getLength());
		b.append(" flags=[" + bufferFlagsToStr(buffer.getFlags()) + "]");
		b.append(" fmt=[" + buffer.getFormat() + "]");
		if (buffer.getData() != null && buffer.getData() instanceof byte[])
			b.append(" data=["
					+ buffer.getData()
					+ " "
					+ StringUtils.byteArrayToHexString((byte[]) buffer
							.getData(), buffer.getLength(), buffer.getOffset())
					+ "]");
		else if (buffer.getData() != null)
			b.append(" data=[" + buffer.getData() + "]");
		else
			b.append(" data=[null]");

		return b.toString();
	}

	public static String bufferFlagsToStr(int flags) {
		List strings = new ArrayList();
		if ((flags & Buffer.FLAG_EOM) != 0)
			strings.add("FLAG_EOM");
		if ((flags & Buffer.FLAG_DISCARD) != 0)
			strings.add("FLAG_DISCARD");
		if ((flags & Buffer.FLAG_SILENCE) != 0)
			strings.add("FLAG_SILENCE");
		if ((flags & Buffer.FLAG_SID) != 0)
			strings.add("FLAG_SID");
		if ((flags & Buffer.FLAG_KEY_FRAME) != 0)
			strings.add("FLAG_KEY_FRAME");
		if ((flags & Buffer.FLAG_NO_WAIT) != 0)
			strings.add("FLAG_NO_WAIT");
		if ((flags & Buffer.FLAG_NO_SYNC) != 0)
			strings.add("FLAG_NO_SYNC");
		if ((flags & Buffer.FLAG_SYSTEM_TIME) != 0)
			strings.add("FLAG_SYSTEM_TIME");
		if ((flags & Buffer.FLAG_RELATIVE_TIME) != 0)
			strings.add("FLAG_RELATIVE_TIME");
		if ((flags & Buffer.FLAG_FLUSH) != 0)
			strings.add("FLAG_FLUSH");
		if ((flags & Buffer.FLAG_SYSTEM_MARKER) != 0)
			strings.add("FLAG_SYSTEM_MARKER");
		if ((flags & Buffer.FLAG_RTP_MARKER) != 0)
			strings.add("FLAG_RTP_MARKER");
		if ((flags & Buffer.FLAG_RTP_TIME) != 0)
			strings.add("FLAG_RTP_TIME");
		if ((flags & Buffer.FLAG_BUF_OVERFLOWN) != 0)
			strings.add("FLAG_BUF_OVERFLOWN");
		if ((flags & Buffer.FLAG_BUF_UNDERFLOWN) != 0)
			strings.add("FLAG_BUF_UNDERFLOWN");
		if ((flags & Buffer.FLAG_LIVE_DATA) != 0)
			strings.add("FLAG_LIVE_DATA");

		StringBuffer b = new StringBuffer();
		for (int i = 0; i < strings.size(); ++i) {
			if (b.length() != 0)
				b.append(" | ");
			b.append(strings.get(i));
		}
		return b.toString();

	}
}
