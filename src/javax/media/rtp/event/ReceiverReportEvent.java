package javax.media.rtp.event;

import javax.media.rtp.SessionManager;
import javax.media.rtp.rtcp.ReceiverReport;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class ReceiverReportEvent extends RemoteEvent {
	private ReceiverReport report;

	public ReceiverReportEvent(SessionManager from, ReceiverReport report) {
		super(from);
		this.report = report;
	}

	public ReceiverReport getReport() {
		return report;
	}
}
