package net.sf.fmj.utility;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.media.CaptureDeviceInfo;

import net.sf.fmj.media.RegistryDefaults;

/**
 * This is a registry of Plugins, Protocol prefixes, Content prefixes, and MIME
 * types. The registry may be serialized to an XML. The XML file is nominally
 * located in ${user.home}/.fmj.registry.xml
 * 
 * This object is used by the PackageManager and the PluginManager for
 * persisting data across sessions.
 * 
 * Currently the Registry does not store the supported input and output formats
 * for Plugins. This may be supported by adding CDATA sections that are
 * serialized Format objects. However, it would be good to be able to clear the
 * stored formats, and refresh the supported formats by introspecting the
 * Plugins. Sometimes the installed plugins may be updated, and the list of
 * supported formats may change for the same plugin class.
 * 
 * Nevertheless, the present situation is that the PluginManager will need to
 * determine supported formats upon loading informatin from this Registry.
 * 
 * TODO separate the persistence mechanism from this object, so that it may be
 * updated/plugged-in. TODO perhaps remove reliance on JDOM. Although JDOM makes
 * it easy to program, it is another jar to ship.
 * 
 * @author Warren Bloomer
 * @author Ken Larson
 * 
 */
public class Registry {

	/** logger for this class */
	private static final Logger logger = LoggerSingleton.logger;

	/** the singleton registry object */
	private static final Registry registry = new Registry();

	private final RegistryContents registryContents = new RegistryContents();

	/**
	 * Get the singleton.
	 * 
	 * @return The singleton JmfRegistry object.
	 */
	public static Registry getInstance() {
		return registry;
	}

	// for unit tests, add
	// -Dnet.sf.fmj.utility.JmfRegistry.disableLoad=true
	// -Dnet.sf.fmj.utility.JmfRegistry.JMFDefaults=true

	/**
	 * Private constructor.
	 */
	private Registry() {

		try {
			if (System.getProperty(
					"net.sf.fmj.utility.JmfRegistry.disableLoad", "false")
					.equals("true")) {
				setDefaults(); // this capability needed for unit tests.
				return;
			}
		} catch (SecurityException e) { // ignore, we must be in an applet.
		}

		try {
			final File f = getRegistryFile();
			if (!f.exists()) {
				logger
						.fine("JMF registry file does not exist.  Using defaults.");
				setDefaults();
				return;
			}
			FileReader reader = new FileReader(f);
			new RegistryIO(registryContents).load(reader);
		} catch (Throwable e) {
			logger.warning("Problem loading JMF registry: " + e.getMessage()
					+ ".  Using defaults.");
			setDefaults();
		}

	}

	/**
	 * Write the registry to file.
	 * 
	 */
	public synchronized void commit() throws IOException {
		// write to registry file
		FileWriter fileWriter = new FileWriter(getRegistryFile());
		new RegistryIO(registryContents).write(fileWriter);
		fileWriter.flush();
		fileWriter.close();
	}

	/* ---------------- for PluginManager ---------------------- */

	/**
	 * pluginType = [0..4]
	 */
	public synchronized List<String> getPluginList(int pluginType) {

		// get the list of plugins of the given type
		final Vector<String> pluginList = registryContents.plugins[pluginType - 1];

		return (List<String>) pluginList.clone();
	}

	/**
	 * Plugin list of PluginInfo objects = { classname, inputFormats,
	 * outputFormats, pluginType};
	 * 
	 * @param pluginType
	 *            range of [0..4]
	 * @param plugins
	 */
	public synchronized void setPluginList(int pluginType, List<String> plugins) {
		// use the plugin vector for the given type
		final Vector<String> pluginList = registryContents.plugins[pluginType - 1];
		pluginList.clear();
		pluginList.addAll(plugins);
	}

	/* --------- for PackageManager ----------------- */

	/**
	 * Prefices for determining URL Handlers for content delivered via
	 * particular protocol.
	 * 
	 * Protocols are converted to package names, e.g. "http" -> "http" These
	 * package names are added to the prefices in this list to determine
	 * Handlers for them. i.e. "<i>prefix</i>.media.protocol.http.Handler"
	 * 
	 * TODO perhaps use URLStreamHandlers
	 * 
	 */
	public synchronized void setProtocolPrefixList(List<String> list) {
		if (!list.contains("javax")) {
			list.add("javax");
		}
		registryContents.protocolPrefixList.clear();
		registryContents.protocolPrefixList.addAll(list);
	}

	public synchronized Vector<String> getProtocolPrefixList() {
		return (Vector<String>) registryContents.protocolPrefixList.clone();
	}

	/**
	 * Prefices for determining Handlers for content of particular MIME types.
	 * 
	 * MIME types are converted to package names, e.g. text/html -> text.html
	 * 
	 * These package names are added to the prefices in this list to determine
	 * Handlers for them. i.e. "<i>prefix</i>.media.content.text.html.Handler"
	 */
	public synchronized void setContentPrefixList(List<String> list) {
		if (!list.contains("javax")) {
			list.add("javax");
		}
		registryContents.contentPrefixList.clear();
		registryContents.contentPrefixList.addAll(list);
	}

	public synchronized Vector<String> getContentPrefixList() {
		return (Vector<String>) registryContents.contentPrefixList.clone();
	}

	/* ---------------- for mime-type --------------------- */

	public synchronized void addMimeType(String extension, String type) {
		registryContents.mimeTable.addMimeType(extension, type);
	}

	public synchronized String getMimeType(String extension) {
		return registryContents.mimeTable.getMimeType(extension);
	}

	public synchronized String getDefaultExtension(String mimeType) {
		return registryContents.mimeTable.getDefaultExtension(mimeType);
	}

	public synchronized Hashtable getMimeTable() {
		return registryContents.mimeTable.getMimeTable();
	}

	public synchronized boolean removeMimeType(String fileExtension) {
		return registryContents.mimeTable.removeMimeType(fileExtension);
	}

	public synchronized List getExtensions(String mimeType) {
		return registryContents.mimeTable.getExtensions(mimeType);
	}

	/* ---------------- for CaptureDeviceManager --------------------- */

	public synchronized Vector<CaptureDeviceInfo> getDeviceList() {
		return (Vector<CaptureDeviceInfo>) registryContents.captureDeviceInfoList
				.clone();
	}

	public synchronized boolean addDevice(CaptureDeviceInfo newDevice) {
		return registryContents.captureDeviceInfoList.add(newDevice);

	}

	public synchronized boolean removeDevice(CaptureDeviceInfo device) {
		return registryContents.captureDeviceInfoList.remove(device);
	}

	/* ------------------------- defaults ------------------------- */

	private void setDefaults() {
		// com.sun and com.ibm are added only for compatibility with the
		// reference implementation.
		// FMJ does not provide any of the sun or ibm implementations.

		// FMJ's prefix can be not added by default if the system property
		// net.sf.fmj.utility.JmfRegistry.JMFDefaults is set to true;

		boolean jmfDefaults = false;

		try {
			jmfDefaults = System.getProperty(
					"net.sf.fmj.utility.JmfRegistry.JMFDefaults", "false")
					.equals("true");
		} catch (SecurityException e) { // we must be an applet.
		}

		final int flags = jmfDefaults ? RegistryDefaults.JMF
				: RegistryDefaults.ALL;

		registryContents.protocolPrefixList.addAll(RegistryDefaults
				.protocolPrefixList(flags));
		registryContents.contentPrefixList.addAll(RegistryDefaults
				.contentPrefixList(flags));

		// TODO: get the plugins and set them.
		// RegistryDefaults.registerPlugins(flags);
	}

	/**
	 * Return the filepath of the registry file.
	 */
	private File getRegistryFile() {
		/** the name of the file used to store the registry */
		final String filename = System.getProperty(
				"net.sf.fmj.utility.JmfRegistry.filename", ".fmj.registry.xml"); // allow
																					// override
		String home = System.getProperty("user.home");
		return new File(home + File.separator + filename);
	}

}
