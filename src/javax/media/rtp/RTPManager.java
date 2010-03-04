package javax.media.rtp;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.media.Controls;
import javax.media.Format;
import javax.media.PackageManager;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.DataSource;
import javax.media.rtp.rtcp.SourceDescription;

import net.sf.fmj.utility.LoggerSingleton;

/**
 * Coding complete.
 * 
 * @author Ken Larson
 * 
 */
public abstract class RTPManager implements Controls {
	// Sun's does a reflection trick, much like PlugInManager, etc. We don't do
	// that here.

	private static final Logger logger = LoggerSingleton.logger;

	public RTPManager() {
		super();
	}

	public abstract void addFormat(Format format, int payload);

	public abstract void addReceiveStreamListener(ReceiveStreamListener listener);

	public abstract void addRemoteListener(RemoteListener listener);

	public abstract void addSendStreamListener(SendStreamListener listener);

	public abstract void addSessionListener(SessionListener listener);

	public abstract void removeTarget(SessionAddress remoteAddress,
			String reason) throws InvalidSessionAddressException;

	public abstract void removeTargets(String reason);

	public abstract SendStream createSendStream(DataSource dataSource,
			int streamIndex) throws UnsupportedFormatException,
			java.io.IOException;

	public abstract void dispose();

	public abstract java.util.Vector getActiveParticipants();

	public abstract java.util.Vector getAllParticipants();

	public abstract GlobalReceptionStats getGlobalReceptionStats();

	public abstract GlobalTransmissionStats getGlobalTransmissionStats();

	public abstract LocalParticipant getLocalParticipant();

	public abstract java.util.Vector getPassiveParticipants();

	public abstract java.util.Vector getReceiveStreams();

	public abstract java.util.Vector getRemoteParticipants();

	public abstract java.util.Vector getSendStreams();

	public abstract void initialize(SessionAddress localAddress)
			throws InvalidSessionAddressException, java.io.IOException;

	public abstract void initialize(SessionAddress[] localAddresses,
			SourceDescription[] sourceDescription,
			double rtcpBandwidthFraction, double rtcpSenderBandwidthFraction,
			EncryptionInfo encryptionInfo)
			throws InvalidSessionAddressException, java.io.IOException;

	public abstract void initialize(RTPConnector connector);

	public abstract void addTarget(SessionAddress remoteAddress)
			throws InvalidSessionAddressException, java.io.IOException;

	public abstract void removeReceiveStreamListener(
			ReceiveStreamListener listener);

	public abstract void removeRemoteListener(RemoteListener listener);

	public abstract void removeSendStreamListener(SendStreamListener listener);

	public abstract void removeSessionListener(SessionListener listener);

	public static RTPManager newInstance() {
		final Vector v = getRTPManagerList();
		for (int i = 0; i < v.size(); ++i) {
			final String className = (String) v.get(i);

			try {
				logger.finer("Trying RTPManager class: " + className);
				final Class clazz = Class.forName(className);
				return (RTPManager) clazz.newInstance();
			} catch (ClassNotFoundException e) {
				logger.finer("RTPManager.newInstance: ClassNotFoundException: "
						+ className);
				continue;
			} catch (Exception e) {
				logger.log(Level.WARNING, "" + e, e);
				continue;
			}
		}
		return null;
	}

	// returns a vector of string
	public static java.util.Vector getRTPManagerList() {
		final Vector result = new Vector();
		result.add("media.rtp.RTPSessionMgr");
		final Vector prefixList = PackageManager.getProtocolPrefixList();
		for (int i = 0; i < prefixList.size(); ++i) {
			final String prefix = (String) prefixList.get(i);
			result.add(prefix + ".media.rtp.RTPSessionMgr");

		}

		return result;
	}
}
