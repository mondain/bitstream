package javax.media.datasink;

import javax.media.DataSink;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class EndOfStreamEvent extends DataSinkEvent {

	public EndOfStreamEvent(DataSink from, String reason) {
		super(from, reason);
	}

	public EndOfStreamEvent(DataSink from) {
		super(from);
	}

}
