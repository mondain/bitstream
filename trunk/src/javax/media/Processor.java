package javax.media;

import javax.media.control.TrackControl;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface Processor extends Player {
	public static final int Configuring = 140;
	public static final int Configured = 180;

	public void configure();

	public TrackControl[] getTrackControls() throws NotConfiguredError;

	public ContentDescriptor[] getSupportedContentDescriptors()
			throws NotConfiguredError;

	public ContentDescriptor setContentDescriptor(
			ContentDescriptor outputContentDescriptor)
			throws NotConfiguredError;

	public ContentDescriptor getContentDescriptor() throws NotConfiguredError;

	public DataSource getDataOutput() throws NotRealizedError;
}
