package javax.media.pim;

import javax.media.Format;

/**
 * 
 * @author Ken Larson
 * 
 */
class PlugInInfo {
	public String className;
	public Format[] inputFormats;
	public Format[] outputFormats;

	public PlugInInfo(String name, Format[] formats, Format[] formats2) {
		super();
		className = name;
		inputFormats = formats;
		outputFormats = formats2;
	}

	public int hashCode() {
		return className.hashCode();
	}

	public boolean equals(Object other) {
		return (other instanceof PlugInInfo && (className == ((PlugInInfo) other).className || className != null
				&& className.equals(((PlugInInfo) other).className)));
	}
}
