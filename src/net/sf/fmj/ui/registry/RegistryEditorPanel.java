package net.sf.fmj.ui.registry;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import net.sf.fmj.utility.ClasspathChecker;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * 
 * @author Warren Bloomer
 * 
 */
public class RegistryEditorPanel extends JPanel {

	private static final Logger logger = LoggerSingleton.logger;

	private JTabbedPane registryTabbedPane = null;
	private PluginTypesPanel pluginsPanel = null;
	// private UserSettingsPanel userSettingsPanel = null;
	private CaptureDevicePanel captureDevicePanel = null;
	private MimeTypesPanel mimeTypesPanel = null;
	private PackagesPanel packagesPanel = null;

	/**
	 * This method initializes captureDevicePanel
	 * 
	 * @return net.sf.fmj.ui.registry.CaptureDevicePanel
	 */
	private CaptureDevicePanel getCaptureDevicePanel() {
		if (captureDevicePanel == null) {
			captureDevicePanel = new CaptureDevicePanel();
		}
		return captureDevicePanel;
	}

	/**
	 * This method initializes mimeTypesPanel
	 * 
	 * @return net.sf.fmj.ui.registry.MimeTypesPanel
	 */
	private MimeTypesPanel getMimeTypesPanel() {
		if (mimeTypesPanel == null) {
			mimeTypesPanel = new MimeTypesPanel();
		}
		return mimeTypesPanel;
	}

	/**
	 * This method initializes packagesPanel
	 * 
	 * @return net.sf.fmj.ui.registry.PackagesPanel
	 */
	private PackagesPanel getPackagesPanel() {
		if (packagesPanel == null) {
			packagesPanel = new PackagesPanel();
		}
		return packagesPanel;
	}

	public static void main(String[] args) {

		System.setProperty("java.util.logging.config.file",
				"logging.properties");
		try {
			LogManager.getLogManager().readConfiguration();
		} catch (Exception e) {
			logger.log(Level.WARNING, "" + e, e);
		}

		ClasspathChecker.checkAndWarn();

		if (false) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				logger.log(Level.WARNING, "Unable to set Swing look and feel: "
						+ e, e);

			}
		}

		try {
			RegistryEditorPanel panel = new RegistryEditorPanel();

			JFrame frame = new JFrame("Registry Editor");
			Container contentPane = frame.getContentPane();
			contentPane.setLayout(new BorderLayout());
			contentPane.add(panel, BorderLayout.CENTER);

			// frame.setMinimumSize(new Dimension(480, 320)); // doesn't seem to
			// have any effect (at least in linux), and is not 1.4-compatible
			// anyway.
			frame.setSize(640, 480);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setVisible(true);
		} catch (Exception e) {
			logger.log(Level.WARNING, "" + e, e);
		}
	}

	// /**
	// * This method initializes userSettingsPanel
	// *
	// * @return net.sf.fmj.ui.registry.UserSettingsPanel
	// */
	// private UserSettingsPanel getUserSettingsPanel() {
	// if (userSettingsPanel == null) {
	// userSettingsPanel = new UserSettingsPanel();
	// }
	// return userSettingsPanel;
	// }

	/**
	 * This method initializes
	 * 
	 */
	public RegistryEditorPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setLayout(new BorderLayout());
		this.setSize(new Dimension(530, 320));
		this.add(getRegistryTabbedPane(), BorderLayout.CENTER);

	}

	/**
	 * This method initializes registryTabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getRegistryTabbedPane() {
		if (registryTabbedPane == null) {
			registryTabbedPane = new JTabbedPane();
			registryTabbedPane.setPreferredSize(new Dimension(320, 240));
			// registryTabbedPane.addTab("User Settings", null,
			// getUserSettingsPanel(), null);
			registryTabbedPane.addTab("Capture Devices", null,
					getCaptureDevicePanel(), null);
			registryTabbedPane.addTab("PlugIns", null, getPluginsPanel(), null);
			registryTabbedPane.addTab("MIME Types", null, getMimeTypesPanel(),
					null);
			registryTabbedPane.addTab("Packages", null, getPackagesPanel(),
					null);
		}
		return registryTabbedPane;
	}

	/**
	 * This method initializes pluginsPanel
	 * 
	 * @return net.sf.fmj.ui.registry.PluginsPanel
	 */
	private PluginTypesPanel getPluginsPanel() {
		if (pluginsPanel == null) {
			pluginsPanel = new PluginTypesPanel();
		}
		return pluginsPanel;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
