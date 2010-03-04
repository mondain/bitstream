package net.sf.fmj.media.renderer.audio;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.media.GainChangeEvent;
import javax.media.GainChangeListener;
import javax.media.GainControl;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.FloatControl;

/**
 * See Sun's javadoc for GainControl. TODO: need to make sure we meet the
 * requirements in the javadoc under the section
 * "Decibel and Level Interactions".
 * 
 * @author Ken Larson
 * 
 */
public class JavaSoundGainControl implements GainControl {

	private final FloatControl masterGainControl;
	private final BooleanControl muteControl;
	private final float max; // max gain
	private final float min; // min gain
	private final float range; // max - min;
	private final boolean gainUnitsDb; // whether the underlying
										// masterGainControl uses db for units.

	public JavaSoundGainControl(final FloatControl masterGainControl,
			final BooleanControl muteControl) {
		super();
		this.masterGainControl = masterGainControl;
		this.muteControl = muteControl;

		if (masterGainControl != null) {
			min = masterGainControl.getMinimum();
			max = masterGainControl.getMaximum();
			gainUnitsDb = masterGainControl.getUnits().equals("dB"); // a bit of
																		// a
																		// hack
																		// since
																		// we
																		// are
																		// effectively
																		// string-scraping
																		// here.
		} else {
			min = max = 0.f;
			gainUnitsDb = false;
		}
		range = max - min;
	}

	// TODO: need a visual component.
	// private StandardGainControl standardGainControl;
	public Component getControlComponent() {
		return null;
		// if (standardGainControl == null)
		// {
		// standardGainControl = new StandardGainControl();
		// // TODO
		// }
		//		
		// return standardGainControl.getControlComponent();
	}

	private final List listeners = new ArrayList(); // of GainChangeListener

	public void addGainChangeListener(GainChangeListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	public float getDB() {
		if (masterGainControl == null)
			return 0.f;

		if (gainUnitsDb)
			return masterGainControl.getValue();
		else
			return levelToDb(getLevel());

	}

	private static float levelToDb(float level) {
		return (float) (Math.log10(level) * 20.0); // opposite of dBToLevel.
	}

	private static float dBToLevel(float db) {
		return (float) Math.pow(10.0, db / 20.0); // from GainControl javadoc
	}

	/** Level is between 0 and 1 */
	public float getLevel() {
		if (masterGainControl == null)
			return 0.f;

		if (gainUnitsDb) {
			return dBToLevel(masterGainControl.getValue());
		} else {
			float value = masterGainControl.getValue();
			return (value - min) / range;
		}
	}

	public boolean getMute() {
		if (muteControl == null)
			return false;
		return muteControl.getValue();
	}

	public void removeGainChangeListener(GainChangeListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}

	}

	private void notifyListenersGainChangeEvent() {
		final GainChangeEvent event = new GainChangeEvent(this, getMute(),
				getDB(), getLevel());
		notifyListenersGainChangeEvent(event);
	}

	private void notifyListenersGainChangeEvent(GainChangeEvent event) {
		final List listenersCopy = new ArrayList(); // of GainChangeListener

		synchronized (listeners) {
			listenersCopy.addAll(listeners);
		}

		for (int i = 0; i < listenersCopy.size(); ++i) {
			final GainChangeListener listener = (GainChangeListener) listenersCopy
					.get(i);
			listener.gainChange(event);
		}
	}

	public float setDB(float gain) {
		if (masterGainControl == null)
			return 0.f;

		if (gainUnitsDb)
			masterGainControl.setValue(gain);
		else
			setLevel(dBToLevel(gain));

		final float result = getDB();

		notifyListenersGainChangeEvent(); // TODO: don't notify if no change

		return result;
	}

	public float setLevel(float level) {
		if (masterGainControl == null)
			return 0.f;

		if (gainUnitsDb) {
			masterGainControl.setValue(levelToDb(level));
		} else {
			level = min + level * range;
			masterGainControl.setValue(level);
		}

		float result = getLevel();

		notifyListenersGainChangeEvent(); // TODO: don't notify if no change

		return result;
	}

	public void setMute(boolean mute) {
		if (muteControl == null)
			return;

		muteControl.setValue(mute);

		notifyListenersGainChangeEvent(); // TODO: don't notify if no change

	}

}
