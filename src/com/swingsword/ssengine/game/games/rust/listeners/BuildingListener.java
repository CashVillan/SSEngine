package com.swingsword.ssengine.game.games.rust.listeners;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.building.Building;
import com.swingsword.ssengine.game.games.rust.utils.BuildingUtils;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.game.games.rust.utils.SimpleLocation;
import com.swingsword.ssengine.game.games.rust.utils.SpawnUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class BuildingListener implements Listener {
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		final Player player = event.getPlayer();
		
		if(Rust.placeLoc.containsKey(player.getName()) && !player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 6).equals(Rust.placeLoc.get(player.getName()))) {
			Rust.placeLoc.remove(player.getName());
		}
		
		//Preview
		
		BuildingUtils.updatePreview(player, player.getItemInHand(), false);
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(player.isOnline()) {
					BuildingUtils.updatePreview(player, player.getItemInHand(), false);
				}
			}
		}, 11);
		
		if(player.getLocation().clone().add(0, -0.5, 0).getBlock().getType() == Material.AIR && Building.playerBlocks.containsKey(player.getName()) && Building.playerBlocks.get(player.getName()).contains(new SimpleLocation(player.getLocation().clone().add(0, -0.5, 0).getBlock().getLocation()).toString())) {
			player.teleport(player.getLocation().clone().add(0, -0.5, 0));
		}
	}
	
	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();

		BuildingUtils.updatePreview(player, player.getInventory().getItem(event.getNewSlot()), true);
	}
	
	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event) {
		event.setCancelled(true);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGH)
	public void onPlayerInteract(PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if(event.getClickedBlock().getType() == Material.CARPET && event.getClickedBlock().getData() == 13) {
				boolean isHis = false;
				
				for(int x = -1; x <= 1; x++) {
					for(int z = -1; z <= 1; z++) {
						Location loc = event.getClickedBlock().getLocation().clone().add(x, 0, z);
						
						for(String all : SpawnUtils.getHomes(player)) {
							if(LocationUtils.RealLocationFromString(all).equals(loc)) {
								isHis = true;
								break;
							}
						}
						
						if(isHis) {
							SpawnUtils.removeHome(player, event.getClickedBlock().getLocation());
							
							ItemStack bag = new ItemStack(Material.NETHER_BRICK_ITEM, 1);
							ItemMeta bagm = bag.getItemMeta();
							bagm.setDisplayName(ChatColor.WHITE + "Sleeping Bag");
							bag.setItemMeta(bagm);
							
							player.getInventory().addItem(bag);
							return;
						}
					}
				}
				
				if(!isHis) {
					StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Not yours!");
				}
			}
			
			Block block = event.getClickedBlock();
			
			if(Rust.placeLoc.containsKey(event.getPlayer().getName())) {
				if(Rust.firePreview.containsKey(player.getName())) {
					if(player.getItemInHand().getType() != Material.getMaterial(405)) {
						for(SimpleLocation loc : Rust.firePreview.get(player.getName())) {
							player.sendBlockChange(loc.toLocation(), loc.getBlock().getType(), loc.getBlock().getData());
						}
						
						Rust.firePreview.remove(player);
						
					} else {
						if (SpawnUtils.getHomes(player).size() == 0 || player.hasPermission("ss.rust.halfcraft")) {
							for (SimpleLocation loc : Rust.firePreview.get(player.getName())) {
								if (LocationUtils.isLocationNearBlock(loc.toLocation().add(0, 1, 0), Material.IRON_DOOR, 2)) {
									StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ERROR", ChatColor.GRAY + "Invalid Placement!");
									event.setCancelled(true);
									return;
								}
							}

							for (SimpleLocation loc : Rust.firePreview.get(player.getName())) {
								if (loc.toLocation().add(0, 1, 0).getBlock().getType() != Material.AIR) {
									StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ERROR", ChatColor.GRAY + "Invalid Placement!");
									event.setCancelled(true);
									return;
								}
							}

							for (SimpleLocation loc : Rust.firePreview.get(player.getName())) {
								player.sendBlockChange(loc.toLocation(), loc.getBlock().getType(), loc.getBlock().getData());
								loc.toLocation().add(0, 1, 0).getBlock().setTypeIdAndData(171, (byte) 13, true);
							}

							Rust.firePreview.remove(player);

							block.getLocation().getWorld().playSound(block.getLocation().clone().add(0, 1, 0), Sound.BLOCK_ANVIL_USE, 1, 0);

							StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Home Added!");
							SpawnUtils.addHome(player, block.getLocation().clone().add(0, 1, 0));

							if (player.getItemInHand().getAmount() > 1) {
								player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
							} else {
								player.setItemInHand(null);
							}
						} else {
							StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ERROR", ChatColor.GRAY + "Buy a rank to set more than 1 homes!");
						}
					}
				}
				
				if(!block.getLocation().equals(Rust.placeLoc.get(player.getName()).toLocation())) {
					Rust.placeLoc.remove(player.getName());
					event.setCancelled(true);
				}
			}
			
			if(Building.holdingBuildItem(player)) {
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				event.setCancelled(true);
				event.setUseInteractedBlock(Result.DENY);
				event.setUseItemInHand(Result.DENY);
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						if(player.isOnline()) {
							player.closeInventory();
						}
					}
				}, 1);
				
			} else {
				if(event.getClickedBlock().getType() == Material.CHEST || event.getClickedBlock().getType() == Material.ANVIL || event.getClickedBlock().getType() == Material.STEP) {
					if(player.getLocation().distance(event.getClickedBlock().getLocation().clone().add(0.5, 0.5, 0.5)) > 1.5) {
						event.setCancelled(true);
						event.setUseInteractedBlock(Result.DENY);
						event.setUseItemInHand(Result.DENY);
						
						StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "Not close enough to search!");
						
						Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
							public void run() {
								if(player.isOnline()) {
									player.closeInventory();
								}
							}
						}, 1);
					}
				}
			}
			
			if(Building.playerBuildLoc.containsKey(player.getName())) {
				event.setCancelled(true);
				
				if(!new SimpleLocation(player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 6).getLocation()).toString().equals(Building.playerBuildLoc.get(player.getName())) && (!com.swingsword.ssengine.utils.ItemUtils.getDisplayName(player.getItemInHand()).contains("Foundation") && !com.swingsword.ssengine.utils.ItemUtils.getDisplayName(player.getItemInHand()).contains("Ceiling"))) {				
					Building.playerBuildLoc.remove(player.getName());
					
				} else {
					if(player.getItemInHand().getType() == Material.getMaterial(264) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Foundation".replace("&", ChatColor.COLOR_CHAR + ""))) {
						Building.fm.createFoundation(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), Material.WOOD);
						event.setCancelled(true);	
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(266) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Pillar".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "Pillar");
						event.setCancelled(true);
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(281) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Wall".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "Wall");
						event.setCancelled(true);
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(287) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Doorway".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "Doorway");
						event.setCancelled(true);
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(288) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Window".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "Window");
						event.setCancelled(true);
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(377) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Stairs".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "Stair");
						event.setCancelled(true);
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(296) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fWood_Ceiling".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "Ceiling");
						event.setCancelled(true);
						
					
					} else if(player.getItemInHand().getType() == Material.getMaterial(336) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Foundation".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.fm.createFoundation(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), Material.IRON_BLOCK);
						event.setCancelled(true);
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(338) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Pillar".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "MetalPillar");
						event.setCancelled(true);
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(353) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Wall".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "MetalWall");
						event.setCancelled(true);
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(371) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Doorway".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "MetalDoorway");
						event.setCancelled(true);
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(372) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Window".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "MetalWindow");
						event.setCancelled(true);
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(378) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Stairs".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "MetalStair");
						event.setCancelled(true);
						
					} else if(player.getItemInHand().getType() == Material.getMaterial(388) && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().replace(" ", "_").equals("&fMetal_Ceiling".replace("&", ChatColor.COLOR_CHAR + ""))) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "MetalCeiling");
						event.setCancelled(true);
						
						//Chest
					} else if(player.getItemInHand().getType() == Material.CHEST && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Large Wood Storage Box")) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "Chest1");	
						event.setCancelled(true);
					
					} else if(player.getItemInHand().getType() == Material.CHEST && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null && player.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.WHITE + "Wood Storage Box")) {
						event.setCancelled(true);	
						Building.pm.createProp(player, LocationUtils.RealLocationFromString(Building.playerBuildLoc.get(player.getName())), "Chest2");	
						event.setCancelled(true);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		if(event.getBlock().getType() == Material.BED_BLOCK && Rust.placeLoc.containsKey(player.getName())) {
			event.getBlock().getLocation().getWorld().playSound(event.getBlock().getLocation().clone().add(0, 1, 0), Sound.BLOCK_ANVIL_USE, 1, 0);
			
			SpawnUtils.addHome(player, event.getBlock().getLocation().clone().add(0, 1, 0));
			
			if(player.getItemInHand().getAmount() > 1) {
				player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
			} else {
				player.setItemInHand(null);
			}
			
		} else if(Rust.firePreview.containsKey(player)) {
			event.setCancelled(true);
		}
		
		if(Building.holdingBuildItem(player)) {
    		event.setCancelled(true);
    	}
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		Building.playerBuildLoc.remove(player.getName());
		Building.oldLocation.remove(player.getName());
		Building.playerBlocks.remove(player.getName());
	}
}
