package socket;

import java.util.ArrayList;
import java.util.List;

import socket.sockets.SocketClient;
import socket.sockets.SocketServer;

public abstract class SocketAPI {

	public static SocketAPI sa = null;
	
	public ArrayList<SocketClient> clients = new ArrayList<SocketClient>();
	public ArrayList<SocketServer> servers = new ArrayList<SocketServer>();
	
	public SocketAPI() {
		sa = this;
	}
	
	public static SocketAPI getInstance() {
		return sa;
	}
	
	public abstract void processSocketMessage(SocketMessage message);
	public abstract void onConnect(String id);
	
	//Methods
	
	public SocketServer createSocketServer(String host, int port) {
		SocketServer server = new SocketServer(host, port);
		servers.add(server);
		
		return server;
	}
	
	public SocketClient createSocketClient(String host, int port, String id) {
		SocketClient client = new SocketClient(host, port, id);
		clients.add(client);
		
		return client;
	}
	
	public void removeClient(String id) {
		List<SocketClient> remove = new ArrayList<SocketClient>();
		for(SocketClient all : clients) {
			if(all.id.equals(id)) {
				remove.add(all);
			}
		}
		for(SocketClient all : remove) {
			all.sendMessage(new SocketMessage("quit", all.id, " ").toString());
			
			all.disconnect();
			clients.remove(all);
		}
	}
}
