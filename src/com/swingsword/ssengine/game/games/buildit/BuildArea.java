package com.swingsword.ssengine.game.games.buildit;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.threads.TimerHandler;
import com.swingsword.ssengine.utils.ItemUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class BuildArea {

	public static ArrayList<BuildArea> areas = new ArrayList<BuildArea>();
	public static BuildArea buildAreaInReview = null;
	
	public static int height = 64;
	public static int width = 25;
	
	public static BuildArea getArea(Player owner) {
		for(BuildArea all : areas) {
			if(all.owner.equals(owner)) {
				return all;
			}
		}
		return null;
	}
	
	//
	
	public Player owner;
	public int id;
	public CuboidRegion region;
	
	public int rating = 0;
	
	public BuildArea(Player owner, int id) {
		this.owner = owner;
		this.id = id;
		
		areas.add(this);
		
		int x = 50 + (width + 5) * id;
				
		region = new CuboidRegion(new Vector(x, 5, 0), new Vector(x + width, height + 5, width));
		
		clearArea();
		teleportPlayerToArea(owner);
	}
	
	public void teleportPlayerToArea(Player player) {
		double x = new Random().nextInt(region.getMaximumPoint().getBlockX() - region.getMinimumPoint().getBlockX()) + region.getMinimumPoint().getBlockX() + 0.5;
		double z = new Random().nextInt(region.getMaximumPoint().getBlockZ() - region.getMinimumPoint().getBlockZ()) + region.getMinimumPoint().getBlockZ() + 0.5;
		double y = Bukkit.getWorld("map").getHighestBlockYAt((int) x, (int) z);
		
		Location loc = new Location(Bukkit.getWorld("map"), x, y, z);
		
		if(!region.contains(BukkitUtil.toVector(loc))) {
			teleportPlayerToArea(player);
			
			return;
		}
		
		player.teleport(new Location(Bukkit.getWorld("map"), x, y, z));
	}
	
	public void clearArea() {
		for(BlockVector block : region) {
			Block bukkitBlock = Bukkit.getWorld("map").getBlockAt(block.getBlockX(), block.getBlockY(), block.getBlockZ());
			
			if(block.getBlockX() < region.getMaximumPoint().getBlockX() && block.getBlockZ() < region.getMaximumPoint().getBlockZ()) {
				bukkitBlock.setType(Material.AIR);
			}
		}
	}
	
	public void review() {
		if(owner.isOnline()) {
			Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Lets review " + owner.getName() + "'s build!");
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				teleportPlayerToArea(all);
				
				for(ItemStack item : all.getInventory().getContents()) {
					if(item != null) {
						item.setType(Material.AIR);
					}
				}
				all.getInventory().clear();
				all.updateInventory();
			}
			
			buildAreaInReview = this;
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				StringUtils.sendTitle(all, 5, 20, 5, ChatColor.GREEN + "Built by: " + owner.getName(), "");
			}
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(all != owner) {
					equipReview(all);
				}
			}
			
			TimerHandler.createTimer("reviewWait", 20, 20, new Runnable() {
				public void run() {
					for(Player all : Bukkit.getOnlinePlayers()) {
						StringUtils.sendActionBar(all, ChatColor.YELLOW + "" + TimerHandler.getTimer("reviewWait").getLeft() + " seconds left to vote");
					}
				}
			}, null, new Runnable() {
				public void run() {
					if(areas.size() > id + 1) {
						areas.get(id + 1).review();
						
					} else {
						Thread t = new Thread(new Runnable() {
							public void run() {
								BuildArea first = getHighestPlace();
								int firstAmount = 0;
										
								try {
									firstAmount = first.rating;
									Thread.sleep(1);
									first.rating = -1;
								} catch (Exception e) { }
								
								BuildArea second = getHighestPlace();
								int secondAmount = 0;
								
								try {
									secondAmount = second.rating;
									second.rating = -1;
									Thread.sleep(1);
								} catch (Exception e) { }
								
								BuildArea third = getHighestPlace();
								int thirdAmount = 0;
								
								try {
									thirdAmount = third.rating;
									secondAmount = second.rating;
								} catch (Exception e) { }
								
								Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Lets announce the winners!");
								
								announcePlace("third", third, thirdAmount);
								announcePlace("second", second, secondAmount);
								announcePlace("first", first, firstAmount);
								
								Bukkit.broadcastMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Congratulations to all the winners! And thanks for playing!");
								
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								
								Bukkit.getScheduler().runTask(MasterPlugin.getMasterPlugin(), new Runnable() {
									public void run() {
										GameManager.currentGame.end();
									}
								});
							}
						});
						t.start();
					}
				}
			});
		}
	}
	
	//
	
	public static void equipReview(Player player) {
		player.getInventory().clear();
		
		player.getInventory().setItem(0, ItemUtils.createItem(Material.STAINED_CLAY, 1, (byte) 14, ChatColor.DARK_RED + "Erm... Wat?", null));
		player.getInventory().setItem(1, ItemUtils.createItem(Material.STAINED_CLAY, 1, (byte) 6, ChatColor.RED + "What is this even supposed to be?", null));
		player.getInventory().setItem(2, ItemUtils.createItem(Material.STAINED_CLAY, 1, (byte) 13, ChatColor.DARK_GREEN + "It somewhat relates to the topic I guess.", null));
		player.getInventory().setItem(3, ItemUtils.createItem(Material.STAINED_CLAY, 1, (byte) 5, ChatColor.GREEN + "Mhm, this looks okay.", null));
		player.getInventory().setItem(4, ItemUtils.createItem(Material.STAINED_CLAY, 1, (byte) 3, ChatColor.BLUE + "This is pretty cool actually!", null));
		player.getInventory().setItem(5, ItemUtils.createItem(Material.STAINED_CLAY, 1, (byte) 4, ChatColor.YELLOW + "Wow! That's amazing!", null));
	}
	
	public static BuildArea getHighestPlace() {
		BuildArea highest = null;
		
		for(BuildArea all : areas) {
			if(all.rating >= 0) {
				if(highest == null || all.rating > highest.rating) {
					highest = all;
					
				} else if(all.rating == highest.rating) {
					all.rating -= 1;
				}
			}
		}
		
		return highest;
	}
	
	public static void announcePlace(String place, BuildArea area, int amount) {
		if(area != null) {
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "On the " + place + " place...");

			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			
			for(Player all : Bukkit.getOnlinePlayers()) {
				area.teleportPlayerToArea(all);
			}
			buildAreaInReview = area;
			
			Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + area.owner.getName() + " with " + amount + " points!");

			area.rating = -1;
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
