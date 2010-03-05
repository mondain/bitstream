package net.sf.fmj.ui;

import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JSlider;

import net.sf.fmj.media.RegistryDefaults;
import net.sf.fmj.ui.application.PlayerPanel;
import net.sf.fmj.ui.dialogs.AboutPanel;
import net.sf.fmj.ui.registry.RegistryEditorPanel;
import net.sf.fmj.utility.ClasspathChecker;
import net.sf.fmj.utility.JmfUtility;
import net.sf.fmj.utility.LoggerSingleton;
import net.sf.fmj.utility.OSUtils;

/**
 * Main class for the FMJ media player application.
 * 
 * @author stormboy
 * 
 */
public class FmjStudio {
	private static final Logger logger = LoggerSingleton.logger;

	private JFrame frame;
	private JFrame registryFrame;
	private PlayerPanel playerPanel;

	/**
	 * 
	 * @param args
	 */
	private void run(String[] args) {
		frame = new JFrame("FMJ Studio");
		frame.setSize(new Dimension(640, 480));
		// frame.setDefaultCloseOperation(JFrame.);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				frame.dispose();
				if (playerPanel != null) {
					playerPanel.getPlayer().stop();
				}
			}
		});

		Container contentPane = frame.getContentPane();
		playerPanel = new PlayerPanel();

		contentPane.setLayout(new BorderLayout());
		contentPane.add(playerPanel, BorderLayout.CENTER);
		// JSlider jSlider = new JSlider();
		// contentPane.add(jSlider, BorderLayout.SOUTH);

		// frame.setMenuBar(getMenuBar());

		// Resize frame whenever new Component is added
		playerPanel.getVideoPanel().addContainerListener(
				new ContainerListener() {
					public void componentAdded(ContainerEvent e) {
						frame.pack();
					}

					public void componentRemoved(ContainerEvent e) {
						frame.pack();
					}
				});

		frame.setVisible(true);
		frame.setResizable(false);
		
		if (args.length > 0) {
			// URL is first arg
			final String url = args[0];
			playerPanel.addMediaLocatorAndLoad(url);
		}
	}

	private final MenuBar getMenuBar() {
		MenuBar menuBar = new MenuBar();

		final Menu menuFile = new Menu();
		menuFile.setLabel("File");
		menuBar.add(menuFile);

		// File>Open File
		{
			final MenuItem menuItemOpenFile = new MenuItem("Open File...");
			menuItemOpenFile.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					playerPanel.onOpenFile();
				}
			});
			menuFile.add(menuItemOpenFile);
		}

		// File>Open URL
		{
			final MenuItem menuItemOpenURL = new MenuItem("Open URL...");
			menuItemOpenURL.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					playerPanel.onOpenURL();
				}
			});
			menuFile.add(menuItemOpenURL);
		}

		// File>Open RTP Session
		{
			final MenuItem menuItemReceiveRTP = new MenuItem(
					"Open RTP Session...");
			menuItemReceiveRTP.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					playerPanel.onReceiveRTP();
				}
			});
			menuFile.add(menuItemReceiveRTP);
		}

		// File>Capture
		{
			final MenuItem menuItemOpenCaptureDevice = new MenuItem(
					"Capture...");
			menuItemOpenCaptureDevice.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					playerPanel.onOpenCaptureDevice();
				}
			});
			menuFile.add(menuItemOpenCaptureDevice);
		}

		// separator
		menuFile.addSeparator();

		// TODO: export

		// File>Transmit RTP
		{
			final MenuItem menuItemTransmitRTP = new MenuItem("Transmit RTP...");
			menuItemTransmitRTP.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					playerPanel.onTransmitRTP();
				}
			});
			menuFile.add(menuItemTransmitRTP);
		}

		// File>Transcode
		{
			final MenuItem menuItemTranscode = new MenuItem("Transcode...");
			menuItemTranscode.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					playerPanel.onTranscode();
				}
			});
			menuFile.add(menuItemTranscode);
		}

		// separator
		menuFile.addSeparator();

		// File>Registry Editor
		{
			final MenuItem menuItemRegistryEditor = new MenuItem(
					"Registry Editor...");
			menuItemRegistryEditor.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onOpenRegistryEditor();
				}
			});
			menuFile.add(menuItemRegistryEditor);
		}

		// File>Exit
		{
			final MenuItem menuItemExit = new MenuItem("Exit");
			menuItemExit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onExit();
				}
			});
			menuFile.add(menuItemExit);
		}

		final Menu menuPlayer = new Menu();
		menuPlayer.setLabel("Player");
		menuBar.add(menuPlayer);

		// Player>Auto-play
		{
			final CheckboxMenuItem menuItemAutoPlay = new CheckboxMenuItem(
					"Auto-play");
			menuItemAutoPlay.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					playerPanel.onAutoPlay(menuItemAutoPlay.getState());
				}

			});

			menuPlayer.add(menuItemAutoPlay);
			menuItemAutoPlay.setState(playerPanel.getPrefs().autoPlay);
		}

		// Player>Auto-loop
		{
			final CheckboxMenuItem menuItemAutoLoop = new CheckboxMenuItem(
					"Auto-loop");
			menuItemAutoLoop.addItemListener(new ItemListener() {

				public void itemStateChanged(ItemEvent e) {
					playerPanel.onAutoLoop(menuItemAutoLoop.getState());
				}

			});
			menuPlayer.add(menuItemAutoLoop);
			playerPanel.onAutoLoop(false); // TODO: not working right yet.
			menuItemAutoLoop.setState(playerPanel.getPrefs().autoLoop);
			menuItemAutoLoop.setEnabled(false); // TODO: not working right yet.

		}

		final Menu menuHelp = new Menu();
		menuHelp.setLabel("Help");
		menuBar.add(menuHelp);

		// Help>About
		{
			final MenuItem menuItemHelpAbout = new MenuItem("About...");
			menuItemHelpAbout.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					AboutPanel.run(frame);
				}
			});

			menuHelp.add(menuItemHelpAbout);

		}

		return menuBar;
	}

	/**
	 * Display the registry editor frame
	 * 
	 */
	private void onOpenRegistryEditor() {
		if (registryFrame == null) {
			registryFrame = new JFrame("Registry Editor");
			RegistryEditorPanel panel = new RegistryEditorPanel();

			Container contentPane = registryFrame.getContentPane();
			contentPane.setLayout(new BorderLayout());
			contentPane.add(panel, BorderLayout.CENTER);

			// frame.setMinimumSize(new Dimension(480, 320)); // doesn't seem to
			// have any effect (at least in linux), and is not 1.4-compatible
			// anyway.
			registryFrame.setSize(640, 480);
		}
		registryFrame.setVisible(true);
	}

	private void onExit() {
		if (registryFrame != null)
			registryFrame.dispose();
		frame.dispose();
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 *            the arguments pased to this application
	 */
	public static void main(String[] args) {
		try {

			System.setProperty("java.util.logging.config.file",
					"logging.properties");
			LogManager.getLogManager().readConfiguration();

			if (!ClasspathChecker.checkAndWarn()) {
				// JMF is ahead of us in the classpath. Let's do some things to
				// make this go more smoothly.
				logger.info("Enabling JMF logging");
				if (!JmfUtility.enableLogging())
					logger.warning("Failed to enable JMF logging");

				// Let's register our own prefixes, etc, since they won't
				// generally be if JMF is in charge.
				logger.info("Registering FMJ prefixes and plugins with JMF");
				RegistryDefaults.registerAll(RegistryDefaults.FMJ);
				// RegistryDefaults.unRegisterAll(RegistryDefaults.JMF); //
				// TODO: this can be used to make some things that work in FMJ
				// but not in JMF, work, like streaming mp3/ogg.
				// TODO: what about the removal of some/reordering?
			}

			// see http://developer.apple.com/technotes/tn/tn2031.html
			// see
			// http://java.sun.com/developer/technicalArticles/JavaLP/JavaToMac/
			// It doesn't seem to work to set these in code, they have to be set
			// by the calling environment
			if (OSUtils.isMacOSX()) {
				System.setProperty(
						"com.apple.mrj.application.apple.menu.about.name",
						"FMJ Studio");
				// System.setProperty("com.apple.mrj.application.growbox.intrudes",
				// "false"); // doesn't seem to work
			}

			//
			FmjStudio main = new FmjStudio();
			main.run(args);
		} catch (Throwable t) {
			logger.log(Level.WARNING, "" + t, t);
		}
	}

}
