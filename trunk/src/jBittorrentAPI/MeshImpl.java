package jBittorrentAPI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Timer;

public class MeshImpl {

	private final static int port=2500;
	private final static int updateTime=300000;
	private LinkedHashMap<Peer,DLRate> peerRate;
	private LinkedHashMap<String,Peer> peers;
	private ArrayList<PeerEntry> otherPeersList;


	Thread listener = new Thread(new Runnable() {
		@Override
		public void run() {
			try {
				ServerSocket ss = new ServerSocket(port);
				while(true){
					Socket s=ss.accept();
					new Thread(new Send2Peer(s, peerRate)).run();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	});
	Timer updatePeers;

	public MeshImpl(LinkedHashMap<String, Peer> peer, LinkedHashMap<Peer, DLRate> peerRates) {
		this.peers = peer;
		this.peerRate = peerRates;
		updatePeers = new Timer(60000, new ActionListener() {

			boolean updated = false;
			@Override
			public void actionPerformed(ActionEvent ae) {
				otherPeersList = new ArrayList<PeerEntry>();
				List<Peer> l=new LinkedList<Peer>(peers.values());
				for(Iterator<Peer> it = l.iterator(); it.hasNext();) {
					Peer p = it.next();
					if(peerRate.get(p).getInterval() >= updateTime) {
						updated = true;
						try {
							Socket s = new Socket(p.getIP(), port);
							InputStream is = s.getInputStream();
							ObjectInputStream ois = new ObjectInputStream(is);
							LinkedHashMap<Peer, DLRate> m = (LinkedHashMap<Peer, DLRate>) ois.readObject();
							List<Peer> ml = new LinkedList<Peer>(m.keySet());
							for(Peer temp : ml) {
								otherPeersList.add(new PeerEntry(p, temp, m.get(temp)));
							}
							s.close();
						} catch (UnknownHostException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
						peerRate.get(p).setLastUpdate();
					}
				}
				if(updated) {
					updateOwnPeers();
				}
			}
		});

	}

	private Peer getLowestSpeed() {
		double speed = 1000000;
		Peer temp;
		Peer p = null;
		List<Peer> l=new LinkedList<Peer>(peers.values());
		Iterator<Peer> it = l.iterator();
		if(it.hasNext()) {
			temp = it.next();
			speed = peerRate.get(temp).getSpeed();
			p = temp;
		}
		while(it.hasNext()) {
			temp = it.next();
			if(speed > peerRate.get(temp).getSpeed()) {
				speed = peerRate.get(temp).getSpeed();
				p = temp;
			}
		}
		return p;

	}

	private PeerEntry getHighestSpeed() {
		double speed = 0;
		PeerEntry p = null;
		for(PeerEntry temp : otherPeersList) {
			if(speed < temp.getSpeed()) {
				speed = temp.getSpeed();
				p = temp;
			}
		}
		return p;
	}

	public void updateOwnPeers() {
		if(otherPeersList == null) {
			return;
		}

		while(otherPeersList.size() != 0) {
			PeerEntry in = getHighestSpeed();
			Peer out = getLowestSpeed();

			if(peerRate.get(out).getSpeed() < in.getSpeed()) {
				if(peerRate.get(out).getSpeed() < peerRate.get(in.getParent()).getSpeed()) {
					peers.remove(out);
					peers.put(in.getChild().toString(), in.getChild());
					peerRate.put(in.getChild(), in.getRate());	
				}
				else {
					otherPeersList.remove(in);
				}
			}
			else {
				otherPeersList.remove(in);
			}
		}
	}
}

class PeerEntry {
	Peer parent;
	Peer child;
	DLRate rate;

	public PeerEntry(Peer p, Peer c, DLRate r) {
		parent = p;
		child = c;
		rate = r;
	}

	public Peer getParent() {
		return parent;
	}

	public void setParent(Peer parent) {
		this.parent = parent;
	}

	public Peer getChild() {
		return child;
	}

	public void setChild(Peer child) {
		this.child = child;
	}

	public DLRate getRate() {
		return rate;
	}

	public double getSpeed() {
		return rate.getSpeed();
	}

	public void setRate(DLRate rate) {
		this.rate = rate;
	}

}

class Send2Peer implements Runnable {
	Socket sock;
	LinkedHashMap<Peer, DLRate> peerRate;
	public Send2Peer(Socket s, LinkedHashMap<Peer, DLRate> lhm) {
		sock = s;
		peerRate = lhm;
	}

	@Override
	public void run() {
		OutputStream os;
		try {
			os = sock.getOutputStream();
			ObjectOutputStream oos=new ObjectOutputStream(os);
			oos.writeObject(peerRate);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
