package com.swingsword.ssengine.game.games.minestrike;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.NameTagVisibility;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.Game;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.GamePlugin;
import com.swingsword.ssengine.game.PreventionSet;
import com.swingsword.ssengine.game.team.TeamConfig;
import com.swingsword.ssengine.game.games.minestrike.commands.Command;
import com.swingsword.ssengine.game.games.minestrike.commands.LoadoutCommand;
import com.swingsword.ssengine.game.games.minestrike.game.BounceManager;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.games.minestrike.game.CustomItems;
import com.swingsword.ssengine.game.games.minestrike.game.DeathManager;
import com.swingsword.ssengine.game.games.minestrike.game.DefuseManager;
import com.swingsword.ssengine.game.games.minestrike.game.RewardManager;
import com.swingsword.ssengine.game.games.minestrike.guns.GunManager;
import com.swingsword.ssengine.game.games.minestrike.listeners.EntityDamageByEntity;
import com.swingsword.ssengine.game.games.minestrike.listeners.EntityExplode;
import com.swingsword.ssengine.game.games.minestrike.listeners.EntityRegainHealth;
import com.swingsword.ssengine.game.games.minestrike.listeners.InventoryClick;
import com.swingsword.ssengine.game.games.minestrike.listeners.PlayerChat;
import com.swingsword.ssengine.game.games.minestrike.listeners.PlayerDeath;
import com.swingsword.ssengine.game.games.minestrike.listeners.PlayerInteract;
import com.swingsword.ssengine.game.games.minestrike.listeners.PlayerJoin;
import com.swingsword.ssengine.game.games.minestrike.listeners.PlayerMove;
import com.swingsword.ssengine.game.games.minestrike.listeners.PlayerPickupItem;
import com.swingsword.ssengine.game.games.minestrike.listeners.PlayerPlaceBlock;
import com.swingsword.ssengine.game.games.minestrike.listeners.PlayerQuit;
import com.swingsword.ssengine.game.games.minestrike.utils.LocationUtils;
import com.swingsword.ssengine.game.games.minestrike.utils.ScoreboardManager;
import com.swingsword.ssengine.game.games.minestrike.utils.WorldEditUtils;
import com.swingsword.ssengine.game.games.minestrike.zones.BombSite;
import com.swingsword.ssengine.game.games.minestrike.zones.BuyZone;
import com.swingsword.ssengine.game.map.MapType;
import com.swingsword.ssengine.game.team.Team;
import com.swingsword.ssengine.game.threads.TimerHandler;
import com.swingsword.ssengine.stats.StatManager;

public class Minestrike extends GamePlugin implements Listener {

	public static int StartCountdownId;
	public static Minestrike plugin;
	
	GunManager guns;
	
	public WorldEditPlugin we;
	
	Command command = new Command();
	WorldEditUtils weu = new WorldEditUtils();
	
	public void onDisable() {
		List<String> remove = new ArrayList<String>();
		for(org.bukkit.scoreboard.Team all : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
			remove.add(all.getName());
		}
		for(String all : remove) {
			Bukkit.getScoreboardManager().getMainScoreboard().getTeam(all).unregister();
		}
	}
	
	public Game onEnable() {
		plugin = this;
		
		we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		
		registerListeners();
		Bukkit.getServer().getPluginManager().registerEvents(this, MasterPlugin.getMasterPlugin());
		
		guns = new GunManager();
		guns.loadGuns();
		
		new BounceManager();
		
		MasterPlugin.getMasterPlugin().getCommand("debug").setExecutor(command);
		MasterPlugin.getMasterPlugin().getCommand("setlocation").setExecutor(command);
		MasterPlugin.getMasterPlugin().getCommand("createbuyarea").setExecutor(weu);
		MasterPlugin.getMasterPlugin().getCommand("setbombsite").setExecutor(weu);
		MasterPlugin.getMasterPlugin().getCommand("guns").setExecutor(guns);
		MasterPlugin.getMasterPlugin().getCommand("loadout").setExecutor(new LoadoutCommand());
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					all.setFoodLevel(20);
					all.setSaturation(10);
				}
			}
		}, 3, 3);
		
		ScoreboardManager.loopScoreboardLoad();
		DefuseManager.progressDefuse();
		
		PreventionSet set = new PreventionSet();
		set.doDamage = true;
		set.canAttackPlayers = true;
		set.canAttackEntities = true;
		set.canBuild = false;
		
		return new Game(this, 2, 10, true, true, true, true, set, new TeamConfig(Arrays.asList(new Team("CT", ChatColor.getByChar("3")), new Team("T", ChatColor.RED)), false, NameTagVisibility.NEVER), MapType.DYNAMIC, null) {

			@Override
			public void onEnd() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(RewardManager.lastWonTeam != null) {
						StatManager.addStat(all, "cs_games", 1);
						
						if(Team.getTeam(all) != null) {
							if(Team.getTeam(all).getName().toLowerCase().equals(RewardManager.lastWonTeam.toLowerCase())) {
								StatManager.addStat(all, "cs_wins", 1);
							}
						}
					}
				}
			}

			@Override
			public void onLoad() { }

			@Override
			public void onPlayerJoin(Player arg0) { }

			@Override
			public void onPlayerQuit(Player arg0) { }

			@Override
			public void onTeamJoin(Player player, Team team) {
				LocationUtils.TeleportToGame(player, team);
				Minestrike.equip(player);
			}
			
			@Override
			public void onStart() {
				Bukkit.getWorld("map").setGameRuleValue("keepInventory", "true");
				Bukkit.getWorld("map").setDifficulty(Difficulty.PEACEFUL);
				
				BuyZone.loadBuyZones();
				BombSite.loadBombSite();
				
				CSGOGame.start();
				
				Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						PlayerDeath.checkWin();
						
						Location bombLoc = CSGOGame.getBombLocation();
						if(bombLoc != null) {
							for(Player all : Team.getTeam("T").getPlayers()) {
								all.setCompassTarget(bombLoc);
							}
						}
					}
				}, 20, 20);
			}

		};
	}
	
	public void registerListeners() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		
		pm.registerEvents(new PlayerJoin(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new PlayerQuit(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new PlayerMove(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new EntityDamageByEntity(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new PlayerDeath(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new PlayerInteract(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new PlayerPlaceBlock(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new InventoryClick(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new EntityExplode(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new PlayerPickupItem(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new EntityRegainHealth(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new PlayerChat(), MasterPlugin.getMasterPlugin());
	}
	
	public void NextRound(boolean delay) {
		CSGOGame.inPostGameTime = true;
		
		resetMap(false, false);
		DefuseManager.reset();
		
		Runnable run = new Runnable() {
			public void run() {
				CSGOGame.inPostGameTime = false;
				
				resetMap(true, true);
				DefuseManager.reset();
				DeathManager.reset();
				
				CSGOGame.CurrentRound = CSGOGame.CurrentRound + 1;
				
				if(CSGOGame.CurrentRound == 8) {
					Bukkit.broadcastMessage(ChatColor.GREEN + "Switching teams...");
					
					Team.switchTeams(Team.getTeam("CT"), Team.getTeam("T"));
				}
				
				StartBuytime();
				
				for(Player p : Bukkit.getOnlinePlayers()) {
					LocationUtils.TeleportToGame(p, Team.getTeam(p));
					p.setHealth(20d);
					
					CSGOGame.equipPlayer(p);
				}
				
				PlayerDeath.checkWin();
				
				if(Team.getTeam("T").getPlayers().size() == 0 || Team.getTeam("CT").getPlayers().size() == 0) {
					GameManager.currentGame.end();
					
				} else {
					Player bombPlayer = Team.getTeam("T").getPlayers().get(new Random().nextInt(Team.getTeam("T").getPlayers().size()));
					bombPlayer.getInventory().setItem(7, CustomItems.bomb);
					
					for(Player all : Team.getTeam("T").getPlayers()) {
						if(!all.equals(bombPlayer)) {
							bombPlayer.getInventory().setItem(7, CustomItems.bombTracker);

						}
					}
				}
			}
		};
		
		if(delay == true) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), run, 7 * 20);
		} else {
			run.run();
		}
	}
	
	public static void StartBuytime() {
		TimerHandler.createTimer("buytime", 20, 20, new Runnable() {
			public void run() {
				int pregameTime = TimerHandler.getTimer("buytime").getLeft() - 5;
				
				if(pregameTime > 0) {
					
				} else if(pregameTime == 0) {
					StartRoundtime();
				}
			}
		}, null, new Runnable() {
			public void run() {
				for(Player all : Bukkit.getOnlinePlayers()) {
					if(all.getOpenInventory().getTitle().contains("Buy")) {
						all.closeInventory();
					}
				}
				
				Bukkit.broadcastMessage(ChatColor.GRAY + "Buytime has expired.");
			}
		});
	}
	
	public static void StartRoundtime() {
		TimerHandler.createTimer("roundtime", 105, 20, new Runnable() {
			public void run() {
				
			}
		}, null, new Runnable() {
			public void run() {
				CSGOGame.winTeam("CT", "TIME_UP");
			}
		});
	}
	
	public void resetMap(boolean removeEntities, boolean resetBomb) {		
		if(resetBomb == true) {
			if(CSGOGame.bombLoc != null) {
				CSGOGame.bombLoc.getBlock().setType(Material.AIR);
				CSGOGame.bombLoc = null;
				
				if(TimerHandler.getTimer("bombtime") != null) {
					TimerHandler.getTimer("bombtime").cancel();
				}
			}
		}
		
		if(TimerHandler.getTimer("buytime") != null) {
			TimerHandler.getTimer("buytime").cancel();
		}
		if(TimerHandler.getTimer("roundtime") != null) {
			TimerHandler.getTimer("roundtime").cancel();
		}
		
		if(removeEntities) {
			for(World world : Bukkit.getWorlds()) {
				for(Entity ent : world.getEntities()) {
					if(!(ent instanceof Player)) {
						ent.remove();
					}
				}
			}
		}
	}
	
	//Teams
	
	public static void resetInv(Player player) {
		player.setDisplayName(player.getName());
		player.setPlayerListName(player.getName());
		
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
	}
	
	public static void equip(Player player) {
		resetInv(player);
		
		player.setGameMode(GameMode.SURVIVAL);
		
		ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
		LeatherArmorMeta hm = (LeatherArmorMeta) helmet.getItemMeta();
		ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
		LeatherArmorMeta cm = (LeatherArmorMeta) chest.getItemMeta();
		ItemStack leg = new ItemStack(Material.LEATHER_LEGGINGS);
		LeatherArmorMeta lm = (LeatherArmorMeta) leg.getItemMeta();
		ItemStack boot = new ItemStack(Material.LEATHER_BOOTS);
		LeatherArmorMeta bm = (LeatherArmorMeta) boot.getItemMeta();
		
		Color color;

		if(Team.getTeam(player).getName().startsWith("T")) {
			color = Color.fromRGB(255, 0, 0);
		} else {
			color = Color.fromRGB(102, 153, 216);
		}
		
		hm.setColor(color);
		helmet.setItemMeta(hm);
		cm.setColor(color);
		chest.setItemMeta(cm);
		lm.setColor(color);
		leg.setItemMeta(lm);
		bm.setColor(color);
		boot.setItemMeta(bm);
		
		player.getInventory().setHelmet(helmet);
		player.getInventory().setChestplate(chest);
		player.getInventory().setLeggings(leg);
		player.getInventory().setBoots(boot);
		
		player.setDisplayName(Team.getTeam(player).color + player.getName() + ChatColor.RESET);
	}
}