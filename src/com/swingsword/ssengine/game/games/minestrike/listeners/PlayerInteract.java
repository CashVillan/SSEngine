package com.swingsword.ssengine.game.games.minestrike.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.games.minestrike.game.CustomItems;
import com.swingsword.ssengine.game.games.minestrike.game.DeathManager;
import com.swingsword.ssengine.game.games.minestrike.game.DefuseManager;
import com.swingsword.ssengine.game.games.minestrike.guns.Gun;
import com.swingsword.ssengine.game.games.minestrike.utils.BuyUtils;
import com.swingsword.ssengine.game.games.minestrike.utils.ChatUtils;
import com.swingsword.ssengine.game.games.minestrike.utils.NadeUtils;
import com.swingsword.ssengine.game.games.minestrike.zones.BuyZone;
import com.swingsword.ssengine.game.team.Team;
import com.swingsword.ssengine.game.threads.TimerHandler;
import com.swingsword.ssengine.utils.ItemUtils;

public class PlayerInteract implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(player.getItemInHand() != null && player.getItemInHand().getType() == Material.CHEST) {
			for(BuyZone all : BuyZone.buyZones) {
				if(all.r.contains(player.getLocation())) {
					if(TimerHandler.getTimer("buytime") != null) {
						player.openInventory(BuyUtils.getBuyMenu(player, Team.getTeam(player).getName(), "Buy ($" + CSGOGame.Money.get(player.getName()) + ")"));
					} else {
						ChatUtils.sendTitle(player, 5, 20, 5, ChatColor.RED + "Alert", "The 20 second buy period has expired");
					}
				}
			}
		}
		
		if(event.getClickedBlock() != null && CSGOGame.bombLoc != null && event.getClickedBlock().getLocation().toVector().equals(CSGOGame.bombLoc.toVector()) && Team.getTeam(player).getName().equals("CT") && DefuseManager.defuser == null && !DeathManager.dead.contains(player.getName()) && !CSGOGame.inPostGameTime) {
			DefuseManager.defuse(player);
		}
		
		if(player.getItemInHand().getType() == Material.BONE) {
			player.setItemInHand(null);
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(all.getLocation().distance(player.getLocation()) < 10) {
					all.playSound(player.getLocation(), "guns.zeus.fire", 1, 1);
				}
			}
			
			for(float x = 1f; x < 5; x += 0.25f) {
				Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(x));
				
				player.getWorld().playEffect(loc, Effect.HAPPY_VILLAGER, 30);
				
				for(Player all : Bukkit.getOnlinePlayers()) {
					if((all.getLocation().getBlock().getLocation().distance(loc.getBlock().getLocation()) < 0.25 || all.getEyeLocation().getBlock().getLocation().distance(loc.getBlock().getLocation()) < 0.25) && all != player) {
						all.damage(9001, player);
					}
				}
			}
		}
		
		if(player.getItemInHand().getType() == Material.COMPASS && !ItemUtils.getDisplayName(player.getItemInHand()).contains("Bomb")) {
			event.setCancelled(true);
			
			Channel.sendToServer(player, Channel.getBestHub());
		}
		
		if(player.getItemInHand().getType() == Material.BOOK) {
			event.setCancelled(true);
			
			player.openInventory(DeathManager.getSpectateInventory());
		}
		
		if(event.getAction().name().contains("RIGHT")) {
			if(player.getItemInHand() != null) {
				Material type = player.getItemInHand().getType();
				
				if(type == CustomItems.hegranade.getType()) {
					NadeUtils.throwNade(player, "he");
				}
				if(type == CustomItems.flash.getType()) {
					NadeUtils.throwNade(player, "flash");
				}
				if(type == CustomItems.smoke.getType()) {
					NadeUtils.throwNade(player, "smoke");
				}
				if(type == CustomItems.incendiary.getType() || type == CustomItems.molotov.getType()) {
					NadeUtils.throwNade(player, "fire");
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		if(DeathManager.dead.contains(player.getName())) {
			event.setCancelled(true);
		}
		
		if(!Gun.isGun(event.getItemDrop().getItemStack()) && event.getItemDrop().getItemStack().getType() != Material.TNT) {
			event.setCancelled(true);
			
			if(player.getInventory().getHeldItemSlot() == 2 || player.getInventory().getHeldItemSlot() == 8) {
				ChatUtils.sendTitle(player, 5, 20, 5, ChatColor.RED + "Alert", "You cannot drop that");
				return;
			}
		}
		
		if(event.getItemDrop().getItemStack().getType() == Material.COMPASS) {
			event.setCancelled(true);
			return;
		}
		
		if(event.getItemDrop().getItemStack().getType() == Material.TNT) {
			player.getInventory().setItem(7, CustomItems.bombTracker);
		}
		
		if(Gun.isGun(event.getItemDrop().getItemStack())) {
			player.setItemInHand(null);
			event.getItemDrop().getItemStack().setAmount(1);
			
			ChatUtils.sendTitle(player, 5, 20, 5, ChatColor.RED + "Alert", "You have dropped your " + ChatColor.stripColor(Gun.getGun(event.getItemDrop().getItemStack().getItemMeta().getDisplayName()).getName()));
			
		} else {
			ChatUtils.sendTitle(player, 5, 20, 5, ChatColor.RED + "Alert", "You have dropped your " + ChatColor.stripColor(event.getItemDrop().getItemStack().getItemMeta().getDisplayName()));
		}
	}
}
