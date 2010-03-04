package javax.media.datasink;

import javax.media.DataSink;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class DataSinkErrorEvent extends DataSinkEvent {

	public DataSinkErrorEvent(DataSink from, String reason) {
		super(from, reason);
	}

	public DataSinkErrorEvent(DataSink from) {
		super(from);
	}

}
