package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public class GainChangeEvent extends MediaEvent {
	GainControl eventSrc;

	boolean newMute;
	float newDB;
	float newLevel;

	public GainChangeEvent(GainControl from, boolean mute, float dB, float level) {
		super(from);
		this.eventSrc = from;
		this.newMute = mute;
		this.newDB = dB;
		this.newLevel = level;
	}

	public Object getSource() {
		return eventSrc;
	}

	public GainControl getSourceGainControl() {
		return eventSrc;
	}

	public float getDB() {
		return newDB;
	}

	public float getLevel() {
		return newLevel;
	}

	public boolean getMute() {
		return newMute;
	}
}
