package com.swingsword.ssengine.game.games.rust;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.Game;
import com.swingsword.ssengine.game.GamePlugin;
import com.swingsword.ssengine.game.PreventionSet;
import com.swingsword.ssengine.game.games.rust.building.Building;
import com.swingsword.ssengine.game.games.rust.commands.AddspawnCommand;
import com.swingsword.ssengine.game.games.rust.commands.AdminCommand;
import com.swingsword.ssengine.game.games.rust.commands.AirdropCommand;
import com.swingsword.ssengine.game.games.rust.commands.CancelCommand;
import com.swingsword.ssengine.game.games.rust.commands.CraftCommand;
import com.swingsword.ssengine.game.games.rust.commands.CreatelootcrateCommand;
import com.swingsword.ssengine.game.games.rust.commands.CreatenobuildzoneCommand;
import com.swingsword.ssengine.game.games.rust.commands.CreateorespawnzoneCommand;
import com.swingsword.ssengine.game.games.rust.commands.CreateradzoneCommand;
import com.swingsword.ssengine.game.games.rust.commands.CreatespawnzoneCommand;
import com.swingsword.ssengine.game.games.rust.commands.CustomitemCommand;
import com.swingsword.ssengine.game.games.rust.commands.EntercodeCommand;
import com.swingsword.ssengine.game.games.rust.commands.GunCommand;
import com.swingsword.ssengine.game.games.rust.commands.SetcodeCommand;
import com.swingsword.ssengine.game.games.rust.commands.SuicideCommand;
import com.swingsword.ssengine.game.games.rust.crafting.Crafting;
import com.swingsword.ssengine.game.games.rust.guns.Guns;
import com.swingsword.ssengine.game.games.rust.listeners.AirdropListener;
import com.swingsword.ssengine.game.games.rust.listeners.BackpackListener;
import com.swingsword.ssengine.game.games.rust.listeners.BuildingListener;
import com.swingsword.ssengine.game.games.rust.listeners.C4Listener;
import com.swingsword.ssengine.game.games.rust.listeners.CookingListener;
import com.swingsword.ssengine.game.games.rust.listeners.CraftingListener;
import com.swingsword.ssengine.game.games.rust.listeners.DamageListener;
import com.swingsword.ssengine.game.games.rust.listeners.DoorListener;
import com.swingsword.ssengine.game.games.rust.listeners.GunListener;
import com.swingsword.ssengine.game.games.rust.listeners.LootListener;
import com.swingsword.ssengine.game.games.rust.listeners.MobsListener;
import com.swingsword.ssengine.game.games.rust.listeners.OresListener;
import com.swingsword.ssengine.game.games.rust.listeners.PlayerListener;
import com.swingsword.ssengine.game.games.rust.listeners.RadiationListener;
import com.swingsword.ssengine.game.games.rust.listeners.RespawnListener;
import com.swingsword.ssengine.game.games.rust.listeners.SleeperListener;
import com.swingsword.ssengine.game.games.rust.mobs.Mobs;
import com.swingsword.ssengine.game.games.rust.ores.Ores;
import com.swingsword.ssengine.game.games.rust.utils.AirdropUtils;
import com.swingsword.ssengine.game.games.rust.utils.CookingUtils;
import com.swingsword.ssengine.game.games.rust.utils.DoorUtils;
import com.swingsword.ssengine.game.games.rust.utils.InventoryUtils;
import com.swingsword.ssengine.game.games.rust.utils.LootUtils;
import com.swingsword.ssengine.game.games.rust.utils.PropUtils;
import com.swingsword.ssengine.game.games.rust.utils.RadiationUtils;
import com.swingsword.ssengine.game.games.rust.utils.SchedulerUtils;
import com.swingsword.ssengine.game.games.rust.utils.SimpleLocation;
import com.swingsword.ssengine.game.games.rust.utils.SpawnUtils;
import com.swingsword.ssengine.game.games.rust.utils.TimeUtils;
import com.swingsword.ssengine.game.map.MapType;
import com.swingsword.ssengine.game.team.Team;

public class Rust extends GamePlugin implements Listener {

	public static Rust plugin;
	public static WorldEditPlugin we = null;
	
	public static HashMap<String, List<SimpleLocation>> firePreview = new HashMap<String, List<SimpleLocation>>();
	public static HashMap<String, SimpleLocation> placeLoc = new HashMap<String, SimpleLocation>();
	public static ArrayList<String> bleeding = new ArrayList<String>();
	public ArrayList<String> clickDelay = new ArrayList<String>();
	public ArrayList<Player> cooldown = new ArrayList<Player>();
	public static HashMap<String, Integer> playerFlare = new HashMap<String, Integer>();
	
	@Override
	public Game onEnable() {
		plugin = this;
		we = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		
		PreventionSet set = new PreventionSet();
		set.doDamage = true;
		set.canAttackPlayers = true;
		set.canAttackEntities = true;
		set.canBreak = true;
		set.canBuild = true;
		set.canPickupItems = true;
		
		return new Game(this, 0, 50, true, false, false, false, set, null, MapType.STATIC, null) {

			@Override
			public void onEnd() { }
			
			@Override
			public void onLoad() {
				
				registerCommands();
				registerEvents();
				
				CookingUtils.loadCooks();
				RadiationUtils.loadRadiation();
				SpawnUtils.loadSpawns();
				Ores.loadOres();
				Building.loadBuilding();
				Crafting.loadCrafting();
				DoorUtils.loadDoors();
				AirdropUtils.loadAirdrops();
				Mobs.loadMobs();
				Guns.loadGuns();
				LootUtils.loadLoot();
				InventoryUtils.loadInventoryLimiter();
				
				SchedulerUtils.loadSchedulers();
				new TimeUtils();
				
				for (Player all : Bukkit.getOnlinePlayers()) {
					RadiationUtils.playerRad.put(all.getName(), 0);
					PropUtils.playerFood.put(all.getName(), 1500);
				}
				
				for(LivingEntity all : Bukkit.getWorld("map").getLivingEntities()) {
					if(!(all instanceof Player)) {
						all.remove();
					}
				}
			}

			@Override
			public void onPlayerJoin(Player player) { }

			@Override
			public void onPlayerQuit(Player player) { }

			@Override
			public void onTeamJoin(Player player, Team team) { }
			
			@Override
			public void onStart() {
			}
		};
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onDisable() {
		Ores.saveOres();
		SpawnUtils.restartSpawns();
		Building.saveBuilding();
		Crafting.endCrafting();
		Mobs.removeMobs();
		AirdropUtils.saveAirdrop();
		LootUtils.removeLoot();
		
		for (String loc : CookingUtils.furnaceInventory.keySet()) {
			if (CookingUtils.furnaceInventory.get(loc).getItem(13).getData().getData() == 13) {
				new SimpleLocation(loc).getBlock().setData((byte) (new SimpleLocation(loc).getBlock().getData() - 4));
			}
		}
	}
	
	public void registerCommands() {
		MasterPlugin.getMasterPlugin().getCommand("addspawn").setExecutor(new AddspawnCommand());
		MasterPlugin.getMasterPlugin().getCommand("airdrop").setExecutor(new AirdropCommand());
		MasterPlugin.getMasterPlugin().getCommand("createlootcrate").setExecutor(new CreatelootcrateCommand());
		MasterPlugin.getMasterPlugin().getCommand("createnobuildzone").setExecutor(new CreatenobuildzoneCommand());
		MasterPlugin.getMasterPlugin().getCommand("createorespawnzone").setExecutor(new CreateorespawnzoneCommand());
		MasterPlugin.getMasterPlugin().getCommand("createradzone").setExecutor(new CreateradzoneCommand());
		MasterPlugin.getMasterPlugin().getCommand("createspawnzone").setExecutor(new CreatespawnzoneCommand());
		MasterPlugin.getMasterPlugin().getCommand("setcode").setExecutor(new SetcodeCommand());
		MasterPlugin.getMasterPlugin().getCommand("entercode").setExecutor(new EntercodeCommand());
		MasterPlugin.getMasterPlugin().getCommand("suicide").setExecutor(new SuicideCommand());
		MasterPlugin.getMasterPlugin().getCommand("cancel").setExecutor(new CancelCommand());
		MasterPlugin.getMasterPlugin().getCommand("c").setExecutor(new CancelCommand());
		MasterPlugin.getMasterPlugin().getCommand("craft").setExecutor(new CraftCommand());
		MasterPlugin.getMasterPlugin().getCommand("gun").setExecutor(new GunCommand());
		MasterPlugin.getMasterPlugin().getCommand("f").setExecutor(new GunCommand());
		MasterPlugin.getMasterPlugin().getCommand("customitem").setExecutor(new CustomitemCommand());
		MasterPlugin.getMasterPlugin().getCommand("admin").setExecutor(new AdminCommand());

	}
	
	public void registerEvents() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		
		pm.registerEvents(new AirdropListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new BackpackListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new BuildingListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new C4Listener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new CookingListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new CraftingListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new DamageListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new DoorListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new GunListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new LootListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new MobsListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new OresListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new PlayerListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new RadiationListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new SleeperListener(), MasterPlugin.getMasterPlugin());
		pm.registerEvents(new RespawnListener(), MasterPlugin.getMasterPlugin());
	}

	public static Rust getInstance() {
		return plugin;
	}
}