package com.swingsword.ssengine.game.games.minestrike.listeners;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.game.GameState;
import com.swingsword.ssengine.game.games.minestrike.game.DeathManager;
import com.swingsword.ssengine.game.games.minestrike.game.RewardManager;
import com.swingsword.ssengine.game.team.Team;

import net.minecraft.server.v1_10_R1.EntityPlayer;

public class EntityDamageByEntity implements Listener {
	
	public static HashMap<Player, HashMap<Player, ArrayList<Double>>> playerDamageSet = new HashMap<Player, HashMap<Player, ArrayList<Double>>>();
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		//not canceled
		
		Player player = null;
		Player damager = null;
		
		if(e.getEntity() instanceof Player) {
			player = (Player) e.getEntity();
			
			if(e.getDamager() instanceof Player) {
				damager = (Player) e.getDamager();
			} else if(e.getDamager() instanceof Projectile) {
				Projectile proj = (Projectile) e.getDamager();
				
				if(proj.getShooter() instanceof Player) {
					damager = (Player) proj.getShooter();
				}
			}
			
			if(damager != null) {				
				if(Team.sameTeam(player, damager)) {
					e.setCancelled(true);
					return;
				}
			}
		}
		
		if (!(player instanceof Player) || !(damager instanceof Player)) {
			return;
		}
				
		EntityPlayer craftPlayer = ((CraftPlayer) player).getHandle();
		craftPlayer.killer = ((CraftPlayer) damager).getHandle();
		
		if(DeathManager.dead.contains(damager.getName())) {
			e.setCancelled(true);
			return;
		}

		if(!e.isCancelled()) {
			if(damager.getInventory().getHeldItemSlot() == 2 && damager.getItemInHand() != null && e.getCause() != DamageCause.PROJECTILE) {
				e.setDamage(7);
				
				if(!playerDamageSet.get(player).containsKey(damager)) {
					playerDamageSet.get(player).put(damager, new ArrayList<Double>());
				}
				playerDamageSet.get(player).get(damager).add(e.getDamage());
				
				if(e.getDamage() >= player.getHealth()) {
					RewardManager.reward(damager, 1500, "neutralizing an enemy with a knife");
					Bukkit.broadcastMessage(damager.getDisplayName() + ChatColor.RESET + " killed " + player.getDisplayName() + ChatColor.RESET + " using Knife");
				}	
			} else {
				if(e.getCause() != DamageCause.PROJECTILE) {
					e.setCancelled(true);
				}
			}
		}
		
		//canceled
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent e) {
		if(!GameState.isState(GameState.IN_GAME)) {
			e.setCancelled(true);
		}
		
		if(e.getEntity() instanceof Player) {
			final Player player = (Player) e.getEntity();
			
			if(DeathManager.dead.contains(player.getName())) {
				e.setCancelled(true);
			}
			
			if(e.getCause().equals(DamageCause.FIRE)) {
				e.setDamage(2);
			}
			
			if(e.getCause().equals(DamageCause.FIRE_TICK)) {
				e.setCancelled(true);
				player.setFireTicks(-10);
			}
			
			if(e.getDamage() >= player.getHealth()) {
				e.setCancelled(true);
				
				if(!DeathManager.dead.contains(player.getName())) {
					DeathManager.die(player);
					player.setVelocity(new Vector(0, 3, 0));
				}	
			}
		}
	}
	
	public static double getTotal(ArrayList<Double> array) {
		double total = 0d;
		
		if(array != null) {
			for(int x = 0; x < array.size(); x++) {
				total += array.get(x);
			}
		}
		
		return total;
	}
}
