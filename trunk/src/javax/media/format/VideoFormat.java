package javax.media.format;

import java.awt.Dimension;

import javax.media.Format;

import net.sf.fmj.utility.FormatUtils;

/**
 * Coding complete. Frame rate is in frames per second.
 * 
 * @author Ken Larson
 * 
 */
public class VideoFormat extends Format {

	protected Dimension size;
	protected int maxDataLength = NOT_SPECIFIED;
	protected float frameRate = NOT_SPECIFIED;

	// encodings:
	public static final String CINEPAK = "cvid";
	public static final String JPEG = "jpeg";
	public static final String JPEG_RTP = "jpeg/rtp";
	public static final String MPEG = "mpeg";
	public static final String MPEG_RTP = "mpeg/rtp";
	public static final String H261 = "h261";
	public static final String H261_RTP = "h261/rtp";
	public static final String H263 = "h263";
	public static final String H263_RTP = "h263/rtp";
	public static final String H263_1998_RTP = "h263-1998/rtp";
	public static final String RGB = "rgb";
	public static final String YUV = "yuv";
	public static final String IRGB = "irgb";
	public static final String SMC = "smc";
	public static final String RLE = "rle";
	public static final String RPZA = "rpza";
	public static final String MJPG = "mjpg";
	public static final String MJPEGA = "mjpa";
	public static final String MJPEGB = "mjpb";
	public static final String INDEO32 = "iv32";
	public static final String INDEO41 = "iv41";
	public static final String INDEO50 = "iv50";

	public VideoFormat(String encoding) {
		super(encoding);
	}

	public VideoFormat(String encoding, Dimension size, int maxDataLength,
			Class dataType, float frameRate) {
		super(encoding, dataType);
		this.size = FormatUtils.clone(size);
		this.maxDataLength = maxDataLength;
		this.frameRate = frameRate;
	}

	public Dimension getSize() {
		return size;
	}

	public int getMaxDataLength() {
		return maxDataLength;
	}

	public Object clone() {
		return new VideoFormat(encoding, FormatUtils.clone(size),
				maxDataLength, dataType, frameRate);
	}

	public float getFrameRate() {
		return frameRate;
	}

	protected void copy(Format f) {
		super.copy(f);

		final VideoFormat oCast = (VideoFormat) f; // it has to be a
													// VideoFormat, or
													// ClassCastException will
													// be thrown.
		this.size = FormatUtils.clone(oCast.size);
		this.maxDataLength = oCast.maxDataLength;
		this.frameRate = oCast.frameRate;

	}

	public String toString() {
		final StringBuffer b = new StringBuffer();

		if (encoding == null)
			b.append("N/A");
		else
			b.append(encoding.toUpperCase());

		if (FormatUtils.specified(size)) {
			b.append(", ");
			b.append(size.width);
			b.append('x');
			b.append(size.height);
		}

		if (FormatUtils.specified(frameRate)) {
			b.append(", ");
			b.append("FrameRate=");
			b.append(FormatUtils.frameRateToString(frameRate));
		}

		if (FormatUtils.specified(maxDataLength)) {
			b.append(", ");
			b.append("Length=");
			b.append(maxDataLength);
		}

		return b.toString();
	}

	public boolean equals(Object format) {
		if (!super.equals(format))
			return false;

		if (!(format instanceof VideoFormat)) {
			return false;
		}

		final VideoFormat oCast = (VideoFormat) format;
		return (oCast.size == this.size || (oCast.size != null
				&& this.size != null && oCast.size.equals(this.size)))
				&& oCast.maxDataLength == this.maxDataLength
				&& oCast.frameRate == this.frameRate;

	}

	public boolean matches(Format format) {
		if (!super.matches(format)) {
			if (getClass() == FormatUtils.videoFormatClass)
				FormatUtils.traceMatches(this, format, false); // otherwise let
																// subclass
																// trace
			return false;
		}
		if (!(format instanceof VideoFormat)) {
			final boolean result = true;
			if (getClass() == FormatUtils.videoFormatClass)
				FormatUtils.traceMatches(this, format, result);
			return result;
		}

		final VideoFormat oCast = (VideoFormat) format;

		// do not match against max data length.
		final boolean result = FormatUtils.matches(oCast.getFrameRate(), this
				.getFrameRate())
				&& FormatUtils.matches(oCast.getSize(), this.getSize());

		if (getClass() == FormatUtils.videoFormatClass)
			FormatUtils.traceMatches(this, format, result); // otherwise let
															// subclass trace

		return result;

	}

	public Format intersects(Format other) {
		final Format result = super.intersects(other);

		if (other instanceof VideoFormat) {
			final VideoFormat resultCast = (VideoFormat) result;

			final VideoFormat oCast = (VideoFormat) other;
			if (getClass().isAssignableFrom(other.getClass())) {
				// "other" was cloned.
				resultCast.size = oCast.size; // to ensure it is not a copy.
												// TODO: this is a hack, need to
												// understand what is going on
												// here.

				if (FormatUtils.specified(this.size))
					resultCast.size = this.size; // strange, we don't clone
													// here.
				if (FormatUtils.specified(this.maxDataLength))
					resultCast.maxDataLength = this.maxDataLength;
				if (FormatUtils.specified(this.frameRate))
					resultCast.frameRate = this.frameRate;
			} else if (other.getClass().isAssignableFrom(getClass())) { // this
																		// was
																		// cloned

				resultCast.size = this.size; // to ensure it is not a copy.
												// TODO: this is a hack, need to
												// understand what is going on
												// here.

				if (!FormatUtils.specified(resultCast.size))
					resultCast.size = oCast.size; // strange, we don't clone
													// here.
				if (!FormatUtils.specified(resultCast.maxDataLength))
					resultCast.maxDataLength = oCast.maxDataLength;
				if (!FormatUtils.specified(resultCast.frameRate))
					resultCast.frameRate = oCast.frameRate;
			}
		}

		if (getClass() == FormatUtils.videoFormatClass)
			FormatUtils.traceIntersects(this, other, result);

		return result;
	}

	public Format relax() {
		final VideoFormat result = (VideoFormat) super.relax();
		result.size = null;
		result.maxDataLength = NOT_SPECIFIED;
		result.frameRate = NOT_SPECIFIED;
		return result;
	}

}
