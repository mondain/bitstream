package net.sf.fmj.media.protocol.http;

import java.net.URL;

import net.sf.fmj.media.protocol.URLDataSource;

/**
 * TODO: move http-specific code from URLDataSource to here.
 * 
 * @author Ken Larson
 * 
 */
public class DataSource extends URLDataSource {

	public DataSource() {
		super();
	}

	public DataSource(URL url) {
		super(url);
	}

}
