package net.sf.fmj.ejmf.toolkit.gui.controls;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventListener;

import javax.media.Controller;
import javax.media.Time;

/**
 * Reverse Control for StandardControlPanel. This Control is operational only if
 * Controller supports negative rate.
 */
public class StandardReverseControl extends MouseListenerControl {

	public StandardReverseControl(Skin skin) {
		super(skin);
	}

	public StandardReverseControl(Skin skin, Controller controller) {
		this(skin);
		setController(controller);
	}

	@Override
	protected Component createControlComponent(Skin skin) {
		return skin.createReverseButton();
	}

	// ///////// Conditional Control Interface ///////////////////

	/**
	 * Set the state of the listener. If <code>isOperational</code> is passed as
	 * <code>true</code>, then the listener semantics are applied in response to
	 * mouse activity. Otherwise, the listener semantics are not applied.
	 * 
	 * This is used to disable default semantics if client control panel
	 * simulates reversing media. In this case, <code>setController</code> will
	 * call <code>setOperational</code> with a <code>true</code> value.
	 */
	protected void setOperational(boolean isOperational) {
		super.setOperational(isOperational);
		getControlComponent().setEnabled(isOperational);
	}

	/**
	 * Determine operational state of Control based on ability to support
	 * negative rate. If a negative rate is supported, I can use setRate to
	 * affect Controller reverse. Test here for negative rate and then reset old
	 * rate.
	 */
	protected void setControllerHook(Controller controller) {
		if (true) {
			setOperational(false); // disable because setRate causes problems
			return;
		}
		float saveRate = controller.getRate();
		float rate = controller.setRate(-1.0f);
		setOperational(rate < 0.0f);
		getControlComponent().setEnabled(isOperational());
		getController().setRate(saveRate);
	}

	protected EventListener createControlListener() {
		return new MouseAdapter() {
			int priorState;
			float saveRate;

			public void mousePressed(MouseEvent mouseEvent) {
				if (isOperational()) {
					Controller controller = getController();
					saveRate = controller.getRate();
					priorState = controller.getState();
					if (priorState == Controller.Started) {
						controller.stop();
					}
					controller.setRate(-1.0f * saveRate);
					Time now = controller.getTimeBase().getTime();
					controller.syncStart(now);
				}
			}

			public void mouseReleased(MouseEvent event) {
				if (isOperational()) {
					Controller controller = getController();
					controller.setRate(saveRate);
					if (priorState != Controller.Started) {
						controller.stop();
					}
				}
			}
		};
	}
}
