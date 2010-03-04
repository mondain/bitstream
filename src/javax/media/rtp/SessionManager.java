package javax.media.rtp;

import javax.media.Controls;
import javax.media.Format;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.DataSource;
import javax.media.rtp.rtcp.SourceDescription;

/**
 * Complete.
 * 
 * @author Ken Larson
 * @deprecated
 * 
 */
public interface SessionManager extends Controls {
	public static final long SSRC_UNSPEC = 0L;

	public int initSession(SessionAddress localAddress, long defaultSSRC,
			SourceDescription[] defaultUserDesc, double rtcp_bw_fraction,
			double rtcp_sender_bw_fraction)
			throws InvalidSessionAddressException;

	public int initSession(SessionAddress localAddress,
			SourceDescription[] defaultUserDesc, double rtcp_bw_fraction,
			double rtcp_sender_bw_fraction)
			throws InvalidSessionAddressException;

	public int startSession(SessionAddress destAddress, int mcastScope,
			EncryptionInfo encryptionInfo) throws java.io.IOException,
			InvalidSessionAddressException;

	public int startSession(SessionAddress localReceiverAddress,
			SessionAddress localSenderAddress,
			SessionAddress remoteReceiverAddress, EncryptionInfo encryptionInfo)
			throws java.io.IOException, InvalidSessionAddressException;

	public void addSessionListener(SessionListener listener);

	public void addRemoteListener(RemoteListener listener);

	public void addReceiveStreamListener(ReceiveStreamListener listener);

	public void addSendStreamListener(SendStreamListener listener);

	public void removeSessionListener(SessionListener listener);

	public void removeRemoteListener(RemoteListener listener);

	public void removeReceiveStreamListener(ReceiveStreamListener listener);

	public void removeSendStreamListener(SendStreamListener listener);

	public long getDefaultSSRC();

	public java.util.Vector getRemoteParticipants();

	public java.util.Vector getActiveParticipants();

	public java.util.Vector getPassiveParticipants();

	public LocalParticipant getLocalParticipant();

	public java.util.Vector getAllParticipants();

	public java.util.Vector getReceiveStreams();

	public java.util.Vector getSendStreams();

	public RTPStream getStream(long filterssrc);

	public int getMulticastScope();

	public void setMulticastScope(int multicastScope);

	public void closeSession(String reason);

	public String generateCNAME();

	public long generateSSRC();

	public SessionAddress getSessionAddress();

	public SessionAddress getLocalSessionAddress();

	public GlobalReceptionStats getGlobalReceptionStats();

	public GlobalTransmissionStats getGlobalTransmissionStats();

	public SendStream createSendStream(int ssrc, DataSource ds, int streamindex)
			throws UnsupportedFormatException, SSRCInUseException,
			java.io.IOException;

	public SendStream createSendStream(DataSource ds, int streamindex)
			throws UnsupportedFormatException, java.io.IOException;

	public void addFormat(Format fmt, int payload);

	public int startSession(int mcastScope, EncryptionInfo encryptionInfo)
			throws java.io.IOException;

	public void addPeer(SessionAddress peerAddress) throws java.io.IOException,
			InvalidSessionAddressException;

	public void removePeer(SessionAddress peerAddress);

	public void removeAllPeers();

	public java.util.Vector getPeers();

}