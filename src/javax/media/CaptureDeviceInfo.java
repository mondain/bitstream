package javax.media;

/**
 * Coding complete.
 * 
 * @author Ken Larson
 * 
 */
public class CaptureDeviceInfo implements java.io.Serializable {
	protected Format[] formats;
	protected MediaLocator locator;
	protected String name;

	public CaptureDeviceInfo() {
		super();
	}

	public CaptureDeviceInfo(String name, MediaLocator locator, Format[] formats) {
		this.name = name;
		this.locator = locator;
		this.formats = formats;
	}

	public Format[] getFormats() {
		return formats;
	}

	public MediaLocator getLocator() {
		return locator;
	}

	public String getName() {
		return name;
	}

	public boolean equals(Object obj) {
		if (name == null)
			return false;
		if (formats == null)
			return false;
		if (locator == null)
			return false;

		if (!(obj instanceof CaptureDeviceInfo))
			return false;
		CaptureDeviceInfo oCast = (CaptureDeviceInfo) obj;
		return oCast.name.equals(this.name)
				&& oCast.formats.equals(this.formats) && // same as ==, since it
															// is an array.
				oCast.locator.equals(this.locator); // a bit strange, since
													// MediaLocator does not
													// override equals:
													// equivalent to ==

	}

	public String toString() {
		final StringBuffer b = new StringBuffer();
		b.append(name);
		b.append(" : ");
		b.append(locator);
		b.append("\n");
		if (formats != null) {
			for (int i = 0; i < formats.length; ++i) {
				b.append(formats[i]);
				b.append("\n");
			}
		}
		return b.toString();
	}
}
