 package com.swingsword.ssengine.bungee;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.party.Party;
import com.swingsword.ssengine.party.PartyManager;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.server.ServerManager;
import com.swingsword.ssengine.utils.Notification;
import com.swingsword.ssengine.utils.StringUtils;

public class Channel implements PluginMessageListener {

	public static String serverName = MasterPlugin.getServerName();
	
	public static HashMap<String, String> playerServer = new HashMap<String, String>();
	public static HashMap<String, Party> playerParty = new HashMap<String, Party>();
	public static HashMap<String, String> playerSessionRequests = new HashMap<String, String>();
	
	@SuppressWarnings("deprecation")
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {	
		if (!channel.equals("BungeeCord")) {
			return;
		}
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		
		if (subchannel.equals("SS")) {
			short len = in.readShort();
			byte[] msgbytes = new byte[len];
			in.readFully(msgbytes);
	
			DataInputStream msgin = new DataInputStream(new ByteArrayInputStream(msgbytes));
			try {
				String data = msgin.readUTF();
				String subsubchannel = data.split(" ")[0];
								
				if (subsubchannel.equals("getPlayerServer")) {
					String target = data.split(" ")[1];
					String fromServer = data.split(" ")[2];
					
					sendServer(target, fromServer);
					
				} else if (subsubchannel.equals("sendPlayerServer")) {
					String target = data.split(" ")[1];
					String fromServer = data.split(" ")[2];

					playerServer.put(target, fromServer);
					
				} else if (subsubchannel.equals("makeJoin")) {
					String target = data.split(" ")[1];
					String server = data.split(" ")[2];
					
					sendToServer(Bukkit.getPlayer(target), server);
					
				} else if (subsubchannel.equals("sendParty")) {
					Party party = new Party(data.split(" ")[1], false);
					
					PartyManager.addParty(party);
				
				} else if (subsubchannel.equals("disbandParty")) {
					Party party = new Party(data.split(" ")[1], false);
					
					PartyManager.removeParty(party.host);
					
				} else if (subsubchannel.equals("sendRequest")) {
					String target = data.split(" ")[1];
					String from = data.split(" ")[2];
					boolean invite = Boolean.parseBoolean(data.split(" ")[3]);
					String date = data.split(" ")[4];
					String server = data.split(" ")[5];
					
					if(Bukkit.getOfflinePlayer(target).isOnline()) {						
						if(!playerSessionRequests.containsKey(target)) {
							playerSessionRequests.put(target, from + "/;" + date + "~" + invite);
						} else {
							if(!playerSessionRequests.get(target).contains(from + "/;")) {
								playerSessionRequests.put(target, playerSessionRequests.get(target) + ";/" + from + "/;" + date + "~" + invite);
							} else {
								return;
							}
						}
						
						if(invite) {
							Bukkit.getPlayer(target).sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + from + " requested you to play on " + server + " with them!");
						} else {
							Bukkit.getPlayer(target).sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + from + " requested to join your server!");
						}
					}
					
				} else if (subsubchannel.equals("forwardRequests")) {
					String target = data.split(" ")[1];
					String requests = data.split(" ")[2];
					
					playerSessionRequests.put(target, requests);
				
				} else if (subsubchannel.equals("message")) {
					String target = data.split(" ")[1];
					String sendPlayer = data.split(" ")[2];
					String playerMessage = "";
					for(int x = 3; x < data.split(" ").length; x++) {
						playerMessage += data.split(" ")[x] + " ";
					}

					if(Bukkit.getOfflinePlayer(target).isOnline()) {
						StringUtils.sendClickMessage(Bukkit.getPlayer(target), playerMessage, ChatColor.GRAY + "Click to view " + sendPlayer + "'s profile", "/search " + sendPlayer);
					}
					
				} else if (subsubchannel.equals("updateAccount")) {
					String target = data.split(" ")[1];
					
					if(Bukkit.getOfflinePlayer(target).isOnline()) {
						Player targetPlayer = Bukkit.getPlayer(target);
						
						PlayerSessionManager.endSession(targetPlayer, false, false);
						PlayerSessionManager.removeSession(targetPlayer.getName());
						PlayerSessionManager.startSession(targetPlayer);
					}
				}
				
			} catch (IOException e) { }
			
		} else if(subchannel.toLowerCase().contains("server")) {
			String servername = in.readUTF();
			
			serverName = servername;
		}
	}
	
	public static void sendToServer(Player player, String server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(server);

		if(PartyManager.getParty(player.getName(), true) != null) {
			for(String players : PartyManager.getParty(player.getName(), true).players) {
				sendMessage(players, ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Following " + player.getName() + " to server '" + server + "'.");
				
				makeJoinServer(players, server);
			}
		}
		
		if(playerSessionRequests.containsKey(player.getName())) {
			forwardRequests(player.getName(), server);
		}
		
		player.sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
		
	public static String getBestHub() {
		List<String> hubs = new ArrayList<String>();
		for (String server : ServerManager.serverMotd.keySet()) {
			if (ServerManager.serverMotd.get(server).contains("Hub")) {
				hubs.add(server);
			}
		}
		
		String leastPlayers = null;
		String tempLeastPlayers = null;
		
		for (String all : hubs) {
			if (tempLeastPlayers == null) {
				tempLeastPlayers = all;
			} else {
				if (Integer.parseInt(ServerManager.serverMotd.get(all).split(";")[2]) < Integer.parseInt(ServerManager.serverMotd.get(tempLeastPlayers).split(";")[2])) {
					leastPlayers = all;
				}
			}
		}
		
		String hub = null;
		if (leastPlayers == null && hubs.size() > 0) {
			hub = hubs.get(new Random().nextInt(hubs.size()));
		} else {
			hub = leastPlayers;
		}
		
		if(hub != null) {
			return hub;
		} else {
			return MasterPlugin.getServerName();
		}
	}
	
	public static void kickPlayer(String player, String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("KickPlayer");
		out.writeUTF(player);
		out.writeUTF(ChatColor.RED + message);
		
		((Player) Bukkit.getOnlinePlayers().toArray()[0]).sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	public static String getServer(String target) {
		MasterPlugin.getMasterPlugin().channel.requestPlayerServer(target);
		
		int triesLeft = 500;
		while (!Channel.playerServer.containsKey(target) && triesLeft != -1) {
			try {
				Thread.sleep(1);
				triesLeft = triesLeft - 1;
				
				if (Channel.playerServer.containsKey(target)) {
					final String server = Channel.playerServer.get(target);
					
					Channel.playerServer.remove(target);
					triesLeft = -1;
					
					return server;
					
				} else if(triesLeft == 0) {
					return null;
				}
				
			} catch (InterruptedException e) { }
		}
		
		if(playerServer.containsKey(target)) {
			String server = playerServer.get(target);
			playerServer.remove(target);
			
			return server;
		} else {
			return null;
		}
	}

	public static void makeJoinServer(String target, String server) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ForwardToPlayer");
		out.writeUTF(target);
		out.writeUTF("SS");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF("makeJoin " + target + " " + server);
		} catch (IOException e) { }

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		player.sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	public void joinPlayer(final Player player, final String target) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				Thread t = new Thread(new Runnable() {
					public void run() {
						String server = Channel.getServer(target);
						
						if(server != null) {
							sendToServer(player, server);
						} else {
							Notification.sendChatNotification(player, "Session", target + " has gone offline.");
						}
					}
				});
				t.run();
			}
		});
	}
	
	public static void sendMessage(String target, String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Message");
		out.writeUTF(target);
		out.writeUTF(message);
		
		((PluginMessageRecipient) Bukkit.getOnlinePlayers().toArray()[0]).sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	public static void sendInboxMessage(String from, String target, String message) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ForwardToPlayer");
		out.writeUTF(target);
		out.writeUTF("SS");
		
		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF("message " + target + " " + from + " " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());
		
		((PluginMessageRecipient) Bukkit.getOnlinePlayers().toArray()[0]).sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	public static void updateAccount(String target) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ForwardToPlayer");
		out.writeUTF(target);
		out.writeUTF("SS");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF("updateAccount " + target);
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		player.sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	// getServer/getName
	
	public void requestPlayerServer(String target) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("ForwardToPlayer");
		out.writeUTF(target);
		out.writeUTF("SS");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF("getPlayerServer " + target + " " + serverName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		player.sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	public void sendServer(String target, String targetServer) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF(targetServer);
		out.writeUTF("SS");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF("sendPlayerServer " + target + " " + serverName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		player.sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	public void getName() {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("GetServer");

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		player.sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	//Parties
	
	public void sendParty(Party party) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF("ALL");
		out.writeUTF("SS");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF("sendParty " + party.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		player.sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	public void disbandParty(String host) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF("ALL");
		out.writeUTF("SS");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF("disbandParty " + host);
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		player.sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	public void sendRequest(String from, String target, boolean invite) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF("ALL");
		out.writeUTF("SS");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF("sendRequest " + target + " " + from + " " + invite + " " + new SimpleDateFormat("dd/M/yyyy_hh:mm_a").format(new Date()) + " " + serverName);
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		player.sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	public static void forwardRequests(String from, String targetServer) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Forward");
		out.writeUTF(targetServer);
		out.writeUTF("SS");

		ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
		DataOutputStream msgout = new DataOutputStream(msgbytes);
		try {
			msgout.writeUTF("forwardRequests " + from + " " + playerSessionRequests.get(from));
		} catch (IOException e) {
			e.printStackTrace();
		}

		out.writeShort(msgbytes.toByteArray().length);
		out.write(msgbytes.toByteArray());

		Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
		player.sendPluginMessage(MasterPlugin.getMasterPlugin(), "BungeeCord", out.toByteArray());
	}
	
	public static void forceKickPlayer(final Player player, final String reason) {
		player.kickPlayer(reason);
			
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(player.isOnline()) {
					forceKickPlayer(player, reason);
				}
			}
		}, 1);
	}
}
