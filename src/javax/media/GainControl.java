package javax.media;

/**
 * Complete.
 * 
 * @author Ken Larson
 * 
 */
public interface GainControl extends Control {
	public void setMute(boolean mute);

	public boolean getMute();

	public float setDB(float gain);

	public float getDB();

	public float setLevel(float level);

	public float getLevel();

	public void addGainChangeListener(GainChangeListener listener);

	public void removeGainChangeListener(GainChangeListener listener);
}
