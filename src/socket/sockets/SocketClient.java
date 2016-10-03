package socket.sockets;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;

import socket.SocketAPI;
import socket.SocketMessage;
import socket.threads.ClientThread;

public class SocketClient {
	
	public boolean reconnecting = false;
	
	public Socket socket;
	public String id = "prelogin";
	
	PrintWriter out = null;
	public ClientThread ct;
	
	public SocketClient(String host, int port, final String id) {
		this.id = id;
		
		listenSocket(host, port);
	}

	public void listenSocket(final String host, final int port) {
		try {
			System.out.println("[Socket] Connecting to " + host + ":" + port + "...");
			
			socket = new Socket(host, port);
			socket.setKeepAlive(true);
			socket.setTcpNoDelay(true);
			
			out = new PrintWriter(socket.getOutputStream(), true);
			
			out.flush();
			
			ct = new ClientThread(this);
			Thread t = new Thread(ct);
			t.start();
			
			System.out.println("[Socket] Connected to " + host + ":" + port);
			
			Thread t2 = new Thread(new Runnable() {
				public void run() {
					while(true) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						if(new Date().getTime() - ct.lastBeat.getTime() > 2000) {
							reconnect(host, port);
						}
					}
				}
			});
			t2.start();
			
		} catch (UnknownHostException e) {
			System.out.println("[Socket] Unknown host: " + host);
			
		} catch (IOException e) {
			System.out.println("[Socket] Could not connect to server.");
		}
		
		
		if(out != null) {
			try {
				sendMessage(new SocketMessage("join", id, Inet4Address.getLocalHost().getHostAddress()).toString());
			} catch (UnknownHostException e) {
				sendMessage(new SocketMessage("join", id, " ").toString());
			}
			SocketAPI.getInstance().onConnect(id);
			
		} else {
			reconnect(host, port);
		}
	}
	
	public void reconnect(final String host, final int port) {
		if(reconnecting == false) {
			reconnecting = true;
			
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(1000);
						
						reconnecting = false;
						listenSocket(host, port);
						
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			t.start();
		}
	}
	
	public void sendMessage(String message) {
		if(out != null) {
			out.println(message);
		}
	}
	
	public void disconnect() {
		try {
			sendMessage(new SocketMessage("quit", id, Inet4Address.getLocalHost().getHostAddress()).toString());
		} catch (UnknownHostException e1) {
			sendMessage(new SocketMessage("quit", id, " ").toString());
		}
		
		try {
			socket.close();
			socket = null;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
