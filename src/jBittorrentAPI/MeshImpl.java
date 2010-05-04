package jBittorrentAPI;

import java.io.IOException;
import java.io.InputStream;
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

public class MeshImpl implements PeerUpdateListener {

	private final static int port=2500;
	private LinkedHashMap<Peer,Float> peerRate=null;
	private LinkedHashMap<String,Peer> peers;
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
				peerRate.put(p,p.getDLRate(false));
		}
		System.out.println("Peer List updated from tracker with " + list.size()
				+ " peers");
		

	}
	/*The following function creates a server socket and sends the list 
	 * individual peers that are extracted from the peerRate
	 */
	public void listSender(){
		Socket send_list;
		for(int i=0;i<peerRate.size();i++){
			List<Peer> l=new LinkedList<Peer>(peers.values());
			for (Iterator it = l.iterator(); it.hasNext();) {
				Peer p = (Peer) it.next();
				System.out.println("Size of peer List: "+peers.size());
				try {
					send_list=new Socket(p.getIP(),port);
					OutputStream os=send_list.getOutputStream();
					ObjectOutputStream oos=new ObjectOutputStream(os);
					oos.writeObject(peerRate);//this send the list to the peers....
					//this list contains the peers and their corresponding download rate
				} catch (Exception e) {
					e.printStackTrace();
				}	
			}
		}
		
	}
	/*The following function receives the list that is sent by the above function
	 * from another peer and then matched the list with its own list and apply
	 * the mentioned peer exchange algo.
	 */
	public void listReceiver(){
		try {
			ServerSocket recieve_list=new ServerSocket(port);
			while(true){
				Socket s=recieve_list.accept();
				InputStream is=s.getInputStream();
				ObjectInputStream ois=new ObjectInputStream(is);
				LinkedHashMap<Peer,Float> rec_list=(LinkedHashMap<Peer,Float>)ois.readObject();
				//at this point you are recieving the list and then do some mumbo jumbo on it....
				//calling the peer exchanging function
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void peerExchange(LinkedHashMap list){
		
	}
}
