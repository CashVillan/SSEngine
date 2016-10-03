package socket.sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import socket.SocketMessage;
import socket.server.ClientWorker;
import socket.threads.ServerThread;

public class SocketServer {
		
	public ServerSocket server;
	ServerThread st = null;

	public List<Object> clientIds = new ArrayList<Object>();
	public List<ClientWorker> clientWorkers = new ArrayList<ClientWorker>();
	
	public SocketServer(String host, int port) {
		try {
			listenSocket(port);

			st = new ServerThread(this);
			Thread t = new Thread(st);
			t.start();
			
			Thread t2 = new Thread(new Runnable() {
				public void run() {
					while(true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						broadcastMessage(new SocketMessage("heartbeat", " ", " ").toString());
					}
				}
			});
			t2.start();
			
		} catch (Exception e) {
		}
	}
	
	public void listenSocket(int port) {
		try {
			server = new ServerSocket(port);
			server.setSoTimeout(0);
			
		} catch (IOException e) {
			System.out.println("[Socket] Could not listen on port " + port + ".");
		}
	}
	
	public void broadcastMessage(String message) {
		for(ClientWorker all : clientWorkers) {
			if(message != null && all != null) {
				all.sendMessage(message);
			}
		}
	}
	
	//Disconnect
	
	public void disconnectClient(String id, String ip) {
		if(clientIds.contains(id)) {
			ClientWorker worker = null;
			
			for(ClientWorker workers : clientWorkers) {
				if(workers.id.equals(id)) {
					worker = workers;
				}
			}
			
			if(worker != null) {
				if(worker.client.isConnected() && !worker.client.isClosed()) {
					try {
						worker.client.close();
					} catch (IOException e1) { }
				}
				
				clientWorkers.remove(worker);
				clientIds.remove(id);
				
				if(!id.startsWith("prelogin")) {
					System.out.println("[Socket] Client '" + id + "' (" + ip + ") disconnected. (" + clientWorkers.size() + " clients connected)");
				}
			}
		}
	}
	
	public void disconnectClient(String id) {
		if(clientIds.contains(id)) {
			ClientWorker worker = null;
			
			for(ClientWorker workers : clientWorkers) {
				if(workers.id.equals(id)) {
					worker = workers;
				}
			}
			
			if(worker != null) {
				if(worker.client.isConnected() && !worker.client.isClosed()) {
					try {
						worker.client.close();
					} catch (IOException e1) { }
				}
				
				clientWorkers.remove(worker);
				clientIds.remove(id);
			
				if(!id.startsWith("prelogin")) {
					System.out.println("[Socket] Client '" + id + "' disconnected. (" + clientWorkers.size() + " clients connected)");
				}
			}
		}
	}
}
