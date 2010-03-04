package javax.media.control;

import javax.media.Control;
import javax.media.Time;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface FramePositioningControl extends Control {
	public static final Time TIME_UNKNOWN = Time.TIME_UNKNOWN;
	public static final int FRAME_UNKNOWN = Integer.MAX_VALUE;

	public int seek(int frameNumber);

	public int skip(int framesToSkip);

	public Time mapFrameToTime(int frameNumber);

	public int mapTimeToFrame(Time mediaTime);
}
