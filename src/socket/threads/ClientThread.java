package socket.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

import socket.SocketAPI;
import socket.SocketMessage;
import socket.sockets.SocketClient;

public class ClientThread extends Thread {

	SocketClient sc = null;
	BufferedReader in;

	public Date lastBeat = null;
	
	public ClientThread(SocketClient sc) {
		this.sc = sc;
	}

	@Override
	public void run() {
		try {
			in = new BufferedReader(new InputStreamReader(sc.socket.getInputStream()));
			
		} catch (IOException e) {
			System.out.println("[Socket] Could not start a client socket.");
			return;
		}
		
		while (true) {
			if (in != null) {
				try {
					String line = in.readLine();

					if (line != null) {
						SocketMessage message = new SocketMessage(line);

						if (message != null && message.getType() != null) {
							if (message.getType().equalsIgnoreCase("disconnect")) {
								System.out.println(message.getInfo());
								sc.disconnect();
								in = null;
								
							} else if (message.getType().equalsIgnoreCase("heartbeat")) {
								lastBeat = new Date();
							
							} else if (message.getType().equalsIgnoreCase("message")) {
								System.out.println(message.getInfo());

							} else {
								SocketAPI.getInstance().processSocketMessage(message);
							}
						}
					}

				} catch (IOException e) {
				}
			}
		}
	}
}
