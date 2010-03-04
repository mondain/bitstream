package javax.media.pm;

import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.fmj.utility.Registry;
import net.sf.fmj.utility.LoggerSingleton;

/**
 * 
 * @author Ken Larson
 * 
 */
public final class PackageManager extends javax.media.PackageManager {
	private static final Logger logger = LoggerSingleton.logger;

	// This class is supposed to store its settings in jmf.properties somewhere
	// in the JMF directory.
	// however, we wish to be able to run without installing to a JRE, so we
	// could
	// support properties in a local directory.

	// TODO: should we synchronize?

	// TODO: what other kinds of classes are located using this manager?

	private static Registry registry = Registry.getInstance();

	public PackageManager() { // nothing to do
	}

	public static synchronized Vector getProtocolPrefixList() {
		return registry.getProtocolPrefixList();
	}

	public static synchronized void setProtocolPrefixList(Vector list) {
		registry.setProtocolPrefixList(list);
	}

	public static synchronized void commitProtocolPrefixList() {
		try {
			registry.commit();
		} catch (IOException e) {
			logger.log(Level.WARNING, "" + e, e);
		}
	}

	public static synchronized Vector getContentPrefixList() {
		return registry.getContentPrefixList();
	}

	public static synchronized void setContentPrefixList(Vector list) {
		registry.setContentPrefixList(list);
	}

	public static synchronized void commitContentPrefixList() {
		try {
			registry.commit();
		} catch (Exception e) {
			logger.log(Level.WARNING, "" + e, e);
		}
	}
}
