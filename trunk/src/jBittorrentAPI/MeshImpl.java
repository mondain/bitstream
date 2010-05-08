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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Timer;

public class MeshImpl implements PeerUpdateListener {

	private final static int port=2500;
	private final static int updateTime=300000;
	private LinkedHashMap<Peer,DLRate> peerRate=null;
	private LinkedHashMap<String,Peer> peers;
	private LinkedHashMap<Peer, DLRate> otherPeersList;

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

	public MeshImpl() {
		updatePeers = new Timer(60000, new ActionListener() {

			boolean updated = false;
			@Override
			public void actionPerformed(ActionEvent ae) {
				otherPeersList = new LinkedHashMap<Peer, DLRate>();
				List<Peer> l=new LinkedList<Peer>(peers.values());
				for(Iterator<Peer> it = l.iterator(); it.hasNext();) {
					final Peer p = it.next();
					if(peerRate.get(p).getInterval() >= updateTime) {
						updated = true;
						try {
							Socket s = new Socket(p.getIP(), port);
							InputStream is = s.getInputStream();
							ObjectInputStream ois = new ObjectInputStream(is);
							LinkedHashMap<Peer, DLRate> m = (LinkedHashMap<Peer, DLRate>) ois.readObject();
							otherPeersList.putAll(m);
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

	/*
	 * at this point you have recieved the peer lists for all peers and you
	 * have to figure out if we need any new peers or not
	 */
	public void updateOwnPeers() {

	}

	@Override
	public void updateFailed(int error, String message) {	
		System.err.println(message);
		System.err.flush();
	}

	@Override
	public void updatePeerList(LinkedHashMap list) {

		List<Peer> l=new LinkedList<Peer>(list.values());
		peers=list;//a copy of the peers is also made that served another purpose in the below funtion
		for (Iterator it = l.iterator(); it.hasNext();) {
			Peer p = (Peer) it.next();
			System.out.println("Size of peer List: "+list.size());
			//if(p.getDLRate(false)>0.0){

			System.out.print("Peer:"+p.toString()+" ");
			System.out.println("Rate: "+p.getDLRate(true)/ (1024 * 10));
			//	}
			peerRate.put(p,new DLRate(p.getDLRate(false)));
		}
		System.out.println("Peer List updated from tracker with " + list.size()
				+ " peers");


	}
}

class DLRate {
	double speed;
	long lastUpdate;
	public DLRate(double s) {
		speed = s;
		this.lastUpdate = System.currentTimeMillis();
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
		this.lastUpdate = System.currentTimeMillis();
	}

	public void setLastUpdate() {
		this.lastUpdate = System.currentTimeMillis();
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}

	public long getInterval() {
		return System.currentTimeMillis() - this.lastUpdate;
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
