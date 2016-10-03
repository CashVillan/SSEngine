package com.swingsword.ssengine.listeners;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.swingsword.ssengine.MasterPlugin;

public class SpamListener implements Listener {

	ArrayList<String> forceLeave = new ArrayList<String>();

	ArrayList<String> hasMoved = new ArrayList<String>();

	HashMap<String, Integer> playerDelay = new HashMap<String, Integer>();
	HashMap<String, Integer> playerSpam = new HashMap<String, Integer>();

	public SpamListener() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				ArrayList<String> remove = new ArrayList<String>();
				for (String all : playerDelay.keySet()) {
					if (playerDelay.get(all) > 0) {
						playerDelay.put(all, playerDelay.get(all) - 1);
					} else {
						remove.add(all);
					}
				}
				for (String all : remove) {
					playerDelay.remove(all);
				}
			}
		}, 1, 1);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(event.getTo().getBlock().getX() != event.getFrom().getBlock().getX() || event.getTo().getBlock().getZ() != event.getFrom().getBlock().getZ()) {
			hasMoved.add(player.getName());
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		forceLeave.remove(player.getName());
		hasMoved.remove(player.getName());
		playerDelay.remove(player.getName());
		playerSpam.remove(player.getName());
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		
		if(!hasMoved.contains(player.getName()) && !player.hasPermission("ss.donator")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.RED + "Please move before chatting.");
			
		} else if(playerDelay.containsKey(player.getName()) && !player.hasPermission("ss.donator")) {
			event.setCancelled(true);
			player.sendMessage(ChatColor.AQUA + "You can only chat once every 3 seconds to prevent spam.");
			player.sendMessage(ChatColor.AQUA + "Purchase a Rank at " + ChatColor.GRAY + "www.swingsword.com/shop" + ChatColor.AQUA + " to remove this limit.");
			addSpam(player);
		}
		
		playerDelay.put(player.getName(), 60);
		if(!event.isCancelled()) {
			playerSpam.remove(player.getName());
		}
	}
	
	public void addSpam(final Player player) {
		if(playerSpam.containsKey(player.getName())) {
			playerSpam.put(player.getName(), playerSpam.get(player.getName()) + 1);
		} else {
			playerSpam.put(player.getName(), 1);
		}
		
		if(playerSpam.get(player.getName()) > 4) {
			Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					if(player.isOnline()) {
						player.kickPlayer(ChatColor.RED + "You have been kicked for spamming.");
					}
				}
			});
		}
	}
	
}
