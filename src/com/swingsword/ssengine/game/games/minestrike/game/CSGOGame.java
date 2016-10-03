package com.swingsword.ssengine.game.games.minestrike.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.games.minestrike.Minestrike;
import com.swingsword.ssengine.game.games.minestrike.guns.Gun;
import com.swingsword.ssengine.game.games.minestrike.listeners.EntityDamageByEntity;
import com.swingsword.ssengine.game.games.minestrike.utils.ScoreboardManager;
import com.swingsword.ssengine.game.games.minestrike.zones.BombSite;
import com.swingsword.ssengine.game.team.Team;
import com.swingsword.ssengine.game.threads.TimerHandler;
import com.swingsword.ssengine.utils.ItemUtils;

public class CSGOGame {

	private static boolean hasStarted = false;
	
	public static HashMap<String, Integer> Kills = new HashMap<String, Integer>();
	public static HashMap<String, Integer> Deaths = new HashMap<String, Integer>();
	public static HashMap<String, Integer> Money = new HashMap<String, Integer>();
	public static int Twins = 0;
	public static int CTwins = 0;
	public static int CurrentRound = 0;
	
	public static List<BombSite> sites = new ArrayList<BombSite>();
	public static Location bombLoc;
	
	public static boolean inPostGameTime = false;
	
	public static void start() {
		for(Player all : Bukkit.getOnlinePlayers()) {
			all.getInventory().clear();
			loadPlayer(all);
		}
		
		hasStarted = true;
		
		Minestrike.plugin.NextRound(false);
		
		//Start
		CurrentRound = 0;
		CTwins = 0;
		Twins = 0;
	}
	
	public static void stop() {
		CurrentRound = 0;
		Kills.clear();
		Deaths.clear();
		hasStarted = false;
	}
	
	public static boolean hasStarted() {
		return hasStarted;
	}
	
	public static void loadPlayer(Player player) {
		Money.put(player.getName(), 800);
		Kills.put(player.getName(), 0);
		Deaths.put(player.getName(), 0);
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			if(DeathManager.dead.contains(all.getName())) {
				player.hidePlayer(all);
			}
		}
	}
	
	public static void resetPlayer(Player player) {
		player.getInventory().clear();
		
		Money.put(player.getName(), 800);
	}
	
	public static void equipPlayer(Player player) {
		if(player.getInventory().getItem(0) != null && Gun.isGun(player.getInventory().getItem(0))) {
			String owner = ChatColor.stripColor(player.getInventory().getItem(0).getItemMeta().getDisplayName().split("'")[0]);
			player.getInventory().setItem(0, Gun.getGun(player.getInventory().getItem(0).getItemMeta().getDisplayName()).toItemStack(true, owner));
		}
		
		if(player.getInventory().getItem(1) == null) {
			if(Team.getTeam(player).getName().equals("CT")) {
				player.getInventory().setItem(1, Gun.getGun("P2000").toItemStack(true, player.getName()));
			} else {
				player.getInventory().setItem(1, Gun.getGun("Glock-18").toItemStack(true, player.getName()));
			}
			
		} else {
			String owner = ChatColor.stripColor(player.getInventory().getItem(1).getItemMeta().getDisplayName().split("'")[0]);
			player.getInventory().setItem(1, Gun.getGun(player.getInventory().getItem(1).getItemMeta().getDisplayName()).toItemStack(true, owner));
		}
		
		player.getInventory().setItem(2, CustomItems.knife);
		player.getInventory().setItem(8, ItemUtils.itemStackFromString("i=54;n=&f&lBuy"));
		
		EntityDamageByEntity.playerDamageSet.put(player, new HashMap<Player, ArrayList<Double>>());
	}
	
	public static void winTeam(final String team, String winMethod) {
		if(!inPostGameTime) {
			Team winTeam = Team.getTeam(team);
			
			if(winMethod.equals("DEFUSE")) {
				if(TimerHandler.getTimer("bombtime") != null) {
					TimerHandler.getTimer("bombtime").cancel();
				}
				
				for(Player player : Bukkit.getOnlinePlayers()) {
					player.playSound(player.getLocation(), "game.defuse", 1, 1);
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						for(Player player : Bukkit.getOnlinePlayers()) {
							player.playSound(player.getLocation(), "game." + team.toLowerCase() + "win", 1, 1);	
						}
					}
				}, 40);
				
			} else {
				for(Player player : Bukkit.getOnlinePlayers()) {
					player.playSound(player.getLocation(), "game." + team.toLowerCase() + "win", 1, 1);	
				}
			}
			
			if(team.equals("CT")) {
				CTwins = CTwins + 1;
				Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "Counter-Terrorists Win!");
				
				if(RewardManager.lastWonTeam.equals("CT")) {
					RewardManager.winStreak += 1;
				} else {
					RewardManager.lastWonTeam = "CT";
					RewardManager.winStreak = 1;
				}
				
				if(CTwins == 8) {
					GameManager.currentGame.end();
				}
				
			} else {
				Twins = Twins + 1;
				Bukkit.getServer().broadcastMessage(ChatColor.RED + "Terrorists Win!");
				
				if(RewardManager.lastWonTeam.equals("T")) {
					RewardManager.winStreak += 1;
				} else {
					RewardManager.lastWonTeam = "T";
					RewardManager.winStreak = 1;
				}
				
				if(Twins == 8) {
					GameManager.currentGame.end();
				}
			}
			
			Minestrike.plugin.NextRound(true);
			
			RewardManager.rewardEndRound(winTeam, winMethod);
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				if(Team.hasTeam(all)) {
					ScoreboardManager.loadScoreboard(all);
				}
			}
		}
	}
	
	public static Location getBombLocation() {
		if(CSGOGame.bombLoc != null) {
			return CSGOGame.bombLoc;
			
		} else {
			for(Entity ent : Bukkit.getWorld("map").getEntities()) {
				if(ent instanceof Item) {
					Item item = (Item) ent;
					
					if(item.getItemStack().getType() == Material.TNT) {
						return item.getLocation();
					}
				}
			}
			
			Location loc = null;
			for(Player all : Team.getTeam("T").getPlayers()) {
				if(all.getInventory().getItem(7) != null && all.getInventory().getItem(7).getType() == Material.TNT) {
					if(loc == null) {
						loc = all.getLocation();
					} else {
						all.getInventory().setItem(7, null);
					}
				}
			}
			return loc;
		}
	}
}
