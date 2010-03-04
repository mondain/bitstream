package javax.media.rtp;

import javax.media.Control;
import javax.media.Format;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface RTPControl extends Control {

	public void addFormat(Format fmt, int payload);

	public ReceptionStats getReceptionStats();

	public GlobalReceptionStats getGlobalStats();

	public Format getFormat();

	public Format[] getFormatList();

	public Format getFormat(int payload);
}
