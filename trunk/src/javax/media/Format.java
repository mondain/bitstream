package javax.media;

import net.sf.fmj.utility.FormatUtils;

/**
 * Coding complete.
 * 
 * @author Ken Larson
 * 
 */
public class Format implements Cloneable, java.io.Serializable {
	public static final int NOT_SPECIFIED = -1;
	public static final int TRUE = 1;
	public static final int FALSE = 0;

	protected String encoding; // allowed to be null

	// we don't declare the types inline here, because the eclipse compiler
	// adds anonymous fields of type Class which affect serialization.
	public static final Class byteArray = FormatUtils.byteArray;
	public static final Class shortArray = FormatUtils.shortArray;
	public static final Class intArray = FormatUtils.intArray;
	public static final Class formatArray = FormatUtils.formatArray;

	protected Class dataType;

	protected Class clz; // TODO: what is this for? - present in JMF javadoc but
							// not clear it is needed.
	// perhaps it is needed for serialization, or for faster code than
	// getClass().

	private long encodingCode; // This is set during equals/matches comparisons
								// via isSameEncoding. Allows for fast string
								// comparisons.

	public Format(String encoding) {
		this.encoding = encoding;
		this.dataType = byteArray;
		clz = getClass();
	}

	public Format(String encoding, Class dataType) {
		this.encoding = encoding;
		this.dataType = dataType;
		clz = getClass();

	}

	public String getEncoding() {
		return encoding;
	}

	public Class getDataType() {
		return dataType;
	}

	public boolean equals(Object format) {
		if (!(format instanceof Format))
			return false;

		// if we are a format is a subclass this, let the subclass do the
		// comparison:
		if (FormatUtils.isSubclass(format.getClass(), getClass())) {
			return format.equals(this);
		}

		final Format oCast = (Format) format;
		return FormatUtils.nullSafeEquals(oCast.getDataType(), this
				.getDataType())
				&& (this.encoding == oCast.encoding || isSameEncoding(oCast));

	}

	public boolean matches(Format format) {
		if (format == null) {
			return false;
			// System.out.println("matches null? " +
			// MediaCGUtils.formatToStr(this));
		}
		if (!FormatUtils.isOneAssignableFromTheOther(getClass(), format
				.getClass()))
			return false;
		return (this.encoding == format.encoding || this.encoding == null
				|| format.encoding == null || isSameEncoding(format))
				&& FormatUtils.matches(format.dataType, this.dataType);

	}

	public Format intersects(Format other) {
		// the JMF JavaDoc states:
		// " If both objects have specified values then the value in this object will be used."
		// however, testing reveals that the value in the passed-in object will
		// be used.

		final Format result;
		if (getClass().isAssignableFrom(other.getClass())) {
			result = (Format) other.clone();
			if (!FormatUtils.specified(result.encoding))
				result.encoding = this.encoding;
			if (!FormatUtils.specified(result.dataType))
				result.dataType = this.dataType;
		} else if (other.getClass().isAssignableFrom(getClass())) {
			result = (Format) clone();
			if (!FormatUtils.specified(result.encoding))
				result.encoding = other.encoding;
			if (!FormatUtils.specified(result.dataType))
				result.dataType = other.dataType;

		} else {
			result = null; // subclasses in different branches
		}

		return result;
	}

	public boolean isSameEncoding(Format other) {
		if (other == null)
			return false;

		if (other.encoding == null)
			return false;

		if (this.encoding == null)
			return false;

		if (other.encoding == this.encoding)
			return true;

		if (this.encodingCode == 0)
			this.encodingCode = getEncodingCode(this.encoding);

		if (other.encodingCode == 0)
			other.encodingCode = getEncodingCode(other.encoding);

		return encodingCode == other.encodingCode;
	}

	public boolean isSameEncoding(String encoding) {
		if (encoding == null)
			return false;

		if (this.encoding == null)
			return false;

		if (encoding == this.encoding)
			return true;

		if (this.encodingCode == 0)
			this.encodingCode = getEncodingCode(this.encoding);

		return this.encodingCode == getEncodingCode(encoding);

	}

	private long getEncodingCode(String enc) {
		if (enc == null)
			return 0;
		return FormatUtils.stringEncodingCodeVal(enc);
	}

	public Format relax() {
		return (Format) clone();
	}

	public Object clone() {
		return new Format(encoding, dataType);

	}

	protected void copy(Format f) {
		// do not copy encoding.
		this.dataType = f.dataType;
	}

	public String toString() {
		return getEncoding();
	}

	static {
		// TODO: what to do here?
	}

}
