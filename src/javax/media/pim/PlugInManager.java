package javax.media.pim;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Codec;
import javax.media.Demultiplexer;
import javax.media.Effect;
import javax.media.Format;
import javax.media.Multiplexer;
import javax.media.Renderer;

import net.sf.fmj.media.RegistryDefaults;
import net.sf.fmj.utility.Registry;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * In progress.
 * 
 * @author Ken Larson
 * 
 */
public class PlugInManager extends javax.media.PlugInManager {
	private static final Logger logger = LoggerSingleton.logger;

	private static boolean TRACE = false;

	// TODO: what exactly is stored in the properties? just the class names, or
	// the formats as well?
	// the properties files appears to be binary, an appears to be created using
	// java serialization.
	// seems like it contains the formats.

	// TODO: implement efficiently using maps

	/**
	 * Vectors of classname Strings. This is sorted in the order that plugins
	 * must be searched.
	 */
	private static final Vector[] classLists = new Vector[] { new Vector(),
			new Vector(), new Vector(), new Vector(), new Vector() };

	/**
	 * Maps of classnames to PluginInfo
	 */
	private static final HashMap[] pluginMaps = new HashMap[] { new HashMap(),
			new HashMap(), new HashMap(), new HashMap(), new HashMap(), };

	/**
	 * The registry that persists the data.
	 */
	private static Registry registry = Registry.getInstance();

	static {
		try {
			if (TRACE)
				logger.info("initializing...");

			// populate vectors with info from the persisted registry
			for (int i = 0; i < 5; i++) {
				Vector classList = classLists[i];
				HashMap pluginMap = pluginMaps[i];

				Iterator pluginIter = registry.getPluginList(i + 1).iterator();
				while (pluginIter.hasNext()) {
					// PlugInInfo info = (PlugInInfo) pluginIter.next();
					// classList.add(info.className);
					// pluginMap.put(info.className, info);

					// registry only contains classnames, not in and out formats
					String classname = (String) pluginIter.next();
					classList.add(classname);
					PlugInInfo info = getPluginInfo(classname);
					if (info != null) {
						pluginMap.put(info.className, info);
					}
				}
			}

			boolean jmfDefaults = false;

			try {
				jmfDefaults = System.getProperty(
						"net.sf.fmj.utility.JmfRegistry.JMFDefaults", "false")
						.equals("true");
			} catch (SecurityException e) { // we must be an applet.
			}

			final int flags = jmfDefaults ? RegistryDefaults.JMF
					: RegistryDefaults.ALL;
			RegistryDefaults.registerPlugins(flags);

		} catch (Throwable t) {
			logger.log(Level.SEVERE,
					"Unable to initialize javax.media.pim.PlugInManager (static): "
							+ t, t);
			throw new RuntimeException(t);
		}
	}

	/**
	 * Private constructor so that is can not be constructed. In JMF it is
	 * public, but this is an implementation detail that is not important for
	 * FMJ compatibility.
	 */
	private PlugInManager() {
	}

	/**
	 * Get a list of plugins that match the given input and output formats.
	 * 
	 * @param input
	 * @param output
	 * @param type
	 * @return A Vector of classnames
	 */
	public static synchronized Vector<String> getPlugInList(Format input,
			Format output, int type) {
		if (TRACE)
			logger.info("getting plugin list...");
		if (!isValid(type)) {
			return new Vector<String>();
		}

		final Vector<String> result = new Vector<String>();
		final Vector<String> classList = getVector(type);
		final HashMap pluginMap = pluginMaps[type - 1];

		for (int i = 0; i < classList.size(); ++i) {
			final Object classname = classList.get(i);
			final PlugInInfo plugInInfo = (PlugInInfo) pluginMap.get(classname);
			if (plugInInfo == null)
				continue;

			if (input != null) {
				if (plugInInfo.inputFormats == null) {
					continue;
				}
				boolean match = false;
				for (int j = 0; j < plugInInfo.inputFormats.length; ++j) {
					if (input.matches(plugInInfo.inputFormats[j])) {
						match = true;
						break;
					}
				}
				if (!match) {
					continue;
				}
			}

			if (output != null) {
				if (plugInInfo.outputFormats == null) {
					continue;
				}
				boolean match = false;
				for (int j = 0; j < plugInInfo.outputFormats.length; ++j) {
					if (output.matches(plugInInfo.outputFormats[j])) {
						match = true;
						break;
					}
				}
				if (!match) {
					continue;
				}
			}

			// matched both input and output formats
			result.add(plugInInfo.className);
		}

		return result;
	}

	/**
	 * according to the docs, sets the search order. does not appear to add new
	 * plugins.
	 * 
	 * @param plugins
	 * @param type
	 */
	public static synchronized void setPlugInList(Vector plugins, int type) {
		// the vector to affect
		Vector vector = classLists[type - 1];

		// The following code does not appear to be consistent with JMF. More
		// testing needed:
		// if ( vector.size() != plugins.size() || !vector.containsAll(plugins)
		// ) {
		// // extra or missing classname(s) given
		// logger.warning("setPlugInList: extra or missing classname(s) given");
		// return;
		// }

		// reorder vector
		vector.clear();
		vector.addAll(plugins);

		registry.setPluginList(type, plugins);
	}

	public static synchronized void commit() throws java.io.IOException {
		registry.commit();
	}

	public static synchronized boolean addPlugIn(String classname, Format[] in,
			Format[] out, int type) {
		try {
			Class.forName(classname);
		} catch (ClassNotFoundException e) {
			logger
					.finer("addPlugIn failed for nonexistant class: "
							+ classname);
			return false; // class does not exist.
		} catch (Throwable t) {
			logger.log(Level.WARNING, "Unable to addPlugIn for " + classname
					+ " due to inability to get its class: " + t, t);
			return false;
		}

		if (find(classname, type) != null) {
			return false; // already there.
		}

		final PlugInInfo plugInInfo = new PlugInInfo(classname, in, out);

		Vector classList = classLists[type - 1];
		HashMap pluginMap = pluginMaps[type - 1];

		// add to end of ordered list
		classList.add(classname);

		// add to PluginInfo map
		pluginMap.put(classname, plugInInfo);

		registry.setPluginList(type, classList);

		return true;
	}

	public static synchronized boolean removePlugIn(String classname, int type) {
		Vector classList = classLists[type - 1];
		HashMap pluginMap = pluginMaps[type - 1];

		boolean result = classList.remove(classname)
				|| (pluginMap.remove(classname) != null);

		registry.setPluginList(type, classList);

		return result;
	}

	public static synchronized Format[] getSupportedInputFormats(
			String className, int type) {
		final PlugInInfo pi = find(className, type);
		if (pi == null) {
			return null;
		}
		return pi.inputFormats;
	}

	public static synchronized Format[] getSupportedOutputFormats(
			String className, int type) {
		final PlugInInfo pi = find(className, type);
		if (pi == null)
			return null;
		return pi.outputFormats;

	}

	private static boolean isValid(int type) {
		return type >= 1 && type <= 5;
	}

	private static Vector<String> getVector(int type) {
		if (!isValid(type)) {
			return null;
		}
		return (Vector<String>) classLists[type - 1];
	}

	private static synchronized PlugInInfo find(String classname, int type) {
		PlugInInfo info = (PlugInInfo) pluginMaps[type - 1].get(classname);

		return info;
	}

	private static final PlugInInfo getPluginInfo(String pluginName) {
		Object pluginObject;

		try {
			Class cls = Class.forName(pluginName);
			pluginObject = cls.newInstance();
		} catch (ClassNotFoundException e) {
			logger.warning("Problem loading plugin " + pluginName + ": "
					+ e.getMessage());
			return null;
		} catch (InstantiationException e) {
			logger.warning("Problem loading plugin " + pluginName + ": "
					+ e.getMessage());
			return null;
		} catch (IllegalAccessException e) {
			logger.warning("Problem loading plugin " + pluginName + ": "
					+ e.getMessage());
			return null;
		}

		Format[] in = null;
		Format[] out = null;

		if (pluginObject instanceof Demultiplexer) {
			Demultiplexer demux = (Demultiplexer) pluginObject;
			in = demux.getSupportedInputContentDescriptors();
		} else if (pluginObject instanceof Codec) {
			Codec codec = (Codec) pluginObject;
			in = codec.getSupportedInputFormats();
			out = codec.getSupportedOutputFormats(null);
		} else if (pluginObject instanceof Multiplexer) {
			Multiplexer mux = (Multiplexer) pluginObject;
			in = mux.getSupportedInputFormats();
			out = mux.getSupportedOutputContentDescriptors(null);
		} else if (pluginObject instanceof Renderer) {
			Renderer renderer = (Renderer) pluginObject;
			in = renderer.getSupportedInputFormats();
			out = null;
		} else if (pluginObject instanceof Effect) {
			Effect effect = (Effect) pluginObject;
			in = effect.getSupportedInputFormats();
			out = effect.getSupportedOutputFormats(null);
		}

		return new PlugInInfo(pluginName, in, out);
	}

}
