package javax.media.protocol;

import javax.media.CaptureDeviceInfo;
import javax.media.control.FormatControl;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface CaptureDevice {
	public CaptureDeviceInfo getCaptureDeviceInfo();

	public FormatControl[] getFormatControls();

	public void connect() throws java.io.IOException;

	public void disconnect();

	public void start() throws java.io.IOException;

	public void stop() throws java.io.IOException;
}
