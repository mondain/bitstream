package javax.media;

import javax.media.protocol.DataSource;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface MediaProxy extends MediaHandler {
	public DataSource getDataSource() throws java.io.IOException,
			NoDataSourceException;
}
