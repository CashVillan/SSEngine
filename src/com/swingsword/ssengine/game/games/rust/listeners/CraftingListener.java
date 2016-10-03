package com.swingsword.ssengine.game.games.rust.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.crafting.Crafting;
import com.swingsword.ssengine.game.games.rust.crafting.InventoryManager;
import com.swingsword.ssengine.game.games.rust.utils.ItemUtils;
import com.swingsword.ssengine.game.games.rust.utils.MathUtils;
import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.StringUtils;

@SuppressWarnings("deprecation")
public class CraftingListener implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		Crafting.loadResearch(player.getName());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if (Crafting.playerCraft.containsKey(player.getName()) && !Crafting.settingAmount.contains(player.getName())) {
			for (ItemStack all : InventoryManager.itemNeeded.get(Crafting.playerCraft.get(player.getName()))) {
				player.getInventory().addItem(all.clone());
			}

			Crafting.playerCraft.remove(player.getName());
			Crafting.playerCraftAmount.remove(player.getName());
			player.setExp(0);
		}

		Crafting.save(player.getName());

		Crafting.unlocked.remove(player);

		Crafting.playerCraft.remove(player.getName());
		Crafting.playerCraftAmount.remove(player.getName());
		Crafting.settingAmount.remove(player.getName());
		Crafting.confirm.remove(player.getName());
		Crafting.selecting.remove(player.getName());
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack inHand = event.getPlayer().getItemInHand();

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (event.getClickedBlock().getType() == Material.STONE_SLAB2 || event.getClickedBlock().getType() == Material.WORKBENCH) {
				event.setCancelled(true);
				player.openInventory(InventoryManager.craftInv);

				return;
			}
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			if (player.getItemInHand() != null) {
				if (player.getItemInHand().getItemMeta() != null) {
					if (player.getItemInHand().getItemMeta().getDisplayName() != null) {
						FileConfiguration cache = ConfigUtils.getConfig("cache");
						if (cache.getConfigurationSection("crafting.blueprints").getKeys(false).contains(player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").replace(ChatColor.COLOR_CHAR + "", "&"))) {
							String perm = cache.getString("crafting.blueprints." + player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").replace(ChatColor.COLOR_CHAR + "", "&") + ".permission");
							int item = cache.getInt("crafting.blueprints." + player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").replace(ChatColor.COLOR_CHAR + "", "&") + ".item");

							if (player.getItemInHand().getTypeId() == item) {
								if (Crafting.confirm.contains(player.getName())) {
									Crafting.confirm.remove(player.getName());
									
									Crafting.addResearch(player.getName(), perm);

									StringUtils.sendTitle(player, 5, 40, 5, ChatColor.GREEN + "Reseached " + player.getItemInHand().getItemMeta().getDisplayName().toLowerCase().replace("blueprint", "") + ChatColor.GREEN + "!", null);


									ItemUtils.removeItem(player, player.getItemInHand(), 1);

								} else if ((!Crafting.hasResearched(player.getName(), perm)) || (player.hasPermission(perm))) {
									Crafting.confirm(player);

								} else {
									StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "Already researched!", null);
								}
							}

						} else if (ChatColor.stripColor(player.getItemInHand().getItemMeta().getDisplayName()).equalsIgnoreCase("Research Kit 1")) {
							Crafting.selecting.add(player.getName());
							player.openInventory(player.getInventory());
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();

		if (Crafting.selecting.contains(player.getName())) {
			Crafting.selecting.remove(player.getName());
		}
	}

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if ((event.getWhoClicked() instanceof Player)) {
			final Player player = (Player) event.getWhoClicked();
			
			if ((event.getAction().name().contains("DROP")) && ((player.getOpenInventory().getTopInventory().equals(InventoryManager.craftInv)) || player.getOpenInventory().getTopInventory().getName().contains("Crafting"))) {
				event.setCancelled(true);
				player.closeInventory();
			}

			if (Crafting.settingAmount.contains(player.getName())) {
				event.setCancelled(true);

				player.updateInventory();

				Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						if (player.isOnline()) {
							player.updateInventory();
						}
					}
				}, 1L);

				return;
			}

			if (event.getClickedInventory() != null) {
				if (player.getOpenInventory().getTopInventory().equals(InventoryManager.craftInv)) {
					event.setCancelled(true);

					player.updateInventory();

					Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
						public void run() {
							if (player.isOnline()) {
								player.updateInventory();
							}
						}
					}, 1L);

					if (event.getCurrentItem() != null) {
						if (event.getCurrentItem().equals(InventoryManager.back)) {
							event.getWhoClicked().closeInventory();
							event.getWhoClicked().openInventory(InventoryManager.craftInv);
						}

						if (InventoryManager.sections.contains(event.getCurrentItem())) { 
							event.getWhoClicked().closeInventory();
							player.openInventory(InventoryManager.getSection(player, event.getCurrentItem(), ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).replace(" ", "_")));
						}
					}
				} else {
					int z;
					if (player.getOpenInventory().getTopInventory().getTitle().contains("Crafting -")) {
						event.setCancelled(true);

						player.updateInventory();

						Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
							public void run() {
								if (player.isOnline()) {
									player.updateInventory();
								}
							}
						}, 1L);

						if (event.getCurrentItem().getTypeId() != 0) {
							if (event.getCurrentItem().equals(InventoryManager.back)) {
								event.getWhoClicked().closeInventory();
								event.getWhoClicked().openInventory(InventoryManager.craftInv);
							}

							if (!Crafting.playerCraft.containsKey(player.getName())) {
								ItemStack item = null;
								
								
								for(ItemStack all : InventoryManager.itemResult.keySet()) {
									if(ChatColor.stripColor(all.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()))) {
										item = all;
									}
								}

								if (item != null) {
									if (InventoryManager.itemPermission.containsKey(item)) {
										if (!Crafting.hasResearched(player.getName(), InventoryManager.itemPermission.get(item))) {
											return;
										}
									}

									boolean hasItems = true;
									for (ItemStack all : InventoryManager.itemNeeded.get(item)) {
										if (ItemUtils.containsItem(player, all.clone(), all.clone().getAmount()))
											continue;
										hasItems = false;
										player.sendMessage(ChatColor.RED + "You don't have all the needed items.");
										break;
									}

									boolean workbench = true;

									if (InventoryManager.itemWorkbench.containsKey(item)) {
										workbench = !((Boolean) InventoryManager.itemWorkbench.get(item)).booleanValue();

										if (!workbench) {
											for (int x = -2; x < 3; x++) {
												for (int y = -2; y < 3; y++) {
													for (z = -2; z < 3; z++) {
														if (player.getLocation().getBlock().getRelative(x, y, z).getType() != Material.STONE_SLAB2)
															continue;
														workbench = true;
													}
												}

											}

											if (!workbench) {
												// must be near a workbench
											}
										}
									}

									if ((hasItems) && (workbench)) {
										Crafting.playerCraft.put(player.getName(), item);
										Crafting.settingAmount.add(player.getName());
										StringUtils.sendTitle(player, 5, 40, 5, ChatColor.GREEN + "How many you want to craft?", ChatColor.GRAY + "(type it in chat)");

										player.closeInventory();
									}
								}
							} else {
								StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "Already crafting!", null);
							}
						}
					} else if (event.getClickedInventory().equals(player.getInventory())) {
						if (Crafting.selecting.contains(player.getName())) {
							//player.getItemInHand().setAmount(player.getItemInHand().getAmount() + 65);

							if (!event.getCurrentItem().equals(player.getItemInHand())) {
								ItemStack newItem = event.getCurrentItem().clone();
								newItem.setAmount(1);

								String perm = null;
								ItemStack item = null;

								for (ItemStack items : InventoryManager.itemResult.keySet()) {
									if (((ItemStack) InventoryManager.itemResult.get(items)).getType() != newItem.getType())
										continue;
									if ((newItem.getItemMeta().getDisplayName() != null) && (!ChatColor.stripColor(newItem.getItemMeta().getDisplayName()).contains(ChatColor.stripColor(((ItemStack) InventoryManager.itemResult.get(items)).getItemMeta().getDisplayName()))))
										continue;
									if (InventoryManager.itemPermission.get(items) == null)
										continue;
									perm = (String) InventoryManager.itemPermission.get(items);
									item = items;
								}

								if (perm != null) {
									if (!Crafting.hasResearched(player.getName(), perm)) {
										Integer amount = null;
										//player.setItemInHand(null);

										Crafting.addResearch(player.getName(), perm);

										event.setCancelled(true);
										Crafting.selecting.remove(player.getName());
										player.closeInventory();
										StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Reseached " + ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName().toLowerCase().replace("blueprint", "")) + "!");
										amount = player.getItemInHand().getAmount();
										
										ItemUtils.removeItem(player, com.swingsword.ssengine.utils.ItemUtils.itemStackFromString("i=351;d=12;n=&fResearch &fKit &f1"), 1);
										
										/*player.getInventory().remove(player.getItemInHand());
										if (amount != 1) {
											player.getInventory().addItem(com.swingsword.ssengine.utils.ItemUtils.itemStackFromString("i=351;d=12;n=&fResearch &fKit &f1;a=" + (amount - 1)));
										}*/
									} else {
										event.setCancelled(true);
										Crafting.selecting.remove(player.getName());
										player.closeInventory();
										StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ERROR", ChatColor.GRAY + "Already researched!");
									}
									return;
								} else {
									event.setCancelled(true);
									Crafting.selecting.remove(player.getName());
									player.closeInventory();
									StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ERROR", ChatColor.GRAY + "Invalid item!");
								}
							} else {
								event.setCancelled(true);
								Crafting.selecting.remove(player.getName());
								player.closeInventory();
								StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ERROR", ChatColor.GRAY + "Cant research the kit your using!");
							}

							/*if (removeItem) {
								ItemUtils.removeItem(player, player.getItemInHand(), player.getItemInHand().getAmount());
							} else {
								player.getItemInHand().setAmount(1);
							}*/

						}

					} else if (event.getClickedInventory().getType() == InventoryType.CRAFTING) {
						event.setCancelled(true);

						if (!player.hasPotionEffect(PotionEffectType.SLOW)) {
							player.openInventory(InventoryManager.craftInv);
						} else
							player.sendMessage(ChatColor.RED + "You can't craft right now.");
					}
				}
			}
			
			if(!event.isCancelled()) {
				if(player.getItemInHand().getTypeId() == 351) {
					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + "Sorry, you can't edit inventories while holding this item.");
				}
			}
		}
	}

	@EventHandler
	public void onPlayerDropItemEvent(final PlayerDropItemEvent event) {
		if (Crafting.settingAmount.contains(event.getPlayer().getName())) {
			event.setCancelled(true);

			event.getPlayer().updateInventory();

			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					if (event.getPlayer().isOnline()) {
						event.getPlayer().updateInventory();
					}
				}
			}, 1);
		}
	}

	@EventHandler
	public void onInventorDrag(final InventoryDragEvent event) {
		final Player player = (Player) event.getWhoClicked();

		if (event.getInventory().equals(InventoryManager.craftInv) || player.getOpenInventory().getTopInventory().getTitle().contains("Crafting") || Crafting.settingAmount.contains(player.getName())) {
			event.setCancelled(true);

			player.updateInventory();

			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					if (player.isOnline()) {
						player.updateInventory();
					}
				}
			}, 1);
		}
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();

		if (Crafting.settingAmount.contains(player.getName())) {
			event.setCancelled(true);

			if (MathUtils.isInteger(event.getMessage())) {
				if (Integer.parseInt(event.getMessage()) > 0) {
					if (InventoryManager.itemResult.get(Crafting.playerCraft.get(player.getName())).getItemMeta() != null && InventoryManager.itemResult.get(Crafting.playerCraft.get(player.getName())).getItemMeta().getDisplayName() != null) {
						player.sendMessage(ChatColor.GREEN + "Crafting " + event.getMessage() + " " + InventoryManager.itemResult.get(Crafting.playerCraft.get(player.getName())).getItemMeta().getDisplayName() + ChatColor.GREEN + "...");
					} else {
						player.sendMessage(ChatColor.GREEN + "Crafting " + event.getMessage() + " " + InventoryManager.itemResult.get(Crafting.playerCraft.get(player.getName())).getType().name().toLowerCase() + "...");
					}
					player.sendMessage(ChatColor.GRAY + "You can chat '/cancel' to stop crafting.");

					Crafting.playerCraftAmount.put(player.getName(), Integer.parseInt(event.getMessage()));

					for (ItemStack all : InventoryManager.itemNeeded.get(Crafting.playerCraft.get(player.getName()))) {
						ItemUtils.removeItem(player, all.clone(), all.clone().getAmount());
					}
					player.setExp(1);
					if (player.hasPermission("ss.rust.halfcraft")) {
						Crafting.progressCraft(player, (InventoryManager.itemDelay.get(InventoryManager.itemResult.get(Crafting.playerCraft.get(player.getName()))) * 20) / 2 - 1, InventoryManager.itemDelay.get(InventoryManager.itemResult.get(Crafting.playerCraft.get(player.getName()))) * 10);
						player.sendMessage(ChatColor.GREEN + "You are crafting these items 2x as fast than usual because of your donation!");
					} else {
						Crafting.progressCraft(player, InventoryManager.itemDelay.get(InventoryManager.itemResult.get(Crafting.playerCraft.get(player.getName()))) * 20 - 1, InventoryManager.itemDelay.get(InventoryManager.itemResult.get(Crafting.playerCraft.get(player.getName()))) * 20);
					}
					Crafting.playerCraftAmount.put(player.getName(), Crafting.playerCraftAmount.get(player.getName()) - 1);

				} else {
					player.sendMessage(ChatColor.RED + "That is an invalid number. Crafting canceled.");
					Crafting.playerCraft.remove(player.getName());
				}
			} else {
				player.sendMessage(ChatColor.RED + "That is not a number. Crafting canceled.");
				Crafting.playerCraft.remove(player.getName());
			}

			Crafting.settingAmount.remove(player.getName());
		}
	}

}
