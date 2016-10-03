package com.swingsword.ssengine.game.games.hub.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.entity.InteractEntity;
import com.swingsword.ssengine.game.Game;
import com.swingsword.ssengine.game.games.hub.Hub;
import com.swingsword.ssengine.player.PlayerSessionManager;

public class PlayerListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		Game.resetPlayer(player);
		
		Location spawnLoc = new Location(Bukkit.getWorld("map"), Double.parseDouble(Hub.plugin.getLoadedMap().getMapConfig().getString("spawn.x")), Double.parseDouble(Hub.plugin.getLoadedMap().getMapConfig().getString("spawn.y")), Double.parseDouble(Hub.plugin.getLoadedMap().getMapConfig().getString("spawn.z")), Float.parseFloat(Hub.plugin.getLoadedMap().getMapConfig().getString("spawn.yaw")), Float.parseFloat(Hub.plugin.getLoadedMap().getMapConfig().getString("spawn.pitch")));
		while(spawnLoc.getBlock().getRelative(0, -1, 0).getType().equals(Material.AIR) && spawnLoc.getBlockY() > 0) {
			spawnLoc.setY(spawnLoc.getBlockY() - 1);
		}
		
		player.teleport(spawnLoc);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		player.setAllowFlight(false);
	}
		
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(player.isOnGround() && player.getAllowFlight() == false) {
			player.setFlying(false);
			player.setAllowFlight(true);
			player.setFlying(false);
		}
		
		player.setFallDistance(-10);
	}
	
	@EventHandler
	public static void onPlayerToggleFly(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		
		if(player.getGameMode() != GameMode.CREATIVE) {
			boolean flying = PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("fly") != null && PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("fly").equals("1");
			
			if(player.getAllowFlight() == true && !flying) {
				player.setFlying(false);
				player.setAllowFlight(false);
				
				Vector vec = player.getLocation().getDirection().multiply(1.5f);
				vec.setY(0.5f + vec.getY());
				
				player.setVelocity(vec);
				
				player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_GROWL, 0.5f, 2f);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(InteractEntity.getInteractEntities().contains(event.getEntity())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		if(!event.getPlayer().isOp()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onClickEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		
		if(InteractEntity.getInteractEntity(event.getRightClicked()) != null) {
			player.chat(InteractEntity.getInteractEntity(event.getRightClicked()).command);
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if(event.getDamager() instanceof Player) {
			Player player = (Player) event.getDamager();
			
			if(InteractEntity.getInteractEntity(event.getEntity()) != null) {
				player.chat(InteractEntity.getInteractEntity(event.getEntity()).command);
			}
		}
	}
}
