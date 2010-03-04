package javax.media.format;

import javax.media.Format;

import net.sf.fmj.utility.FormatUtils;

/**
 * Coding complete.
 * 
 * @author Ken Larson
 * 
 */
public class YUVFormat extends VideoFormat {
	public static final int YUV_411 = 1;
	public static final int YUV_420 = 2;
	public static final int YUV_422 = 4;
	public static final int YUV_111 = 8;
	public static final int YUV_YVU9 = 16;
	public static final int YUV_YUYV = 32;
	public static final int YUV_SIGNED = 64;

	protected int strideY = NOT_SPECIFIED;

	protected int strideUV = NOT_SPECIFIED;

	protected int yuvType = NOT_SPECIFIED;

	protected int offsetY = NOT_SPECIFIED;

	protected int offsetU = NOT_SPECIFIED;

	protected int offsetV = NOT_SPECIFIED;

	private static String ENCODING = YUV;

	public YUVFormat() {
		super(ENCODING);
		dataType = Format.byteArray;

	}

	public YUVFormat(int yuvType) {
		super(ENCODING);
		this.yuvType = yuvType;
		this.dataType = byteArray;
	}

	public YUVFormat(java.awt.Dimension size, int maxDataLength,
			Class dataType, float frameRate, int yuvType, int strideY,
			int strideUV, int offsetY, int offsetU, int offsetV)

	{
		super(ENCODING, size, maxDataLength, dataType, frameRate);
		this.yuvType = yuvType;
		this.strideY = strideY;
		this.strideUV = strideUV;
		this.offsetY = offsetY;
		this.offsetU = offsetU;
		this.offsetV = offsetV;

	}

	public int getYuvType() {
		return yuvType;
	}

	public int getStrideY() {
		return strideY;
	}

	public int getStrideUV() {
		return strideUV;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public int getOffsetU() {
		return offsetU;
	}

	public int getOffsetV() {
		return offsetV;
	}

	public Object clone() {
		return new YUVFormat(FormatUtils.clone(size), maxDataLength, dataType,
				frameRate, yuvType, strideY, strideUV, offsetY, offsetU,
				offsetV);
	}

	protected void copy(Format f) {
		super.copy(f);
		final YUVFormat oCast = (YUVFormat) f; // it has to be a YUVFormat, or
												// ClassCastException will be
												// thrown.
		this.yuvType = oCast.yuvType;
		this.strideY = oCast.strideY;
		this.strideUV = oCast.strideUV;
		this.offsetY = oCast.offsetY;
		this.offsetU = oCast.offsetU;
		this.offsetV = oCast.offsetV;

	}

	public boolean equals(Object format) {
		if (!super.equals(format))
			return false;

		if (!(format instanceof YUVFormat)) {
			return false;
		}

		final YUVFormat oCast = (YUVFormat) format;
		return this.yuvType == oCast.yuvType && this.strideY == oCast.strideY
				&& this.strideUV == oCast.strideUV
				&& this.offsetY == oCast.offsetY
				&& this.offsetU == oCast.offsetU
				&& this.offsetV == oCast.offsetV;
	}

	public boolean matches(Format format) {
		if (!super.matches(format)) {
			FormatUtils.traceMatches(this, format, false);
			return false;
		}

		if (!(format instanceof YUVFormat)) {
			final boolean result = true;
			FormatUtils.traceMatches(this, format, result);
			return result;
		}

		final YUVFormat oCast = (YUVFormat) format;

		final boolean result = FormatUtils.matches(this.yuvType, oCast.yuvType)
				&& FormatUtils.matches(this.strideY, oCast.strideY)
				&& FormatUtils.matches(this.strideUV, oCast.strideUV)
				&& FormatUtils.matches(this.offsetY, oCast.offsetY)
				&& FormatUtils.matches(this.offsetU, oCast.offsetU)
				&& FormatUtils.matches(this.offsetV, oCast.offsetV);

		FormatUtils.traceMatches(this, format, result);

		return result;

	}

	public Format intersects(Format other) {
		final Format result = super.intersects(other);
		if (other instanceof YUVFormat) {
			final YUVFormat resultCast = (YUVFormat) result;

			final YUVFormat oCast = (YUVFormat) other;
			if (getClass().isAssignableFrom(other.getClass())) {
				// "other" was cloned.

				if (FormatUtils.specified(this.yuvType))
					resultCast.yuvType = this.yuvType;
				if (FormatUtils.specified(this.strideY))
					resultCast.strideY = this.strideY;
				if (FormatUtils.specified(this.strideUV))
					resultCast.strideUV = this.strideUV;
				if (FormatUtils.specified(this.offsetY))
					resultCast.offsetY = this.offsetY;
				if (FormatUtils.specified(this.offsetU))
					resultCast.offsetU = this.offsetU;
				if (FormatUtils.specified(this.offsetV))
					resultCast.offsetV = this.offsetV;

			} else { // this was cloned

				if (!FormatUtils.specified(resultCast.yuvType))
					resultCast.yuvType = oCast.yuvType;
				if (!FormatUtils.specified(resultCast.strideY))
					resultCast.strideY = oCast.strideY;
				if (!FormatUtils.specified(resultCast.strideUV))
					resultCast.strideUV = oCast.strideUV;
				if (!FormatUtils.specified(resultCast.offsetY))
					resultCast.offsetY = oCast.offsetY;
				if (!FormatUtils.specified(resultCast.offsetU))
					resultCast.offsetU = oCast.offsetU;
				if (!FormatUtils.specified(resultCast.offsetV))
					resultCast.offsetV = oCast.offsetV;

			}
		}

		FormatUtils.traceIntersects(this, other, result);

		return result;
	}

	public Format relax() {
		final YUVFormat result = (YUVFormat) super.relax();
		result.strideY = NOT_SPECIFIED;
		result.strideUV = NOT_SPECIFIED;
		result.offsetY = NOT_SPECIFIED;
		result.offsetU = NOT_SPECIFIED;
		result.offsetV = NOT_SPECIFIED;
		return result;

	}

	public String toString() {
		return "YUV Video Format:" + " Size = " + size + " MaxDataLength = "
				+ maxDataLength + " DataType = " + dataType + " yuvType = "
				+ yuvType + " StrideY = " + strideY + " StrideUV = " + strideUV
				+ " OffsetY = " + offsetY + " OffsetU = " + offsetU
				+ " OffsetV = " + offsetV + "\n";
	}

	static { // for Serializable compatibility.
	}
}
