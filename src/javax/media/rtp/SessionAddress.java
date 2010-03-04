package javax.media.rtp;

import java.net.InetAddress;

/**
 * Coding complete.
 * 
 * @author Ken Larson
 * 
 */
public class SessionAddress implements java.io.Serializable {
	private InetAddress m_dataAddress;
	private InetAddress m_controlAddress;
	private int m_dataPort = ANY_PORT;
	private int m_controlPort = ANY_PORT;
	private int ttl;

	public static final int ANY_PORT = -1;

	public SessionAddress(java.net.InetAddress dataAddress, int dataPort) {
		this(dataAddress, dataPort, 0);
	}

	public SessionAddress(java.net.InetAddress dataAddress, int dataPort,
			int timeToLive) {
		this(dataAddress, dataPort, dataAddress, dataPort + 1);
		ttl = timeToLive;
	}

	public SessionAddress(java.net.InetAddress dataAddress, int dataPort,
			java.net.InetAddress controlAddress, int controlPort) {
		this.m_dataAddress = dataAddress;
		this.m_dataPort = dataPort;
		this.m_controlAddress = controlAddress;
		this.m_controlPort = controlPort;

	}

	public SessionAddress() {
		super();
	}

	public int getTimeToLive() {
		return ttl;
	}

	public java.net.InetAddress getDataAddress() {
		return m_dataAddress;
	}

	public void setDataHostAddress(java.net.InetAddress dataAddress) {
		this.m_dataAddress = dataAddress;
	}

	public void setDataPort(int dataPort) {
		this.m_dataPort = dataPort;
	}

	public String getDataHostAddress() {
		return m_dataAddress.getHostAddress();
	}

	public int getDataPort() {
		return m_dataPort;
	}

	public java.net.InetAddress getControlAddress() {
		return m_controlAddress;
	}

	public void setControlHostAddress(java.net.InetAddress controlAddress) {
		this.m_controlAddress = controlAddress;
	}

	public void setControlPort(int controlPort) {
		this.m_controlPort = controlPort;
	}

	public String getControlHostAddress() {
		return m_controlAddress.getHostAddress();
	}

	public int getControlPort() {
		return m_controlPort;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SessionAddress))
			return false;
		final SessionAddress oCast = (SessionAddress) obj;
		return this.getControlAddress().equals(oCast.getControlAddress())
				&& this.getDataAddress().equals(oCast.getDataAddress())
				&& this.getControlPort() == oCast.getControlPort()
				&& this.getDataPort() == oCast.getDataPort();
	}

	public int hashCode() {
		return getControlAddress().hashCode() + getDataAddress().hashCode()
				+ getControlPort() + getDataPort();
	}

	public String toString() {
		return "DataAddress: " + m_dataAddress + "\nControlAddress: "
				+ m_controlAddress + "\nDataPort: " + m_dataPort
				+ "\nControlPort: " + m_controlPort;
	}
}
