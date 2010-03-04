package net.sf.fmj.ejmf.toolkit.util;

import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.swing.JApplet;
import javax.swing.JFrame;

import net.sf.fmj.media.RegistryDefaults;
import net.sf.fmj.utility.ClasspathChecker;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * The PlayerDriver class provides a basis for displaying a Java Media Player as
 * either an applet or an application.
 * 
 * From the book: Essential JMF, Gordon, Talley (ISBN 0130801046). Used with
 * permission.
 * 
 * @author Steve Talley & Rob Gordon
 */
public abstract class PlayerDriver extends JApplet {

	private static final Logger logger = LoggerSingleton.logger;

	private JFrame frame; // only used if not running as applet
	private PlayerPanel playerpanel;

	/**
	 * Constructs a PlayerDriver.
	 */
	public PlayerDriver() {
		// Swing-endorsed hack
		getRootPane().putClientProperty("defeatSystemEventQueueCheck",
				Boolean.TRUE);
	}

	/**
	 * An abstract method that should be overridden to begin the playback of the
	 * media once the GUI layout has taken place.
	 */
	public abstract void begin();

	/**
	 * This method is run when PlayerDriver is an applet.
	 */
	public void init() {
		// JLabel running =
		// new JLabel("Media Applet Running", JLabel.CENTER);
		//
		// running.setBorder( BorderConstants.etchedBorder );
		// getContentPane().add(running);
		// pack();

		if (!ClasspathChecker.checkAndWarn()) {
			// JMF is ahead of us in the classpath. Let's register our own
			// prefixes, etc.
			logger.warning("Registering FMJ prefixes and plugins with JMF");
			RegistryDefaults.registerAll(RegistryDefaults.FMJ);
			// RegistryDefaults.unRegisterAll(RegistryDefaults.JMF); // TODO:
			// this can be used to make some things that work in FMJ but not in
			// JMF, work, like streaming mp3/ogg.
			// this will also make the GUI be provided by FMJ, not JMF
		}

		String media;

		// Get the media filename
		if ((media = getParameter("MEDIA")) == null) {
			logger.warning("Error: MEDIA parameter not specified");
			return;
		}

		MediaLocator locator = Utility.appletArgToMediaLocator(this, media);

		try {
			// initialize will open a new window.
			// initialize(locator);
			playerpanel = new PlayerPanel(locator);

			// Resize whenever new Component is added
			playerpanel.getMediaPanel().addContainerListener(
					new ContainerListener() {
						public void componentAdded(ContainerEvent e) {
							pack();
						}

						public void componentRemoved(ContainerEvent e) {
							pack();
						}
					});

			getContentPane().add(playerpanel);
			pack();
			begin();
		}

		catch (IOException e) {
			logger.warning("Could not connect to media");
			destroy();
		}

		catch (NoPlayerException e) {
			logger.warning("Player not found for media");
			destroy();
		}
	}

	/**
	 * This method is called by browser when page is left. Player must close or
	 * else it will continue to render even after having left the page. This can
	 * be an issue with audio.
	 */
	public void destroy() {
		super.destroy();
		if (getPlayerPanel() != null && getPlayerPanel().getPlayer() != null) {
			getPlayerPanel().getPlayer().stop();
			getPlayerPanel().getPlayer().close();
		}
	}

	/**
	 * Should be called by the real main() once a PlayerDriver has been
	 * constructed for the given arguments.
	 */
	public static void main(PlayerDriver driver, String args[]) {
		// Get the media filename
		if (args.length == 0) {
			logger.warning("Media parameter not specified");
			return;
		}

		MediaLocator locator = Utility.appArgToMediaLocator(args[0]);

		try {
			driver.initialize(locator);
		}

		catch (IOException e) {
			logger.log(Level.WARNING, "Could not connect to media: " + e, e);
			System.exit(1);
		}

		catch (NoPlayerException e) {
			logger.log(Level.WARNING, "Player not found for media: " + e, e);
			System.exit(1);
		}
	}

	/**
	 * Initializes the PlayerDriver with the given MediaLocator.
	 * 
	 * @exception IOException
	 *                If an I/O error occurs while accessing the media.
	 * 
	 * @exception NoPlayerException
	 *                If a Player cannot be created from the given MediaLocator.
	 */
	public void initialize(MediaLocator locator) throws IOException,
			NoPlayerException {
		playerpanel = new PlayerPanel(locator);

		frame = new JFrame(locator.toString());

		// Allow window to close
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		// Resize frame whenever new Component is added
		playerpanel.getMediaPanel().addContainerListener(
				new ContainerListener() {
					public void componentAdded(ContainerEvent e) {
						frame.pack();
					}

					public void componentRemoved(ContainerEvent e) {
						frame.pack();
					}
				});

		Container c = frame.getContentPane();
		c.add(playerpanel);

		frame.pack();
		frame.setVisible(true);

		// Execute implementation-specific functionality
		begin();
	}

	/**
	 * Gets the Frame in which the media is being displayed.
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Gets the PlayerPanel for this PlayerDriver.
	 */
	public PlayerPanel getPlayerPanel() {
		return playerpanel;
	}

	public void pack() {
		setSize(getPreferredSize());
		validate();
	}

	/**
	 * Redraws the Frame containing the media.
	 */
	public void redraw() {
		frame.pack();
	}
}
