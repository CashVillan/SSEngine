package com.swingsword.ssengine.game.games.minestrike.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.games.minestrike.game.CustomItems;
import com.swingsword.ssengine.game.games.minestrike.guns.Gun;
import com.swingsword.ssengine.game.games.minestrike.guns.GunData;
import com.swingsword.ssengine.game.games.minestrike.guns.GunType;
import com.swingsword.ssengine.game.games.minestrike.guns.SkinnedGun;
import com.swingsword.ssengine.game.team.Team;
import com.swingsword.ssengine.inventory.PlayerInventory;
import com.swingsword.ssengine.utils.InventoryUtils;

public class BuyUtils {

	public static Inventory getBuyMenu(final Player player, final String team, final String title) {
		int size = 54;
		if(title.contains(" Loadout")) {
			size = 36;
		}
		
		final Inventory inv = Bukkit.createInventory(player, size, title);
		
		inv.setItem(0, ItemUtils.itemStackFromString("i=339;n=&fPISTOLS"));
		inv.setItem(9, ItemUtils.itemStackFromString("i=339;n=&fHEAVY"));
		inv.setItem(18, ItemUtils.itemStackFromString("i=339;n=&fSMG"));
		inv.setItem(27, ItemUtils.itemStackFromString("i=339;n=&fRIFLES"));
		
		if(!title.contains(" Loadout")) {
			inv.setItem(36, ItemUtils.itemStackFromString("i=339;n=&fEQUIPMENT"));
			inv.setItem(45, ItemUtils.itemStackFromString("i=339;n=&fGRENADES"));
		}
		
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Gun gun : GunData.guns) {
					if(!(gun instanceof SkinnedGun)) {
						if(gun.getTeam().equals("ALL") || gun.getTeam().equalsIgnoreCase(team)) {
							ItemStack item = getGunItem(gun.getName());
							
							if(title.contains(" Loadout")) {
								ItemMeta meta = item.getItemMeta();
								List<String> lore = meta.getLore();
								lore.add("");
								
								int totalSkins = 0;
								for(com.swingsword.ssengine.inventory.Item skin : new PlayerInventory(player.getName()).getItems()) {
									if(ItemUtils.getDisplayName(skin.getStack()).contains(" | ")) {
										if(Gun.getGun(skin.getType()).getSelectionSlot() == gun.getSelectionSlot()) {
											totalSkins += 1;
										}
									}
								}
								
								if(totalSkins > 0) {
									lore.add(ChatColor.YELLOW + "Click to select gun skin.");
								} else {
									lore.add(ChatColor.RED + "No skins available.");
								}
								
								meta.setLore(lore);
								item.setItemMeta(meta);
							}
							
							inv.setItem(gun.getSelectionSlot(), item);
						}
					}
				}
				
				/*for(int gunId : Loadout.getLoadout(player).items) {
					Gun gun = Gun.getGun(gunId);
						
					if(gun != null) {
						if((gun.getTeam().equals("ALL") || gun.getTeam().equalsIgnoreCase(team)) && PlayerSessionManager.getSession(player).getAccount().getInventory().contains(ItemManager.getItem(gunId))) {
							ItemStack item = getGunItem(gun.getName());
							
							if(title.contains(" Loadout")) {
								ItemMeta meta = item.getItemMeta();
								List<String> lore = meta.getLore();
								lore.add("");
								
								int totalSkins = 0;
								for(com.swingsword.ssengine.inventory.Item skin : PlayerSessionManager.getSession(player).getAccount().getInventory()) {
									if(ItemUtils.getDisplayName(skin.getStack()).contains(" | ")) {
										if(Gun.getGun(skin.typeId).getSelectionSlot() == gun.getSelectionSlot()) {
											totalSkins += 1;
										}
									}
								}
								
								if(totalSkins > 0) {
									lore.add(ChatColor.YELLOW + "Click to select gun skin.");
								} else {
									lore.add(ChatColor.RED + "No skins available.");
								}
								
								meta.setLore(lore);
								item.setItemMeta(meta);
							}
							
							inv.setItem(gun.getSelectionSlot(), item);
						}
						
					} else if(isKnife(gunId)) {
						ItemStack item = ItemManager.getItem(gunId).getItemStack();
						
						if(title.contains(" Loadout")) {
							ItemMeta meta = item.getItemMeta();
							List<String> lore = meta.getLore();
							lore.add("");
							
							int totalSkins = 0;
							for(com.swingsword.ssengine.item.Item skin : PlayerSessionManager.getSession(player).getAccount().getInventory()) {
								if(com.swingsword.ssengine.utils.ItemUtils.itemStackToString(skin.getItemStack()).equals(com.swingsword.ssengine.utils.ItemUtils.itemStackToString(item))) {
									totalSkins += 1;
								}
							}
							
							if(totalSkins > 0) {
								lore.add(ChatColor.YELLOW + "Click to select knife skin.");
							} else {
								lore.add(ChatColor.RED + "No skins available.");
							}
							
							meta.setLore(lore);
							item.setItemMeta(meta);
							
							inv.setItem(44, item);
						}
					}
				}*/
			}
		});
		
		if(!title.contains(" Loadout")) {
			inv.setItem(46, CustomItems.hegranade);
			inv.setItem(47, CustomItems.flash);
			inv.setItem(48, CustomItems.smoke);
			
			if(Team.getTeam(player).getName().contains("CT")) {
				inv.setItem(49, CustomItems.incendiary);
				inv.setItem(40, CustomItems.defuseKit);
			} else {
				inv.setItem(49, CustomItems.molotov);
			}
			
			inv.setItem(37, CustomItems.kevlarVest);
			inv.setItem(38, CustomItems.kevlarHelmet);
			inv.setItem(39, CustomItems.zeus);
		}
		
		return inv;
	}
	
	public static Inventory getLoadoutOptions(final Player player, String gunName) {
		final Gun gun = Gun.getGun(gunName);
		
		final Inventory inv = Bukkit.createInventory(null, 27, "Replace " + gunName);
		
		//TODO knife selection mechanics
		
		for(Gun all : GunData.guns) {
			if(gun.getSelectionSlot() == all.getSelectionSlot() && (all.getTeam().equals("ALL") || gun.getTeam().equals(all.getTeam())) && !(all instanceof SkinnedGun)) {				
				inv.addItem(all.toItemStack(false, null));
			}
		}
		
		inv.setItem(26, ItemUtils.createItem(Material.ARROW, 1, 0, ChatColor.YELLOW + "Back", null));
		
		/*Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
			public void run() {
				for(com.swingsword.ssengine.inventory.Item skin : PlayerSessionManager.getSession(player).getAccount().getInventory()) {
					if(ItemUtils.getDisplayName(skin.getItemStack()).contains(" | ")) {
						if(Gun.getGun(skin.getID()).getSelectionSlot() == gun.getSelectionSlot()) {
							inv.addItem(Gun.getGun(skin.getID()).toItemStack(false, null));
						}
					}
				}
			}
		});*/
		
		return inv;
	}
	
	public static ItemStack getGunItem(String gunName) {
		Gun gun = Gun.getGun(gunName);
		ItemStack item = gun.toItemStack(false, null);
		ItemMeta meta = item.getItemMeta();
		
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Cost: " + ChatColor.WHITE + "$" + gun.getPrice());
		lore.add(ChatColor.GRAY + "Ammo: " + gun.getMagazineSize() + "/" + gun.getSpawnAmmo());
		lore.add(ChatColor.GRAY + "Kill Award: " + ChatColor.GREEN + "+$" + gun.getKillReward());
		lore.add("");
		lore.add(ChatColor.GRAY + "Firepower: " + ChatUtils.getProgressbar(20, (float) ((float) gun.getDamage() / (float) 20), ChatColor.GREEN, ChatColor.DARK_GRAY));
		lore.add(ChatColor.GRAY + "Firerate: " + ChatUtils.getProgressbar(20, (float) ((float) gun.getRPM() / (float) 1000), ChatColor.GREEN, ChatColor.DARK_GRAY));
		lore.add(ChatColor.GRAY + "Accuracy: " + ChatUtils.getProgressbar(20, (float) ((float) 1 / (float) (gun.getAccurracy() + 1)), ChatColor.GREEN, ChatColor.DARK_GRAY));

		meta.setLore(lore);
		item.setItemMeta(meta);
		item.setAmount(1);
		
		return item;
	}
	
	public static void buyGun(Player player, Gun gun) {
		if(CSGOGame.Money.get(player.getName()) >= gun.getPrice()) {
			CSGOGame.Money.put(player.getName(), CSGOGame.Money.get(player.getName()) - gun.getPrice());
			
			if(gun.getGunType() == GunType.PRIMARY) {
				if(player.getInventory().getItem(0) != null) {
					ItemStack item = player.getInventory().getItem(0);
					item.setAmount(1);
					
					Item drop = player.getWorld().dropItem(player.getLocation(), item);
					drop.setCustomName(drop.getItemStack().getItemMeta().getDisplayName());
					drop.setCustomNameVisible(false);
				}
				
				player.getInventory().setItem(0, gun.toItemStack(true, player.getName()));
				
			} else if(gun.getGunType() == GunType.SECONDARY) {
				if(player.getInventory().getItem(1) != null) {
					ItemStack item = player.getInventory().getItem(1);
					item.setAmount(1);
					
					Item drop = player.getWorld().dropItem(player.getLocation(), item);
					drop.setCustomName(drop.getItemStack().getItemMeta().getDisplayName());
					drop.setCustomNameVisible(true);
				}
				
				player.getInventory().setItem(1, gun.toItemStack(true, player.getName()));
			}
			
		} else {
			player.sendMessage(ChatColor.RED + "You don't have the money to buy that.");
		}
	}
	
	public static void buyNade(Player player, ItemStack nade) {
		int price = Integer.parseInt(ChatColor.stripColor(nade.getItemMeta().getLore().get(0)).toLowerCase().replace("cost: $", ""));
		
		if(CSGOGame.Money.get(player.getName()) >= price) {
			if(InventoryUtils.getFirstFreeFrom(player.getInventory(), 4) < 7) {
				CSGOGame.Money.put(player.getName(), CSGOGame.Money.get(player.getName()) - price);
				
				ItemStack usableNade = nade.clone();
				ItemMeta nadeMeta = usableNade.getItemMeta();
				nadeMeta.setLore(null);
				usableNade.setItemMeta(nadeMeta);
				
				player.getInventory().setItem(InventoryUtils.getFirstFreeFrom(player.getInventory(), 4), usableNade);
				
			} else {
				player.sendMessage(ChatColor.RED + "You can't buy more nades.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "You don't have the money to buy that.");
		}
	}
	
	public static void buyGear(Player player, ItemStack item) {
		int price = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).toLowerCase().replace("cost: $", ""));
		
		if(CSGOGame.Money.get(player.getName()) >= price) {
			ItemStack usableGear = item.clone();
			ItemMeta gearMeta = usableGear.getItemMeta();
			gearMeta.setLore(null);
			usableGear.setItemMeta(gearMeta);
			
			int slot = -1;
			
			if(item.isSimilar(CustomItems.zeus)) {
				slot = 3;
			}
			if(item.isSimilar(CustomItems.defuseKit)) {
				slot = 7;
			}
			if(item.isSimilar(CustomItems.kevlarHelmet)) {
				slot = 9;
			}
			if(item.isSimilar(CustomItems.kevlarVest)) {
				slot = 18;
			}
			
			if(slot != -1 && player.getInventory().getItem(slot) == null) {
				player.getInventory().setItem(slot, usableGear);
				CSGOGame.Money.put(player.getName(), CSGOGame.Money.get(player.getName()) - price);
				
			} else {
				player.sendMessage(ChatColor.RED + "You already own this item.");
			}
			
		} else {
			player.sendMessage(ChatColor.RED + "You don't have the money to buy that.");
		}
	}
	
	//Knife
	
	/*public static boolean isKnife(int id) {
		String name = ItemManager.getItem(id).getItemStack().getItemMeta().getDisplayName();
		
		return name.contains("Karambit") || name.contains("Knife") || name.contains("Bayonet");
	}*/
}
