package com.swingsword.ssengine.socket;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.server.ServerManager;
import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.StringUtils;

import socket.SocketAPI;
import socket.SocketMessage;
import socket.sockets.SocketClient;

public class ClientInstance extends SocketAPI {
	
	public static SocketClient sc = null;
	public boolean updating;
	public boolean plannedReboot;
	
	public ClientInstance() {
		sc = SocketAPI.getInstance().createSocketClient("127.0.0.1", 25560, MasterPlugin.getServerName());
		updating = false;
		plannedReboot = false;
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    @Override
		    public void run() {
		    	for(SocketClient all : SocketAPI.getInstance().clients) {
		    		SocketAPI.getInstance().removeClient(all.id);
		    	}
		    }
		});
	}
	
	@Override
	public void processSocketMessage(final SocketMessage message) {
		if(message.getType().equalsIgnoreCase("startGamemodeMap")) {
			if(message.getTarget().equals(sc.id)) {
				final String gamemode = message.getInfo().split(";")[0];
				final String map = message.getInfo().split(";")[1];
				if (Bukkit.getPluginManager().getPlugin("SSMinigame") != null) {
					if (!GameManager.delay && GameManager.currentGame == null) {
						Bukkit.getScheduler().runTaskLater(MasterPlugin.getMasterPlugin(), new Runnable() {
							public void run() {
								System.out.println("Starting gamemode: " + gamemode + " on " + map);

								GameManager.startGame(gamemode, !map.equals("null") ? map : null);
							}
						}, 20);
					}
				}
			}
		}
		
		if(message.getType().equalsIgnoreCase("serverMotd")) {
			ServerManager.serverMotd.put(message.getTarget(), message.getInfo());
			
			Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					for(Player all : Bukkit.getOnlinePlayers()) {
						if(all.getOpenInventory().getTopInventory() != null) {
							if(all.getOpenInventory().getTopInventory().getTitle().toLowerCase().contains(message.getTarget().toLowerCase()) && all.getOpenInventory().getTopInventory().getTitle().toLowerCase().contains("join ")) {
								all.openInventory(ServerManager.getJoinInventory(all, all.getOpenInventory().getTopInventory().getTitle().split(" ")[1]));
							}
						}
					}
				}
			});
		}
		
		if (message.getType().equalsIgnoreCase("update")) {
			updating = true;
			FileConfiguration config = ConfigUtils.getConfig("config");
			List<String> update = config.getStringList("update");
			if (!update.contains(message.getInfo())) {
				update.add(message.getInfo());
				config.set("update", update);
				ConfigUtils.saveConfig(config, "config");
			}
			
			updating = false;
			
			if (plannedReboot) {
				Bukkit.shutdown();
			}
		}
		
		if (message.getType().equalsIgnoreCase("stop")) {
			if (updating) {
				plannedReboot = true;
			} else {
				if (message.getTarget().equals(sc.id) || message.getTarget().equals("ALL")) {
					Bukkit.shutdown();
				}
			}
		}
	}
	
	@Override
	public void onConnect(final String id) {		
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				System.out.println("[Socket] Connected to server as: " + id);
				
				if(Bukkit.getPluginManager().getPlugin("SSMinigame") != null) {
					sc.sendMessage(new SocketMessage("sendGamemodeMaps", "server", StringUtils.listToString(com.swingsword.ssengine.utils.BungeeUtils.getGamemodeMaps())).toString());
					sc.sendMessage(new SocketMessage("sendGamemodeServers", "server", StringUtils.listToString(com.swingsword.ssengine.utils.BungeeUtils.getGamemodeServerCounts())).toString());
				}
			}
		}, 1);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {		
				sc.sendMessage(new SocketMessage("sendMotd", MasterPlugin.getServerName(), ServerManager.currentMotd).toString());
			}
		}, 10, 10);
	}
}
