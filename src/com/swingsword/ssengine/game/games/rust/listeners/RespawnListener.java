package com.swingsword.ssengine.game.games.rust.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.utils.SpawnUtils;

public class RespawnListener implements Listener {

	public RespawnListener() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(all.hasPotionEffect(PotionEffectType.INVISIBILITY) && !all.getOpenInventory().getTopInventory().getTitle().contains("Option")) {
						SpawnUtils.respawnPlayer(all, SpawnUtils.getRandomSpawn());
					}
				}
			}
		}, 20, 20);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(player.hasPotionEffect(PotionEffectType.INVISIBILITY) && (event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockZ() != event.getFrom().getBlockZ())) {
			player.teleport(event.getFrom());
			SpawnUtils.processDeath(player);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			
			if(player.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				event.setCancelled(true);
			}
		}
	}
}
