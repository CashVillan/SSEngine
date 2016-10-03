package com.swingsword.ssengine.game.games.rust.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.Rust;

public class InventoryUtils {
	
	public static void loadInventoryLimiter() {
		if(Rust.plugin.getLoadedMap().getMapConfig().contains("stackSizes")) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					for(Player player : Bukkit.getOnlinePlayers()) {
						for(ItemStack item : player.getInventory().getContents()) {
							if(item != null) {
								int id = item.getTypeId();
								byte data = item.getData().getData();
								
								if(Rust.plugin.getLoadedMap().getMapConfig().getConfigurationSection("stackSizes").contains(id + ";" + data)) {
									int maxSize = Rust.plugin.getLoadedMap().getMapConfig().getConfigurationSection("stackSizes").getInt(id + ";" + data);
									
									if(item.getAmount() > maxSize) {
										int dropSize = item.getAmount() - maxSize;
										item.setAmount(maxSize);
										
										while(dropSize > 0) {
											ItemStack dropItem = item.clone();
											if(dropSize > maxSize) {
												dropItem.setAmount(maxSize);
												dropSize -= maxSize;
											} else {
												dropItem.setAmount(dropSize);
												dropSize = 0;
											}
											
											int slot = com.swingsword.ssengine.utils.InventoryUtils.getFirstFreeFrom(player.getInventory(), 0);
											if(slot == -1) {
												Item drop = player.getWorld().dropItemNaturally(player.getLocation(), item);
												drop.setPickupDelay(60);
												
											} else {
												player.getInventory().setItem(slot, dropItem);
												player.updateInventory();
											}
										}
									}
								}
							}
						}
					}
				}
			}, 10, 10);
		}
	}
}
