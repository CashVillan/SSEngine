package com.swingsword.ssengine.server;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;

public class PingedServer {
	
	private String host;
	private int port;
	
	private String version = null;
	private String motd = null;
	private int online = 0;
	private int max = 0;
	
	private boolean hasResponded = false;
	
	public PingedServer(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public PingedServer(String server) {
		this.host = server.split(":")[0];
		this.port = Integer.parseInt(server.split(":")[1]);
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getMOTD() {
		return motd;
	}
	
	public int getOnline() {
		return online;
	}
	
	public int getMax() {
		return max;
	}
	
	public boolean hasResponded() {
		return hasResponded;
	}
	
	public PingedServer ping(final String target) {
		try {
			Socket socket = new Socket();
			OutputStream os;
			DataOutputStream dos;
			InputStream is;
			InputStreamReader isr;
			
			socket.setSoTimeout(1000);
			socket.connect(new InetSocketAddress(host, port), 1000);
			
			os = socket.getOutputStream();
			dos = new DataOutputStream(os);
			
			is = socket.getInputStream();
			isr = new InputStreamReader(is, Charset.forName("UTF-16BE"));
			
			dos.write(new byte[] { (byte) 0xFE, (byte) 0x01 });

			int pakcetId = is.read();

			if (pakcetId == 0xFF) {
				int length = isr.read();

				if (length != -1 && length != 0) {
					char[] chars = new char[length];

					if (isr.read(chars, 0, length) == length) {
						String string = new String(chars);
						String[] data = string.split("\0");

						version = data[2];
						motd = data[3];
						online = Integer.parseInt(data[4]);
						max = Integer.parseInt(data[5]);

						hasResponded = true;

						if (target != null && !motd.split(";")[0].equals("Null")) {
							Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									for (Player all : Bukkit.getOnlinePlayers()) {
										if (all.getOpenInventory().getTopInventory() != null) {
											if (all.getOpenInventory().getTopInventory().getTitle().toLowerCase().contains(target.toLowerCase()) && all.getOpenInventory().getTopInventory().getTitle().toLowerCase().contains("join ")) {
												all.openInventory(ServerManager.getJoinInventory(all, all.getOpenInventory().getTopInventory().getTitle().split(" ")[1]));
											}
										}
									}
								}
							});
						}
					}
				}
			}

			socket.close();
			os.close();
			dos.close();
			is.close();
			isr.close();

		} catch (Exception e) {
		}

		return this;
	}
}
