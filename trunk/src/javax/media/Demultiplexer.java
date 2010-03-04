package javax.media;

import java.io.IOException;

import javax.media.protocol.ContentDescriptor;

/**
 * 
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface Demultiplexer extends PlugIn, MediaHandler, Duration {

	public ContentDescriptor[] getSupportedInputContentDescriptors();

	public void start() throws IOException;

	public void stop();

	public Track[] getTracks() throws IOException, BadHeaderException;

	public boolean isPositionable();

	public boolean isRandomAccess();

	public Time setPosition(Time where, int rounding);

	public Time getMediaTime();

	public Time getDuration();

}
