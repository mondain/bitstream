package javax.media.control;

import javax.media.Format;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface FormatControl extends javax.media.Control {
	public Format getFormat();

	public Format setFormat(Format format);

	public Format[] getSupportedFormats();

	public boolean isEnabled();

	public void setEnabled(boolean enabled);
}
