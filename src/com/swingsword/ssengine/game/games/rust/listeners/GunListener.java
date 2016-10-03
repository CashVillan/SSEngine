package com.swingsword.ssengine.game.games.rust.listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.guns.Gun;
import com.swingsword.ssengine.game.games.rust.guns.GunData;
import com.swingsword.ssengine.game.games.rust.guns.Guns;
import com.swingsword.ssengine.game.games.rust.utils.ItemUtils;

public class GunListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (Gun.isGun(player.getItemInHand())) {
				Gun gun = Gun.getGun(player.getItemInHand().getItemMeta().getDisplayName());

				if (player != null && gun != null) {
					gun.reload(player, gun);
				}
			}

		} else if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (Gun.isGun(player.getItemInHand())) {
				Gun gun = Gun.getGun(ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName()));

				if (Gun.canShoot(player, gun)) {
					gun.shoot(player, gun);
					event.setCancelled(true);
					event.setUseItemInHand(Result.DENY);

				} else if (!GunData.reloading.containsKey(player) && !GunData.delay.containsKey(player)) {
					player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5F, 2);

					gun.reload(player, gun);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		
		if(GunData.reloading.containsKey(player)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if(event.getWhoClicked() instanceof Player) {
			Player player = (Player) event.getWhoClicked();
			
			if(GunData.reloading.containsKey(player)) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onProjectileHit(final ProjectileHitEvent event) {
		if(GunData.bullets.containsKey(event.getEntity())) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					GunData.bullets.remove(event.getEntity());
					GunData.bulletVel.remove(event.getEntity());
					event.getEntity().remove();
				}
			}, 1L);
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		if(event.getCause().equals(TeleportCause.ENDER_PEARL)) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if(event.getPlayer() instanceof Player) {
			Player player = (Player) event.getPlayer();
			
			if(Guns.playerEquip.containsKey(player)) {
				List<String> lore = ItemUtils.getLore(player.getItemInHand());
				
				if(lore != null) {
					if(!ItemUtils.loreContains(lore, "Attachments:")) {
						lore.add("");
						lore.add(ChatColor.GRAY + "Attachments:");
					}
					
					for(ItemStack all : Guns.playerEquip.get(player)) {
						if(all != null && all.getItemMeta() != null && all.getItemMeta().getDisplayName() != null) {
							if(all.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Silencer") && !ItemUtils.loreContains(lore, "Silencer")) {
								lore.add(ChatColor.GRAY + "Silencer");
								player.sendMessage(ChatColor.GREEN + "Equipped Silencer.");
								
							} else if(all.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Flashlight Mod") && !ItemUtils.loreContains(lore, "Flashlight Mod")) {
								lore.add(ChatColor.GRAY + "Flashlight Mod");
								player.sendMessage(ChatColor.GREEN + "Equipped Flashlight Mod.");
								
							} else if(all.getItemMeta().getDisplayName().equalsIgnoreCase(ChatColor.WHITE + "Holo Sight") && !ItemUtils.loreContains(lore, "Holo Sight")) {
								lore.add(ChatColor.GRAY + "Holo Sight");
								player.sendMessage(ChatColor.GREEN + "Equipped Holo Sight.");
								
							} else {
								player.getInventory().addItem(all);
							}
						}
					}
					
					ItemMeta meta = player.getItemInHand().getItemMeta();
					meta.setLore(lore);
					player.getItemInHand().setItemMeta(meta);
					
					player.updateInventory();
				}
				
				Guns.playerEquip.remove(player);
			}
		}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		if(player.getInventory().getHelmet() != null) {
			if(player.getInventory().getHelmet().getType() == Material.PUMPKIN) {
				if(GunData.playerHelmet.containsKey(player)) {
					player.getInventory().setHelmet(GunData.playerHelmet.get(player));
					GunData.playerHelmet.remove(player);
				}
				player.updateInventory();
			}
		}
	}
}
