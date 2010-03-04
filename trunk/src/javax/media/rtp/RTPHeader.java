package javax.media.rtp;

/**
 * Coding complete.
 * 
 * @author Ken Larson
 * 
 */
public class RTPHeader implements java.io.Serializable {
	public static final int VALUE_NOT_SET = -1;

	private boolean extensionPresent;
	private int extensionType = VALUE_NOT_SET;
	private byte[] extension;

	public RTPHeader() {
		super();
	}

	public RTPHeader(int marker) { // TODO: none of the properties seem to be
									// affected by this.
	}

	public RTPHeader(boolean extensionPresent, int extensionType,
			byte[] extension) {
		this.extensionPresent = extensionPresent;
		this.extensionType = extensionType;
		this.extension = extension;
	}

	public boolean isExtensionPresent() {
		return extensionPresent;
	}

	public int getExtensionType() {
		return extensionType;
	}

	public byte[] getExtension() {
		return extension;
	}

	public void setExtensionPresent(boolean p) {
		this.extensionPresent = p;
	}

	public void setExtensionType(int t) {
		this.extensionType = t;
	}

	public void setExtension(byte[] e) {
		this.extension = e;
	}
}
