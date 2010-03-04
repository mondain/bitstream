package net.sf.fmj.ejmf.toolkit.media;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;

import net.sf.fmj.utility.LoggerSingleton;

/**
 * This class provides a dispatching mechanism for ControllerEvents. All events
 * posted to this queue are dispatched to a Vector of ControllerListeners given
 * when the queue is constructed. Note that ControllerListeners may be added or
 * removed from this Vector from outside of this class, and these changes will
 * be reflected in the dispatching mechanism.
 * 
 * From the book: Essential JMF, Gordon, Talley (ISBN 0130801046). Used with
 * permission.
 * 
 * @author Steve Talley & Rob Gordon
 */
public class ControllerEventQueue extends Thread {

	private static final Logger logger = LoggerSingleton.logger;

	Vector eventQueue = new Vector();
	Vector listeners;

	/**
	 * Construct a ControllerEventQueue for the given list of
	 * ControllerListeners.
	 * 
	 * @param listeners
	 *            The list of ControllerListeners to notify whenever a
	 *            ControllerEvents is posted.
	 */
	public ControllerEventQueue(Vector listeners, String threadName) {
		super();
		setName(threadName);
		this.listeners = listeners;
		setDaemon(true);
	}

	/**
	 * Post a ControllerEvent to the queue. All listeners will be notified ASAP.
	 */
	public synchronized void postEvent(ControllerEvent event) {
		eventQueue.addElement(event);
		notify();
	}

	/**
	 * Monitor the event queue. When an event is added, dispatch to all
	 * listeners.
	 */
	private void monitorEvents() {
		Vector v;

		while (true) {
			synchronized (this) {
				while (eventQueue.size() == 0) {
					try {
						wait();
					} catch (InterruptedException e) {
					}
				}
				v = (Vector) eventQueue.clone();
				eventQueue.removeAllElements();
			}

			for (int i = 0; i < v.size(); i++) {
				ControllerEvent event = (ControllerEvent) v.elementAt(i);
				dispatchEvent(event);
			}
		}
	}

	/**
	 * Dispatches an event on the current thread to all ControllerListeners of
	 * this Conroller. Exceptions are caught during the notification so that
	 * event processing can continue.
	 */
	private void dispatchEvent(ControllerEvent event) {
		Vector l;

		// Synchronize so that the listeners vector will not
		// change while we are copying it
		synchronized (listeners) {
			l = (Vector) listeners.clone();
		}

		for (int i = 0; i < l.size(); i++) {
			Object o = l.elementAt(i);

			// Make sure this is a ControllerListener
			if (o instanceof ControllerListener) {
				ControllerListener listener = (ControllerListener) o;

				try {
					listener.controllerUpdate(event);
				} catch (Exception e) {
					logger.log(Level.WARNING,
							"Exception occurred during event dispatching:" + e,
							e);

				}
			}
		}
	}

	/**
	 * Endlessly monitor the event queue. This method should not be called
	 * directly.
	 */
	public void run() {
		monitorEvents();
	}
}
