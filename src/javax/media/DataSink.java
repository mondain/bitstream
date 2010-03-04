package javax.media;

import javax.media.datasink.DataSinkListener;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface DataSink extends MediaHandler, Controls {
	public void setOutputLocator(MediaLocator output);

	public MediaLocator getOutputLocator();

	public void start() throws java.io.IOException;

	public void stop() throws java.io.IOException;

	public void open() throws java.io.IOException, SecurityException;

	public void close();

	public String getContentType();

	public void addDataSinkListener(DataSinkListener listener);

	public void removeDataSinkListener(DataSinkListener listener);
}
