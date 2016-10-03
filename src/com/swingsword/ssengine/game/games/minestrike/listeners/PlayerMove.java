package com.swingsword.ssengine.game.games.minestrike.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.GameState;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.games.minestrike.game.DeathManager;
import com.swingsword.ssengine.game.games.minestrike.guns.GunListener;
import com.swingsword.ssengine.game.games.minestrike.zones.BombSite;
import com.swingsword.ssengine.game.games.minestrike.zones.BuyZone;
import com.swingsword.ssengine.game.threads.TimerHandler;
import com.swingsword.ssengine.utils.ItemUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class PlayerMove implements Listener {
	
	public PlayerMove() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					for(Player all : Bukkit.getOnlinePlayers()) {
    					GunListener.playerOldLoc.put(all.getName(), all.getLocation());
    				}
				}
		}, 3, 3);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		Player p = (Player) e.getPlayer();
		
		if(!DeathManager.dead.contains(p.getName())) {
			if (e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()){
		    	return;
		    }
			if (TimerHandler.getTimer("buytime") != null && TimerHandler.getTimer("buytime").getLeft() >= 5 && CSGOGame.hasStarted()) {
				if(e.getFrom().getBlock().getRelative(0, -1, 0).getType() == Material.AIR) {
					e.setTo(e.getFrom().clone().add(0, -1, 0));
				} else {
					e.setTo(e.getFrom());
				}
				
				return;
			}
		}
		
		if(GameState.isState(GameState.IN_GAME) && !DeathManager.dead.contains(p.getName())) {
			for(BuyZone all : BuyZone.buyZones) {
				if(all.r.contains(e.getTo()) && !all.r.contains(e.getFrom())) {
					StringUtils.sendTitle(p, 5, 20, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Entered buy zone");
					
					p.getInventory().setItem(8, ItemUtils.itemStackFromString("i=54;n=&eBuy"));
				}
				
				if(!all.r.contains(e.getTo()) && all.r.contains(e.getFrom())) {
					StringUtils.sendTitle(p, 5, 20, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Left buy zone");
					
					p.getInventory().setItem(8, null);
					p.closeInventory();
				}
			}
			
			if(CSGOGame.sites.size() > 0) {
				for(BombSite site : CSGOGame.sites) {
					if(site.r.contains(e.getTo()) && !site.r.contains(e.getFrom())) {
						StringUtils.sendTitle(p, 5, 20, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Entered bomb site");
					}
					
					if(!site.r.contains(e.getTo()) && site.r.contains(e.getFrom())) {
						StringUtils.sendTitle(p, 5, 20, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Left bomb site");
					}
				}
			}
		}
	}
}
