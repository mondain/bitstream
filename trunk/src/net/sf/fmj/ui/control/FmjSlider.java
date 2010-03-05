package net.sf.fmj.ui.control;

import java.awt.Color;
import java.util.HashSet;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sf.fmj.ui.application.ContainerPlayer;
import net.sf.fmj.ui.plaf.FmjSliderUI;

/**
 * A slider with a nice look-and-feel.
 * 
 * @author Warren Bloomer
 * 
 */
public class FmjSlider extends JSlider {
	private static final long serialVersionUID = 014L;

	/** whether to track the slider */
	// private boolean trackSlider = false;

	private boolean paintFocus = false;

	private int value = -1;

	private HashSet linearListeners;
	private TransportControl player;
	/**
	 * Constructor
	 * 
	 */
	public FmjSlider(TransportControl player) {
		this.player = player;
		initialize();
	}

	public boolean getPaintFocus() {
		return paintFocus;
	}

	public void setPaintFocus(boolean paintFocus) {
		this.paintFocus = paintFocus;
	}

	private void initialize() {
		this.setUI(FmjSliderUI.createUI(this));
		this.setName("Slider");
		this.setBackground(Color.WHITE);
		this.setOpaque(false);

		this.setPaintTrack(true);
		this.setPaintTicks(false);
		this.setSnapToTicks(true);

		
		this.addChangeListener(new SliderListener());
	}

	/**
	 * Returns the Linear listeners.
	 * 
	 * @return the Linear listeners
	 */
	private HashSet getLinearListeners() {
		if (linearListeners == null) {
			linearListeners = new HashSet();
		}
		return linearListeners;
	}

	/**
	 * A listener for state change events on the slider
	 */
	private class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent event) {
			sendValue();
		}

		private void sendValue() {
			int newValue = getValue();
			
			// only send if value has changed
			if (Math.abs(newValue - value) > 1000) {
				if(player != null) {	
					player.setPosition(newValue/1000);
				}
			}
			value = newValue;
		}

	}
}
