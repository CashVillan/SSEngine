package com.swingsword.ssengine.game.games.rust.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.utils.CookingUtils;
import com.swingsword.ssengine.game.games.rust.utils.ItemUtils;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.game.games.rust.utils.PlayerUtils;
import com.swingsword.ssengine.game.games.rust.utils.SimpleLocation;
import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class CookingListener implements Listener {

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		final Player player = (Player) event.getWhoClicked();

		if (event.getInventory().getName().contains("Furnace") || event.getInventory().getName().contains("Campfire") && event.getCurrentItem() != null) {
			if (!Rust.plugin.clickDelay.contains(player.getName())) {
				Rust.plugin.clickDelay.add(player.getName());

				Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						if (player.isOnline()) {
							Rust.plugin.clickDelay.remove(player.getName());
						}
					}
				}, 5);

				if (event.getCurrentItem() != null) {
					ItemStack item = event.getCurrentItem().clone();

					if (event.getSlot() == 13 && event.getInventory().getName().contains("Furnace")) {
						if (event.getCurrentItem().getData().getData() == 6) {
							if (CookingUtils.getFurnaceInvLoc(event.getInventory()) != null) {
								if (CookingUtils.containsFurnaceFuel(event.getInventory())) {
									ItemStack toggle = new ItemStack(Material.INK_SACK);
									toggle.setDurability((short) 13);
									ItemMeta togglem = toggle.getItemMeta();
									togglem.setDisplayName(ChatColor.GREEN + "On");
									toggle.setItemMeta(togglem);

									event.getInventory().setItem(event.getSlot(), toggle);

									CookingUtils.getFurnaceInvLoc(event.getInventory()).getBlock().setData((byte) (CookingUtils.getFurnaceInvLoc(event.getInventory()).getBlock().getData() + 4));

								} else {
									StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Needs Fuel!");
								}
							}
							event.setResult(Result.DENY);
							event.setCancelled(true);

						} else if (event.getCurrentItem().getData().getData() == 13) {
							if (CookingUtils.getFurnaceInvLoc(event.getInventory()) != null) {
								ItemStack toggle = new ItemStack(Material.INK_SACK);
								toggle.setDurability((short) 6);
								ItemMeta togglem = toggle.getItemMeta();
								togglem.setDisplayName(ChatColor.RED + "Off");
								toggle.setItemMeta(togglem);

								event.getInventory().setItem(event.getSlot(), toggle);

								CookingUtils.getFurnaceInvLoc(event.getInventory()).getBlock().setData((byte) (CookingUtils.getFurnaceInvLoc(event.getInventory()).getBlock().getData() - 4));
							}
							event.setResult(Result.DENY);
							event.setCancelled(true);

						}

					} else if (event.getSlot() == 13 && event.getInventory().getName().contains("Campfire")) {
						if (event.getCurrentItem().getData().getData() == 6) {
							if (CookingUtils.getFireInvLoc(event.getInventory()) != null) {
								if (CookingUtils.containsFireFuel(event.getInventory())) {
									ItemStack toggle = new ItemStack(Material.INK_SACK);
									toggle.setDurability((short) 13);
									ItemMeta togglem = toggle.getItemMeta();
									togglem.setDisplayName(ChatColor.GREEN + "On");
									toggle.setItemMeta(togglem);

									event.getInventory().setItem(event.getSlot(), toggle);

									// getFireInvLoc(event.getInventory()).getBlock().setType(Material.FIRE);

								} else {
									StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Needs Fuel!");
								}
							}
							event.setResult(Result.DENY);
							event.setCancelled(true);

						} else if (event.getCurrentItem().getData().getData() == 13) {
							if (CookingUtils.getFireInvLoc(event.getInventory()) != null) {
								ItemStack toggle = new ItemStack(Material.INK_SACK);
								toggle.setDurability((short) 6);
								ItemMeta togglem = toggle.getItemMeta();
								togglem.setDisplayName(ChatColor.RED + "Off");
								toggle.setItemMeta(togglem);

								event.getInventory().setItem(event.getSlot(), toggle);

								// getFireInvLoc(event.getInventory()).getBlock().setType(Material.STEP);
								// getFireInvLoc(event.getInventory()).getBlock().setData((byte)
								// 0);
							}
							event.setResult(Result.DENY);
							event.setCancelled(true);
						}
					}

					if (event.getRawSlot() > 26) {
						int slot = CookingUtils.findSlot(player, event.getInventory(), event.getCurrentItem(), event.getInventory().getName());

						if (slot != -1) {
							ItemUtils.removeItem(player.getInventory(), event.getCurrentItem());
							event.getInventory().setItem(slot, item);
						}

					} else {
						if (CookingUtils.canMove(event.getInventory(), event.getSlot())) {
							ItemUtils.removeItem(event.getInventory(), event.getCurrentItem());

							if (player.getInventory().firstEmpty() != -1) {
								player.getInventory().addItem(item);
							} else {
								player.getWorld().dropItemNaturally(player.getLocation().add(0, 1, 0), item);
							}
						}
					}
				}

				event.setResult(Result.DENY);
				event.setCancelled(true);

			} else {
				event.setResult(Result.DENY);
				event.setCancelled(true);
			}

			if (event.isShiftClick()) {
				event.setResult(Result.DENY);
				event.setCancelled(true);
			}
		}
	}

	@SuppressWarnings({ "deprecation", "static-access" })
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract2(final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (CookingUtils.furnaceInventory.containsKey(new SimpleLocation(event.getClickedBlock().getLocation()).toString())) {
				if (CookingUtils.furnaceInventory.get(new SimpleLocation(event.getClickedBlock().getLocation()).toString()).getItem(13).getData().getData() == 6 && event.getClickedBlock().getData() > 3) {
					event.getClickedBlock().setData((byte) (event.getClickedBlock().getData() - 4));
				}
			}
		}

		if (player.getItemInHand() != null && player.getItemInHand().getType().equals(Material.CLAY_BALL)) {
			player.getItemInHand().setType(Material.WOOD_AXE);
			player.updateInventory();
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();

			if (Rust.plugin.placeLoc.containsKey(event.getPlayer().getName())) {
				/*
				 * if(firePreview.containsKey(player)) { for(Location loc :
				 * firePreview.get(player)) { player.sendBlockChange(loc,
				 * loc.getBlock().getType(), loc.getBlock().getData()); }
				 * firePreview.remove(player); }
				 */

				if (block.getLocation().equals(Rust.plugin.placeLoc.get(player.getName()).toLocation())) {
					if (!CookingUtils.fireInventory.containsKey(new SimpleLocation(block.getLocation().add(0, 1, 0)).toString()) && !CookingUtils.furnaceInventory.containsKey(new SimpleLocation(block.getLocation().add(0, 1, 0)).toString())) {
						if (player.getItemInHand().getType() == Material.STEP) {
							block.getLocation().clone().add(0, 1, 0).getBlock().setTypeIdAndData(Material.STEP.getId(), (byte) 3, true);

							if (player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							} else {
								player.setItemInHand(null);
							}

							Inventory campInv = CookingUtils.getCampfireInv();
							CookingUtils.fireInventory.put(new SimpleLocation(block.getLocation().add(0, 1, 0)).toString(), campInv);
							CookingUtils.invDelay.put(campInv, 1440);

							FileConfiguration config = ConfigUtils.getConfig("data/cookLocs");
							config.set("fires." + LocationUtils.RealLocationToString(block.getLocation()), 1440);
							ConfigUtils.saveConfig(config, "data/cookLocs");

							event.getClickedBlock().getLocation().getWorld().playSound(event.getClickedBlock().getLocation().clone().add(0, 1, 0), Sound.BLOCK_ANVIL_USE, 1, 0);

							event.setCancelled(true);

						} else if (player.getItemInHand().getType() == Material.ANVIL) {
							if (event.getClickedBlock().getType() != Material.ANVIL) {
								if (PlayerUtils.getPlayerDirection(player).equalsIgnoreCase("s")) {
									block.getLocation().clone().add(0, 1, 0).getBlock().setTypeIdAndData(Material.ANVIL.getId(), (byte) 0, false);
								} else if (PlayerUtils.getPlayerDirection(player).equalsIgnoreCase("n")) {
									block.getLocation().clone().add(0, 1, 0).getBlock().setTypeIdAndData(Material.ANVIL.getId(), (byte) 2, false);
								} else if (PlayerUtils.getPlayerDirection(player).equalsIgnoreCase("e")) {
									block.getLocation().clone().add(0, 1, 0).getBlock().setTypeIdAndData(Material.ANVIL.getId(), (byte) 3, false);
								} else {
									block.getLocation().clone().add(0, 1, 0).getBlock().setTypeIdAndData(Material.ANVIL.getId(), (byte) 1, false);
								}

								if (player.getItemInHand().getAmount() > 1) {
									player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
								} else {
									player.setItemInHand(null);
								}

								Inventory furnaceInv = CookingUtils.getFurnaceInv();
								CookingUtils.furnaceInventory.put(new SimpleLocation(block.getLocation().add(0, 1, 0)).toString(), furnaceInv);
								CookingUtils.invDelay.put(furnaceInv, 1440);

								FileConfiguration config = ConfigUtils.getConfig("data/cookLocs");
								config.set("furnaces." + LocationUtils.RealLocationToString(block.getLocation()), 1440);
								ConfigUtils.saveConfig(config, "data/cookLocs");

								event.getClickedBlock().getLocation().getWorld().playSound(event.getClickedBlock().getLocation().clone().add(0, 1, 0), Sound.BLOCK_ANVIL_USE, 1, 0);

								event.setCancelled(true);

							} else {
								StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ERROR", ChatColor.GRAY + "Invalid Placement!");
							}

						} else if (player.getItemInHand().getType() == Material.STONE_SLAB2) {
							if (event.getClickedBlock().getType() != Material.STONE_SLAB2) {
								block.getLocation().clone().add(0, 1, 0).getBlock().setTypeIdAndData(Material.STONE_SLAB2.getId(), (byte) 0, true);

								if (player.getItemInHand().getAmount() > 1) {
									player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
								} else {
									player.setItemInHand(null);
								}

								event.getClickedBlock().getLocation().getWorld().playSound(event.getClickedBlock().getLocation().clone().add(0, 1, 0), Sound.BLOCK_ANVIL_USE, 1, 0);

								event.setCancelled(true);

							} else {
								StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ERROR", ChatColor.GRAY + "Invalid Placement!");
							}
						}
					}

				} else {
					Rust.plugin.placeLoc.remove(player.getName());
					event.setCancelled(true);
				}
			}
		}
	}

	@SuppressWarnings("static-access")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		if (player.getItemInHand() != null) {
			if (player.getItemInHand().getType() == Material.STEP && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Camp Fire") || player.getItemInHand().getType() == Material.ANVIL || player.getItemInHand().getType() == Material.STONE_SLAB2) {
				event.setCancelled(true);

				if (!Rust.plugin.placeLoc.containsKey(player.getName())) {
					StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ERROR", ChatColor.GRAY + "Invalid Placement!");
				}
			}
		}
	}
	
	@EventHandler
	public void onInventorDrag(InventoryDragEvent event) {
		if (event.getInventory().getTitle().contains("Furnace") || event.getInventory().getTitle().contains("Campfire")) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE) {
			if (CookingUtils.fireInventory.containsKey(new SimpleLocation(event.getBlock().getLocation()).toString())) {
				CookingUtils.fireInventory.remove(new SimpleLocation(event.getBlock().getLocation()).toString());
			}
			if (CookingUtils.furnaceInventory.containsKey(new SimpleLocation(event.getBlock().getLocation()).toString())) {
				CookingUtils.furnaceInventory.remove(new SimpleLocation(event.getBlock().getLocation()).toString());
			}
		}
	}
}
