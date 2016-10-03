package com.swingsword.ssengine.game.games.rust.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.material.Door;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.utils.DoorUtils;
import com.swingsword.ssengine.game.games.rust.utils.LocationUtils;
import com.swingsword.ssengine.utils.ConfigUtils;

public class DoorListener implements Listener {
	
	@SuppressWarnings({ "deprecation" })
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent evt) {
		Player player = evt.getPlayer();
		
		if(evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = evt.getClickedBlock();
			FileConfiguration doors = ConfigUtils.getConfig("data/doors");
			
			if (block.getType() == Material.IRON_DOOR_BLOCK) {
				if (block.getData() >= 8) {
					block = block.getRelative(BlockFace.DOWN);
				}
		
				if(!doors.getConfigurationSection("doors").contains(LocationUtils.RealLocationToString(block.getLocation())) || (doors.getString("doors." + LocationUtils.RealLocationToString(block.getLocation()) + ".owner").equals(player.getUniqueId().toString()) || doors.getStringList("doors." + LocationUtils.RealLocationToString(block.getLocation()) + ".people").contains(player.getUniqueId().toString()))) {
					if (block.getType() == Material.IRON_DOOR_BLOCK) {
						if (block.getData() < 4) {
							block.setData((byte) (block.getData() + 4));
							block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
						} else {
							block.setData((byte) (block.getData() - 4));
							block.getWorld().playEffect(block.getLocation(), Effect.DOOR_TOGGLE, 0);
						}
					}
					
				} else {
					player.sendMessage(ChatColor.RED + "You did not unlock that door. You can unlock a door using '/entercode <4 numbers>'.");
					evt.setCancelled(true);
				}
				
			} else if(block.getType() == Material.WOODEN_DOOR) {
				if(block.getData() == (byte) 8) {
					if(!doors.getConfigurationSection("doors").contains(LocationUtils.RealLocationToString(block.getLocation().add(0, -1, 0))) || (doors.getString("doors." + LocationUtils.RealLocationToString(block.getLocation().add(0, -1, 0)) + ".owner").equals(player.getUniqueId().toString()) || doors.getStringList("doors." + LocationUtils.RealLocationToString(block.getLocation().add(0, -1, 0)) + ".people").contains(player.getUniqueId().toString()))) {
						evt.setCancelled(false);
					} else {			
						player.sendMessage(ChatColor.RED + "You did not unlock that door. You can unlock a door using '/entercode <4 numbers>'.");
						evt.setCancelled(true);
					}
					
				} else {
					if(!doors.getConfigurationSection("doors").contains(LocationUtils.RealLocationToString(block.getLocation())) || (doors.getString("doors." + LocationUtils.RealLocationToString(block.getLocation()) + ".owner").equals(player.getUniqueId().toString()) || doors.getStringList("doors." + LocationUtils.RealLocationToString(block.getLocation()) + ".people").contains(player.getUniqueId().toString()))) {
						evt.setCancelled(false);
					} else {			
						player.sendMessage(ChatColor.RED + "You did not unlock that door. You can unlock a door using '/entercode <4 numbers>'.");
						evt.setCancelled(true);
					}
				}
				
			} else if(Rust.placeLoc.containsKey(evt.getPlayer().getName()) && player.getItemInHand().getType().name().contains("DOOR")) {
				if(DoorUtils.doorPreview.containsKey(player.getName())) {
					for(Location loc : DoorUtils.doorPreview.get(player.getName())) {
						player.sendBlockChange(loc, loc.getBlock().getType(), loc.getBlock().getData());
					}
					DoorUtils.doorPreview.remove(player.getName());
				}
				
				if(evt.getClickedBlock().getLocation().equals(Rust.placeLoc.get(player.getName()).toLocation()) && evt.getClickedBlock().getLocation().clone().add(0, 1, 0).getBlock().getType() == Material.AIR) {
					if(evt.getPlayer().getItemInHand().getType() == Material.IRON_DOOR || evt.getPlayer().getItemInHand().getType() == Material.WOODEN_DOOR || evt.getPlayer().getItemInHand().getType() == Material.WOOD_DOOR) {
						evt.getClickedBlock().getLocation().getWorld().playSound(evt.getClickedBlock().getLocation().clone().add(0, 1, 0), Sound.BLOCK_ANVIL_USE, 1, 0);
						player.sendMessage(ChatColor.GREEN + "Door created. Set the code with '/setcode <4 numbers>'.");
						
						doors.set("doors." + LocationUtils.RealLocationToString(evt.getClickedBlock().getLocation().clone().add(0, 1, 0)) + ".owner", player.getUniqueId().toString());
						doors.set("doors." + LocationUtils.RealLocationToString(evt.getClickedBlock().getLocation().clone().add(0, 1, 0)) + ".code", 10000);
						List<String> people = new ArrayList<String>();
						doors.set("doors." + LocationUtils.RealLocationToString(evt.getClickedBlock().getLocation().clone().add(0, 1, 0)) + ".people", people);
						ConfigUtils.saveConfig(doors, "data/doors");
						
					} else if(player.getItemInHand().getType() == Material.IRON_FENCE) {
						Rust.placeLoc.get(player.getName()).toLocation().add(0, 1, 0).getBlock().setType(Material.IRON_FENCE);
						evt.getClickedBlock().getLocation().getWorld().playSound(evt.getClickedBlock().getLocation().clone().add(0, 1, 0), Sound.BLOCK_ANVIL_USE, 1, 0);
						
						if(player.getItemInHand().getAmount() - 1 > 1) {
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
						} else {
							player.setItemInHand(null);
						}
					}
				} else {
					Rust.placeLoc.remove(player.getName());
					evt.setCancelled(true);
				}
			}
		}
		
		if(evt.getAction().equals(Action.LEFT_CLICK_BLOCK) && evt.getClickedBlock() != null) {
			if((evt.getClickedBlock().getType() == Material.IRON_DOOR_BLOCK || evt.getClickedBlock().getType() == Material.WOODEN_DOOR || evt.getClickedBlock().getType() == Material.WOOD_DOOR) && player.getLocation().distance(evt.getClickedBlock().getLocation()) <= 1.5) {
				if(!((Door) evt.getClickedBlock().getState().getData()).isOpen()) {
					Location loc = player.getLocation().getBlock().getLocation().clone().add(0.5, 0, 0.5);
					loc.setYaw(player.getLocation().getYaw() + 180);
					player.teleport(loc);
					
					if(player.isOnGround()) {
						player.setVelocity(new Vector(player.getLocation().getDirection().getX() * 0.5, 0.1, player.getLocation().getDirection().getBlockZ() * 0.5));
					} else {
						player.setVelocity(new Vector(player.getLocation().getDirection().getX() * 0.1, 0, player.getLocation().getDirection().getBlockZ() * 0.1));
					}
					
					player.sendMessage(ChatColor.RED + "Back up to break this block, this is to prevent block glitching.");
				}
			}
		}
	}
	
	@SuppressWarnings("static-access")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		
		if(Rust.getInstance().placeLoc.containsKey(player.getName())) {
			Rust.getInstance().placeLoc.remove(player.getName());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		
		if(player.getItemInHand() != null) {
			if(player.getItemInHand().getType() == Material.IRON_DOOR || player.getItemInHand().getType() == Material.WOOD_DOOR || player.getItemInHand().getType() == Material.IRON_FENCE) {		
				if(!Rust.placeLoc.containsKey(player.getName())) {
					event.setCancelled(true);
					
					player.sendMessage(ChatColor.RED + "Invalid placement.");
					
				} else {
					if(player.getItemInHand().getType() == Material.IRON_FENCE) {
						event.setCancelled(true);
						
						Rust.placeLoc.get(player.getName()).getBlock().getRelative(0, 1, 0).setType(Material.IRON_FENCE);
						
						if(player.getItemInHand().getAmount() > 1) {
							player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
						} else {
							player.setItemInHand(null);
						}
						player.updateInventory();
					}
				}
			}
		}
	}
}
