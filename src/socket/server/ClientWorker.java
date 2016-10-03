package socket.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import socket.SocketAPI;
import socket.SocketMessage;
import socket.sockets.SocketServer;

public class ClientWorker implements Runnable {
	
	SocketServer server;
	public Socket client;
	public String id;
	
	BufferedReader in = null;
	PrintWriter out = null;
	
	public ClientWorker(SocketServer server, Socket client, String id) {
		this.client = client;
		this.server = server;
		this.id = id;
		
		for(ClientWorker all : server.clientWorkers) {
			if(all.id.equals(id)) {
				try {
					all.client.close();
					server.clientWorkers.remove(all);
					server.clientIds.remove(all.id);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			out = new PrintWriter(client.getOutputStream(), true);
            
		} catch (IOException e) {
			System.out.println("[Socket] Could not start client connection.");
		}
	}

	public void run() {
		String line;

		while (client.isConnected() && !client.isClosed()) {
			try {
				line = in.readLine();
				
				if(line != null && line.split(";/").length > 3) {
					processMessage(new SocketMessage(line));
				}
				
			} catch (IOException e) {
				server.disconnectClient(id);
			}
		}
	}
	
	public void sendMessage(String message) {
		if(client.isConnected() && !client.isClosed()) {
			out.println(message);
		}
	}
	
	public void processMessage(SocketMessage message) {
		if(message != null && message.getType() != null && message.getTarget() != null && message.getInfo() != null) {
			if(message.getType().equalsIgnoreCase("join")) {
				String newId = message.getTarget();
				
				server.clientIds.add(newId);
				id = newId;
				
				System.out.println("[Socket] Client '" + id + "' (" + message.getInfo() + ") connected. (" + (server.clientWorkers.size() - 1) + " clients connected)");
				SocketAPI.getInstance().onConnect(id);
			}
			
			if(message.getType().equalsIgnoreCase("quit")) {
				server.disconnectClient(id);
				
			} else if(message.getType().equalsIgnoreCase("message")) {
				System.out.println(message.getInfo());
				
			} else {
				SocketAPI.getInstance().processSocketMessage(message);
			}
			
		} else {
			sendMessage(new SocketMessage("disconnect", id, "Invalid layout. You got disconnected.").toString());
			server.disconnectClient(id);
			return;
		}
	}
}
