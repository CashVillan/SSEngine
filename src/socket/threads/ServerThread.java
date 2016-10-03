package socket.threads;

import java.io.IOException;
import java.util.Random;

import socket.server.ClientWorker;
import socket.sockets.SocketServer;

public class ServerThread extends Thread {
	
	SocketServer ss = null;
	
	public ServerThread(SocketServer ss) {
		this.ss = ss;
	}

	@Override
	public void run() {
		System.out.println("[Socket] Listening for clients on port " + ss.server.getLocalPort() + "...");
		
		while(true) {
			ClientWorker w;
			
			try {
				String id = "prelogin" + new Random().nextInt(100000);
				
				w = new ClientWorker(ss, ss.server.accept(), id);
				//ss.clients.put(id, w);
				
				ss.clientIds.add(id);
				ss.clientWorkers.add(w);
				
				Thread t = new Thread(w);
				t.start();
				
		    } catch (IOException e) { }
		}
	}
}
