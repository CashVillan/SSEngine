package com.swingsword.ssengine.game.games.minestrike.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.minestrike.database.Loadout;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.games.minestrike.game.CustomItems;
import com.swingsword.ssengine.game.games.minestrike.guns.Gun;
import com.swingsword.ssengine.game.games.minestrike.utils.BuyUtils;
import com.swingsword.ssengine.game.team.Team;

public class InventoryClick implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();
		
		event.setCancelled(true);
		if(player.isOp() && player.getGameMode() == GameMode.CREATIVE) {
			event.setCancelled(false);
		}
		
		if(event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getDisplayName() != null) {
			if(player.getOpenInventory().getTopInventory().getTitle().contains("Spectate")) {
				event.setCancelled(true);
				
				if(event.getCurrentItem().getType() == Material.SKULL_ITEM) {
					String target = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
					
					if(Bukkit.getOfflinePlayer(target).isOnline()) {
						player.teleport(Bukkit.getPlayer(target));
						player.closeInventory();
					}
				}
				
			} else if(player.getOpenInventory().getTopInventory().getTitle().contains("Buy ")) {
				event.setCancelled(true);
				
				if(Gun.isGun(event.getCurrentItem())) {
					BuyUtils.buyGun(player, Gun.getGun(event.getCurrentItem().getItemMeta().getDisplayName()));
					
					player.openInventory(BuyUtils.getBuyMenu(player, Team.getTeam(player).getName(), "Buy ($" + CSGOGame.Money.get(player.getName()) + ")"));
					
				} else if(CustomItems.isNade(event.getCurrentItem())) {
					BuyUtils.buyNade(player, event.getCurrentItem());
				
				} else if(CustomItems.isGear(event.getCurrentItem())) {
					BuyUtils.buyGear(player, event.getCurrentItem());
				}
				
			} else if(player.getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("Select team loadout")) {
				event.setCancelled(true);
				
				if(event.getCurrentItem() != null) {
					player.closeInventory();
					
					if(event.getCurrentItem().getType() == Material.BLAZE_ROD) {
						player.chat("/loadout ct");
						
					} else {
						player.chat("/loadout t");
					}
				}
			
			} else if(player.getOpenInventory().getTopInventory().getTitle().contains(" Loadout")) {
				event.setCancelled(true);
				
				if(event.getCurrentItem() != null && Gun.isGun(event.getCurrentItem())) {
					String name = Gun.getGun(event.getCurrentItem().getItemMeta().getDisplayName()).getName();
					
					player.closeInventory();
					
					player.openInventory(BuyUtils.getLoadoutOptions(player, name));
				}
			
			} else if(player.getOpenInventory().getTopInventory().getTitle().contains("Replace")) {
				String current = "";
				for(int x = 1; x < player.getOpenInventory().getTopInventory().getTitle().split(" ").length; x++) {
					if(current.equals("")) {
						current = player.getOpenInventory().getTopInventory().getTitle().split(" ")[x];
					} else {
						current = current + " " + player.getOpenInventory().getTopInventory().getTitle().split(" ")[x];
					}
				}
				final String finalCurrent = current;
				
				event.setCancelled(true);
				
				if(event.getCurrentItem() != null) {
					if(Gun.isGun(event.getCurrentItem())) {
						final String gunName = Gun.getGun(event.getCurrentItem().getItemMeta().getDisplayName()).getName();
						
						if(!gunName.equalsIgnoreCase(current)) {
							player.closeInventory();
							
							Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
								public void run() {
									Loadout lo = Loadout.getLoadout(player);
									
									lo.addGun(gunName);
									lo.saveLoadout(player);
									
									player.sendMessage(ChatColor.GREEN + "You have equipped your '" + gunName + "'.");
									
									player.closeInventory();
									Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
										public void run() {
											if(Gun.getGun(gunName).getTeam().equals("ALL")) {
												player.chat("/loadout");
											} else {
												player.chat("/loadout " + Gun.getGun(gunName).getTeam());
											}
										}
									}, 5);
								}
							});
						} else {
							player.sendMessage(ChatColor.RED + "You've already equipped this skin.");
						}
						
					} else if(event.getCurrentItem().getType() == Material.ARROW) {
						player.closeInventory();
						Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
							public void run() {
								if(Gun.getGun(finalCurrent).getTeam().equals("ALL")) {
									player.chat("/loadout");
								} else {
									player.chat("/loadout " + Gun.getGun(finalCurrent).getTeam());
								}
							}
						}, 5);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryMoveItem(InventoryMoveItemEvent event) {
		event.setCancelled(true);
	}
}
