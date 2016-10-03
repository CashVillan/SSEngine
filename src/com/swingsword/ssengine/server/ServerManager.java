package com.swingsword.ssengine.server;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.utils.InventoryUtils;

public class ServerManager {
	
	public static HashMap<String, String> serverMotd = new HashMap<String, String>();
	
	public static String gamemode = null;
	public static String map = null;
	
	public static String currentMotd = null;
	
	public ServerManager() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				updateMOTD();
			}
		});
		t.start();
	}
	
	public static void updateMOTD() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				//DedicatedServer s = (((CraftServer)Bukkit.getServer()).getHandle().getServer());
				int mode = 1;
				if(Bukkit.getOnlinePlayers().size() < Bukkit.getMaxPlayers()) {
					mode = 0;
				}
				
				try { 
					String mapName = "";
					if(mapName != null) {
						mapName = ";" + map;
					}
					
					currentMotd = MasterPlugin.getServerName() + ";" + gamemode + ";" + Bukkit.getOnlinePlayers().size() + ";" + Bukkit.getMaxPlayers() + ";" + mode + mapName;
					//s.setMotd(currentMotd);
					
				} catch (Exception e) { }
			}
		}, 10, 10);
	}
	
	public static Inventory getJoinInventory(Player player, String key) {
		Inventory inv = Bukkit.createInventory(null, 45, "Join " + key);
		
		for(String server : serverMotd.keySet()) {			
			if(!server.contains("null") && (server.toLowerCase().contains(key.toLowerCase()) || serverMotd.get(server).toLowerCase().contains(key.toLowerCase()))) {
				ItemStack item = Server.toItemStack(player, server, serverMotd.get(server));
				
				if(item != null) {
					if(server.equals(MasterPlugin.getServerName())) {
						item.setType(Material.GOLD_BLOCK);
					}
					
					inv.setItem(InventoryUtils.getFirstFreeFrom(inv, 20), item);
				}
			}
		}
		
		return inv;
	}
	
	/*public static Inventory getHubInventory() {
		Inventory inv = Bukkit.createInventory(null, 45, "Hub Selector");
		
		for(Server all : servers) {
			if(all.name.toLowerCase().contains("hub-")) {
				int hubId = Integer.parseInt(all.name.split("-")[1]);
				ItemStack hubItem = all.toItemStack();
				ItemMeta hubMeta = hubItem.getItemMeta();
				hubMeta.setDisplayName(hubMeta.getDisplayName().replace("-", " "));
				hubItem.setItemMeta(hubMeta);
				hubItem.setAmount(hubId);
				
				if(Channel.serverName.equals(all.name)) {
					hubItem.setType(Material.GOLD_BLOCK);
				}
				
				inv.setItem(hubId - 1, hubItem);
			}
		}
		
		return inv;
	}*/
	
	public static int getOnlineCount(String key) {
		int total = 0;
		
		for(String server : serverMotd.keySet()) {
			if(server.toLowerCase().contains(key.toLowerCase()) || serverMotd.get(server).toLowerCase().contains(key.toLowerCase())) {
				if(serverMotd.get(server).split(";").length > 1) {
					total += Integer.parseInt(serverMotd.get(server).split(";")[2]);
				}
			}
		}
		
		return total;
	}
}
