package net.sf.fmj.media;

import javax.media.Buffer;
import javax.media.Duration;
import javax.media.Format;
import javax.media.Time;
import javax.media.Track;
import javax.media.TrackListener;

/**
 * 
 * @author Ken Larson
 * 
 */
public abstract class AbstractTrack implements Track {

	private boolean enabled = true; // default to enabled. JMF won't play the
									// track if it is not enabled. TODO: FMJ
									// should do the same.
	private TrackListener trackListener;

	public abstract Format getFormat();

	public Time getStartTime() {
		return TIME_UNKNOWN;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Time mapFrameToTime(int frameNumber) {
		return TIME_UNKNOWN;
	}

	public int mapTimeToFrame(Time t) {
		return FRAME_UNKNOWN;
	}

	public abstract void readFrame(Buffer buffer);

	public void setEnabled(boolean t) {
		this.enabled = t;
	}

	public void setTrackListener(TrackListener listener) {
		this.trackListener = listener;
	}

	public Time getDuration() {
		return Duration.DURATION_UNKNOWN;
	}

}
