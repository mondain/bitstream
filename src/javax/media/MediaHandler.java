package javax.media;

import java.io.IOException;

import javax.media.protocol.DataSource;

/**
 * 
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface MediaHandler {
	public void setSource(DataSource source) throws IOException,
			IncompatibleSourceException;
}
