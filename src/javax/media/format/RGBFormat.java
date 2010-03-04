package javax.media.format;

import javax.media.Format;

import net.sf.fmj.utility.FormatUtils;

/**
 * Coding complete.
 * 
 * @author Ken Larson
 * 
 */
public class RGBFormat extends VideoFormat {

	protected int redMask = NOT_SPECIFIED;
	protected int greenMask = NOT_SPECIFIED;
	protected int blueMask = NOT_SPECIFIED;
	protected int bitsPerPixel = NOT_SPECIFIED;
	protected int pixelStride = NOT_SPECIFIED;
	protected int lineStride = NOT_SPECIFIED;
	protected int flipped = NOT_SPECIFIED;
	protected int endian = NOT_SPECIFIED;

	public static final int BIG_ENDIAN = 0;
	public static final int LITTLE_ENDIAN = 1;

	private static String ENCODING = RGB;

	public RGBFormat() {
		super(ENCODING);
		this.dataType = null;

	}

	public RGBFormat(java.awt.Dimension size, int maxDataLength,
			Class dataType, float frameRate, int bitsPerPixel, int red,
			int green, int blue) {
		super(ENCODING, size, maxDataLength, dataType, frameRate);
		this.bitsPerPixel = bitsPerPixel;
		this.redMask = red;
		this.greenMask = green;
		this.blueMask = blue;

		this.flipped = 0;
		// this constructor is a pain because the values of pixelStride,
		// lineStride, and endian get set
		// by a non-trivial calculation. flipped seems to always be zero.

		if (dataType == null || bitsPerPixel == NOT_SPECIFIED)
			this.pixelStride = NOT_SPECIFIED;
		else if (dataType == byteArray) {
			this.pixelStride = bitsPerPixel / 8;
		} else { // short, and int arrays
			this.pixelStride = 1;
		}

		// endian

		if (dataType == byteArray && bitsPerPixel == 16) {
			this.endian = 1;
		} else {
			this.endian = NOT_SPECIFIED;
		}

		// line stride:
		if (dataType == null || size == null || bitsPerPixel == NOT_SPECIFIED) {
			this.lineStride = NOT_SPECIFIED;
		} else {
			if (dataType == byteArray) {
				this.lineStride = size.width * (bitsPerPixel / 8);
			} else {
				this.lineStride = size.width;
			}
		}

	}

	public RGBFormat(java.awt.Dimension size, int maxDataLength,
			Class dataType, float frameRate, int bitsPerPixel, int red,
			int green, int blue, int pixelStride, int lineStride, int flipped,
			int endian) {
		super(ENCODING, size, maxDataLength, dataType, frameRate);
		this.bitsPerPixel = bitsPerPixel;
		this.redMask = red;
		this.greenMask = green;
		this.blueMask = blue;
		this.pixelStride = pixelStride;
		this.lineStride = lineStride;
		this.flipped = flipped;
		this.endian = endian;
	}

	public int getBitsPerPixel() {
		return bitsPerPixel;
	}

	public int getRedMask() {
		return redMask;
	}

	public int getGreenMask() {
		return greenMask;
	}

	public int getBlueMask() {
		return blueMask;
	}

	public int getPixelStride() {
		return pixelStride;
	}

	public int getLineStride() {
		return lineStride;
	}

	public int getFlipped() {
		return flipped;
	}

	public int getEndian() {
		return endian;
	}

	public Object clone() {

		final RGBFormat result = new RGBFormat(FormatUtils.clone(size),
				maxDataLength, dataType, frameRate, bitsPerPixel, redMask,
				greenMask, blueMask, pixelStride, lineStride, flipped, endian);

		FormatUtils.traceClone(this, result);

		return result;
	}

	protected void copy(Format f) {

		super.copy(f);
		final RGBFormat oCast = (RGBFormat) f; // it has to be a RGBFormat, or
												// ClassCastException will be
												// thrown.
		this.bitsPerPixel = oCast.bitsPerPixel;
		this.redMask = oCast.redMask;
		this.greenMask = oCast.greenMask;
		this.blueMask = oCast.blueMask;
		this.pixelStride = oCast.pixelStride;
		this.lineStride = oCast.lineStride;
		this.flipped = oCast.flipped;
		this.endian = oCast.endian;
	}

	public boolean equals(Object format) {

		if (!super.equals(format)) {
			FormatUtils.traceEquals(this, (Format) format, false);
			return false;
		}

		if (!(format instanceof RGBFormat)) {
			FormatUtils.traceEquals(this, (Format) format, false);
			return false;
		}

		final RGBFormat oCast = (RGBFormat) format;
		final boolean result = this.bitsPerPixel == oCast.bitsPerPixel
				&& this.redMask == oCast.redMask
				&& this.greenMask == oCast.greenMask
				&& this.blueMask == oCast.blueMask
				&& this.pixelStride == oCast.pixelStride
				&& this.lineStride == oCast.lineStride
				&& this.flipped == oCast.flipped && this.endian == oCast.endian;

		FormatUtils.traceEquals(this, (Format) format, result);
		return result;
	}

	public boolean matches(Format format) {

		if (!super.matches(format)) {
			FormatUtils.traceMatches(this, format, false);
			return false;
		}

		if (!(format instanceof RGBFormat)) {
			final boolean result = true;
			FormatUtils.traceMatches(this, format, result);
			return result;
		}

		final RGBFormat oCast = (RGBFormat) format;

		// do not check lineStride:
		final boolean result = FormatUtils.matches(this.bitsPerPixel,
				oCast.bitsPerPixel)
				&& FormatUtils.matches(this.redMask, oCast.redMask)
				&& FormatUtils.matches(this.greenMask, oCast.greenMask)
				&& FormatUtils.matches(this.blueMask, oCast.blueMask)
				&& FormatUtils.matches(this.pixelStride, oCast.pixelStride)
				&& FormatUtils.matches(this.flipped, oCast.flipped)
				&& FormatUtils.matches(this.endian, oCast.endian);

		FormatUtils.traceMatches(this, format, result);

		return result;
	}

	public Format intersects(Format other) {

		final Format result = super.intersects(other);
		if (other instanceof RGBFormat) {
			final RGBFormat resultCast = (RGBFormat) result;

			final RGBFormat oCast = (RGBFormat) other;
			if (getClass().isAssignableFrom(other.getClass())) {
				// "other" was cloned.

				if (FormatUtils.specified(this.bitsPerPixel))
					resultCast.bitsPerPixel = this.bitsPerPixel;
				if (FormatUtils.specified(this.redMask))
					resultCast.redMask = this.redMask;
				if (FormatUtils.specified(this.greenMask))
					resultCast.greenMask = this.greenMask;
				if (FormatUtils.specified(this.blueMask))
					resultCast.blueMask = this.blueMask;
				if (FormatUtils.specified(this.pixelStride))
					resultCast.pixelStride = this.pixelStride;
				if (FormatUtils.specified(this.lineStride))
					resultCast.lineStride = this.lineStride;
				if (FormatUtils.specified(this.flipped))
					resultCast.flipped = this.flipped;
				if (FormatUtils.specified(this.endian))
					resultCast.endian = this.endian;
			} else { // this was cloned

				if (!FormatUtils.specified(resultCast.bitsPerPixel))
					resultCast.bitsPerPixel = oCast.bitsPerPixel;
				if (!FormatUtils.specified(resultCast.redMask))
					resultCast.redMask = oCast.redMask;
				if (!FormatUtils.specified(resultCast.greenMask))
					resultCast.greenMask = oCast.greenMask;
				if (!FormatUtils.specified(resultCast.blueMask))
					resultCast.blueMask = oCast.blueMask;
				if (!FormatUtils.specified(resultCast.pixelStride))
					resultCast.pixelStride = oCast.pixelStride;
				if (!FormatUtils.specified(resultCast.lineStride))
					resultCast.lineStride = oCast.lineStride;
				if (!FormatUtils.specified(resultCast.flipped))
					resultCast.flipped = oCast.flipped;
				if (!FormatUtils.specified(resultCast.endian))
					resultCast.endian = oCast.endian;
			}
		}

		FormatUtils.traceIntersects(this, other, result);

		return result;
	}

	public Format relax() {
		final RGBFormat result = (RGBFormat) super.relax();
		result.lineStride = NOT_SPECIFIED;
		result.pixelStride = NOT_SPECIFIED;
		FormatUtils.traceRelax(this, result);

		return result;

	}

	public String toString() {
		StringBuffer b = new StringBuffer();

		b.append("RGB");

		if (size != null)
			b.append(", " + (int) size.getWidth() + "x"
					+ (int) size.getHeight());
		if (frameRate != -1.f)
			b.append(", FrameRate=" + FormatUtils.frameRateToString(frameRate));

		if (maxDataLength != -1)
			b.append(", Length=" + maxDataLength);

		b.append(", " + bitsPerPixel + "-bit" + ", Masks=" + redMask + ":"
				+ greenMask + ":" + blueMask + ", PixelStride=" + pixelStride
				+ ", LineStride=" + lineStride);

		if (flipped == 1)
			b.append(", Flipped");

		return b.toString();

	}

	static {
	}

}
