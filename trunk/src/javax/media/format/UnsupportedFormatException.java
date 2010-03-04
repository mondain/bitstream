package javax.media.format;

import javax.media.Format;
import javax.media.MediaException;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class UnsupportedFormatException extends MediaException {

	private final Format unsupportedFormat;

	public UnsupportedFormatException(Format unsupportedFormat) {
		super();
		this.unsupportedFormat = unsupportedFormat;
	}

	public UnsupportedFormatException(String message, Format unsupportedFormat) {
		super(message);
		this.unsupportedFormat = unsupportedFormat;

	}

	public Format getFailedFormat() {
		return unsupportedFormat;
	}
}
