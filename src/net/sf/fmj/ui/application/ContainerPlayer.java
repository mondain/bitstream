package net.sf.fmj.ui.application;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Controller;
import javax.media.ControllerClosedEvent;
import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.MediaLocator;
import javax.media.MediaTimeSetEvent;
import javax.media.NoDataSourceException;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.RestartingEvent;
import javax.media.StartEvent;
import javax.media.StopEvent;
import javax.media.Time;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import net.sf.fmj.ejmf.toolkit.util.SourcedTimer;
import net.sf.fmj.ejmf.toolkit.util.SourcedTimerEvent;
import net.sf.fmj.ejmf.toolkit.util.SourcedTimerListener;
import net.sf.fmj.ejmf.toolkit.util.TimeSource;
import net.sf.fmj.ui.control.TransportControl;
import net.sf.fmj.ui.control.TransportControlListener;
import net.sf.fmj.ui.control.TransportControlState;
import net.sf.fmj.utility.ClasspathChecker;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.URLUtils;

/**
 * ContainerPlayer.
 * 
 * Slider update code adapted from EJMF StandardProgressControl.
 * 
 * @author Warren Bloomer
 */
public class ContainerPlayer implements TransportControl, SourcedTimerListener,
		TimeSource {

	private static final Logger logger = LoggerSingleton.logger;

	/** the visual contain to place this player in */
	private Container container;

	/** the JMF player */
	private volatile Player player;

	/** the visual component of the player */
	private Component visualComponent;

	/** whether the player should start upon being "realized" */
	private boolean shouldStartOnRealize = false;
	/** whether we should loop upon EOM */
	private volatile boolean autoLoop = false;

	/** listener for mouse events from visual component */
	private MouseListener mouseListener;

	/** the location of the media. A URI string */
	private String mediaLocation;

	private TransportControlState transportControlState = new TransportControlState();

	private SourcedTimer controlTimer;

	// Timer will fire every TIMER_TICK milliseconds
	final private static int TIMER_TICK = 250;

	/** controller listener to listen to controller events from the player */
	private ControllerListener controllerListener = new ControllerListener() {
		public void controllerUpdate(ControllerEvent event) {

			logger.fine("Got controller event: " + event);

			Player player = (Player) event.getSourceController();

			if (player != ContainerPlayer.this.player)
				return; // ignore messages from old players.

			// TODO: handle RestartingEvent
			if (event instanceof RealizeCompleteEvent) {
				notifyStatusListener(ContainerPlayerStatusListener.REALIZE_COMPLETE);
				// controller realized
				try {
					setVisualComponent(player.getVisualComponent());
					if (shouldStartOnRealize) {
						start();

					}
				} catch (Exception e) {
					notifyStatusListener(ContainerPlayerStatusListener.ERROR_SHOWING_PLAYER);
					logger.log(Level.WARNING, "" + e, e);
					JLabel label = new JLabel("");
					setVisualComponent(label);
				}
			} else if (event instanceof ResourceUnavailableEvent) {
				notifyStatusListener(ContainerPlayerStatusListener.RESOURCE_UNAVAILABLE);
				JLabel label = new JLabel("");
				setVisualComponent(label);
			} else if (event instanceof StopEvent) {
				notifyStatusListener(ContainerPlayerStatusListener.STOPPED);

				transportControlState.setAllowPlay(true);
				transportControlState.setAllowStop(false);
				transportControlState.setAllowVolume(false);

				if (transportControlListener != null)
					transportControlListener
							.onStateChange(transportControlState);

			} else if (event instanceof StartEvent) {
				notifyStatusListener(ContainerPlayerStatusListener.STARTED);

				transportControlState.setAllowPlay(false);
				transportControlState.setAllowStop(true);
				transportControlState
						.setAllowVolume(player.getGainControl() != null);

				if (transportControlListener != null)
					transportControlListener
							.onStateChange(transportControlState);
			} else if (event instanceof ControllerErrorEvent) {
				notifyStatusListener(ContainerPlayerStatusListener.ERROR_PREFIX
						+ ((ControllerErrorEvent) event).getMessage());
			} else if (event instanceof ControllerClosedEvent) {
			}

			// Slider-related: start/stop timer, etc:
			// if (isOperational())
			{
				if (event instanceof StartEvent
						|| event instanceof RestartingEvent) {
					if (transportControlListener != null)
						transportControlListener.onDurationChange(player
								.getDuration().getNanoseconds());
					if (transportControlListener != null)
						transportControlListener.onProgressChange(getTime());
					controlTimer.start();
				} else if (event instanceof StopEvent
						|| event instanceof ControllerErrorEvent) {
					controlTimer.stop();
				} else if (event instanceof MediaTimeSetEvent) {
					if (transportControlListener != null)
						transportControlListener.onDurationChange(player
								.getDuration().getNanoseconds()); // just in
																	// case

					// This catches any direct setting of media time
					// by application. Additionally, it catches
					// setMediaTime(0) by StandardStopControl.
					if (transportControlListener != null)
						transportControlListener.onProgressChange(getTime());
				}
			}

			// Borrowed from EJMF GenericPlayer:
			if (event instanceof EndOfMediaEvent) {
				notifyStatusListener(ContainerPlayerStatusListener.END_OF_MEDIA);
				// End of the media -- rewind
				player.setMediaTime(new Time(0));
				if (autoLoop)
					start();

			}
		}
	};

	private void notifyStatusListener(String status) {
		if (statusListener != null)
			statusListener.onStatusChange(status);

	}

	/**
	 * Constructor.
	 * 
	 * @param container
	 */
	public ContainerPlayer(Container container) {
		setContainer(container);
	}

	public void setMediaLocation(String mediaLocation,
			boolean startAutomatically) throws NoDataSourceException,
			NoPlayerException, IOException {

		logger.fine("setMediaLocation: " + mediaLocation
				+ " startAutomatically=" + startAutomatically);

		try {
			MediaLocator locator = new MediaLocator(mediaLocation);
			stop();
			if (player != null) {
				player.close();
				player.deallocate();
				player = null;
			}
			notifyStatusListener(ContainerPlayerStatusListener.LOADING);
			if (startAutomatically)
				shouldStartOnRealize = true;
			createNewPlayer(locator);
		}
		// catch (NoDataSourceException e)
		// {
		// setMediaLocationFailed = true;
		// notifyStatusListener(ContainerPlayerStatusListener.CREATE_PLAYER_FAILED);
		// throw e;
		// }
		catch (NoPlayerException e) {
			notifyStatusListener(ContainerPlayerStatusListener.CREATE_PLAYER_FAILED);
			throw e;
		} catch (IOException e) {
			notifyStatusListener(ContainerPlayerStatusListener.CREATE_PLAYER_FAILED);
			throw e;
		} catch (RuntimeException e) {
			notifyStatusListener(ContainerPlayerStatusListener.CREATE_PLAYER_FAILED);
			throw e;
		}
	}

	/**
	 * Used in the case of things like RTP wizard, which create a
	 * player/processor explicitly.
	 */
	public void setRealizedStartedProcessor(Processor p) {
		stop();
		if (player != null) {
			player.close();
			player.deallocate();
			player = null;
		}
		notifyStatusListener(ContainerPlayerStatusListener.PROCESSING);

		useExistingRealizedStartedPlayer(p);
	}

	/**
	 * 
	 */
	public void start() {
		if (player != null) {
			shouldStartOnRealize = false;
			player.start();
			// // copied from EJMF StandardStartControl
			// final int state = player.getState();
			//
			// if (state == Controller.Started)
			// return;
			//
			// if (state < Controller.Prefetched) {
			// StateWaiter w = new StateWaiter(player);
			// w.blockingPrefetch();
			// }
			//
			// final TimeBase tb = player.getTimeBase();
			// player.syncStart(tb.getTime());
		} else {
			shouldStartOnRealize = true;
		}
	}

	/**
	 * 
	 */
	public void stop() {
		shouldStartOnRealize = false;
		if (player != null) {
			player.stop();
		}
	}

	public void setPosition(double seconds) {
		setMediaTime(new Time(seconds));
	}

	public void setRate(float rate) {
		if (player != null) {
			player.setRate(rate);
		}
	}

	/**
	 * Deallocate all resources used by the player.
	 */
	public void deallocate() {
		shouldStartOnRealize = false;
		if (player != null) {
			player.deallocate();
			player = null;
		}
	}

	/**
	 * Deallocate resources and close the player.
	 */
	public void close() {
		shouldStartOnRealize = false;
		if (player != null) {
			player.close();
		}
	}

	/**
	 * Set the current media time of the content.
	 */
	public void setMediaTime(Time time) {
		if (player != null) {
			player.setMediaTime(time);
		}
	}

	/* --------------- private methods ------------------- */

	private void createNewPlayer(MediaLocator source) throws NoPlayerException,
			IOException {
		if (!ClasspathChecker.check()) {
			// workaround because JMF does not like relative file URLs.
			if (source.getProtocol().equals("file")) {
				final String newUrl = URLUtils.createAbsoluteFileUrl(source
						.toExternalForm());
				if (newUrl != null) {
					final MediaLocator newSource = new MediaLocator(newUrl);
					if (!source.toExternalForm().equals(
							newSource.toExternalForm())) {
						logger
								.warning("Changing file URL to absolute for JMF, from "
										+ source.toExternalForm()
										+ " to "
										+ newSource);
						source = newSource;
					}
				}
			}
		}

		player = javax.media.Manager.createPlayer(source);

		transportControlState.setAllowPlay(true);
		transportControlState.setAllowStop(false);
		transportControlState.setAllowVolume(false);

		if (transportControlListener != null)
			transportControlListener.onStateChange(transportControlState);

		// Setup timer
		controlTimer = new SourcedTimer(this, TIMER_TICK);
		controlTimer.addSourcedTimerListener(this);

		if (player.getState() == Controller.Started) {
			if (transportControlListener != null)
				transportControlListener.onDurationChange(player.getDuration()
						.getNanoseconds());

			controlTimer.start(); // this handles the case where it is already
									// started before we get here, in which case
									// controllerUpdate will never get called
									// with the initial state
		}

		player.addControllerListener(controllerListener);
		player.realize();
	}

	private void useExistingRealizedStartedPlayer(Player p) {
		player = p;

		transportControlState.setAllowPlay(false);
		transportControlState.setAllowStop(true);
		transportControlState.setAllowVolume(false);

		if (transportControlListener != null)
			transportControlListener.onStateChange(transportControlState);

		// Setup timer
		controlTimer = new SourcedTimer(this, TIMER_TICK);
		controlTimer.addSourcedTimerListener(this);

		if (player.getState() == Controller.Started) {
			if (transportControlListener != null)
				transportControlListener.onDurationChange(player.getDuration()
						.getNanoseconds());

			controlTimer.start(); // this handles the case where it is already
									// started before we get here, in which case
									// controllerUpdate will never get called
									// with the initial state
		}

		player.addControllerListener(controllerListener);

	}

	/**
	 * Set the visdual component.
	 * 
	 * @param newVisualComponent
	 */
	private void setVisualComponent(final Component newVisualComponent) {
		if (this.visualComponent == newVisualComponent) {
			return;
		}

		if (getMouseListener() != null) {
			newVisualComponent.addMouseListener(getMouseListener());
		}

		Runnable runnable = new Runnable() {
			public void run() {
				if (ContainerPlayer.this.visualComponent != null)
					getContainer().remove(ContainerPlayer.this.visualComponent);
				ContainerPlayer.this.visualComponent = newVisualComponent;
				if (newVisualComponent != null)
					getContainer().add(newVisualComponent);
				getContainer().validate();
			}
		};
		SwingUtilities.invokeLater(runnable);

	}

	private void setContainer(Container container) {
		this.container = container;
	}

	private Container getContainer() {
		return container;
	}

	public void setMouseListener(MouseListener mouseListener) {
		this.mouseListener = mouseListener;
	}

	private MouseListener getMouseListener() {
		return mouseListener;
	}

	private TransportControlListener transportControlListener;

	public void setTransportControlListener(TransportControlListener listener) {
		transportControlListener = listener;
	}

	private ContainerPlayerStatusListener statusListener;

	public void setContainerPlayerStatusListener(
			ContainerPlayerStatusListener listener) {
		statusListener = listener;
	}

	/**
	 * This method implements the SourcedTimerListener interface. Each timer
	 * tick causes slider thumbnail to move if a ProgressBar was built for this
	 * control panel.
	 * 
	 * @see net.sf.fmj.ejmf.toolkit.util.SourcedTimer
	 */
	public void timerUpdate(SourcedTimerEvent e) {
		// Since we are also the TimeSource, we can get
		// directly from StandardControls instance.
		// Normally, one would call e.getTime().

		if (transportControlListener != null)
			transportControlListener.onProgressChange(getTime());

	}

	// For TimeSource interface
	/**
	 * As part of TimeSource interface, getTime returns the current media time
	 * in nanoseconds.
	 */
	public long getTime() {
		if (player == null)
			return 0L;
		return player.getMediaNanoseconds();
	}

	/**
	 * This method is used as a divisor to convert getTime to seconds.
	 */
	public long getConversionDivisor() {
		return TimeSource.NANOS_PER_SEC;
	}

	public void setGain(float value) {
		if (player != null && player.getGainControl() != null) {
			player.getGainControl().setLevel(value);
		}
	}

	public void setMute(boolean value) {
		if (player != null && player.getGainControl() != null) {
			player.getGainControl().setMute(value);
		}
	}

	public boolean isAutoLoop() {
		return autoLoop;
	}

	public void setAutoLoop(boolean autoLoop) {
		this.autoLoop = autoLoop;
	}

}