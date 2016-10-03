package com.swingsword.ssengine;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.swingsword.ssengine.achievements.AchievementManager;
import com.swingsword.ssengine.bar.BarManager;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.command.BanCommand;
import com.swingsword.ssengine.command.BlockCommand;
import com.swingsword.ssengine.command.DatabaseCommand;
import com.swingsword.ssengine.command.FriendCommand;
import com.swingsword.ssengine.command.HubCommand;
import com.swingsword.ssengine.command.InboxCommand;
import com.swingsword.ssengine.command.InvCommand;
import com.swingsword.ssengine.command.JoinCommand;
import com.swingsword.ssengine.command.KickCommand;
import com.swingsword.ssengine.command.MessageCommand;
import com.swingsword.ssengine.command.MuteCommand;
import com.swingsword.ssengine.command.NotRegisteredCommand;
import com.swingsword.ssengine.command.PartyCommand;
import com.swingsword.ssengine.command.ProfileCommand;
import com.swingsword.ssengine.command.SearchCommand;
import com.swingsword.ssengine.command.SettingsCommand;
import com.swingsword.ssengine.command.StaffCommand;
import com.swingsword.ssengine.command.TempbanCommand;
import com.swingsword.ssengine.command.UnbanCommand;
import com.swingsword.ssengine.command.UnmuteCommand;
import com.swingsword.ssengine.command.UpdateCommand;
import com.swingsword.ssengine.command.VanishCommand;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.entity.InteractEntityManager;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.preventevents.EntityDamage;
import com.swingsword.ssengine.game.preventevents.PlayerAttackEntity;
import com.swingsword.ssengine.game.preventevents.PlayerAttackPlayer;
import com.swingsword.ssengine.game.preventevents.PlayerBreak;
import com.swingsword.ssengine.game.preventevents.PlayerBuild;
import com.swingsword.ssengine.game.preventevents.PlayerPickup;
import com.swingsword.ssengine.inventory.ItemManager;
import com.swingsword.ssengine.language.LanguageManager;
import com.swingsword.ssengine.listeners.EntityListener;
import com.swingsword.ssengine.listeners.InventoryListener;
import com.swingsword.ssengine.listeners.PlayerListener;
import com.swingsword.ssengine.listeners.SpamListener;
import com.swingsword.ssengine.listeners.WorldListener;
import com.swingsword.ssengine.options.OptionHandler;
import com.swingsword.ssengine.player.AccountCreationManager;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.rank.RankManager;
import com.swingsword.ssengine.scoreboard.ScoreboardManager;
import com.swingsword.ssengine.server.ServerManager;
import com.swingsword.ssengine.socket.ClientInstance;
import com.swingsword.ssengine.stats.StatManager;
import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.HologramUtils;
import com.swingsword.ssengine.utils.OptionsUtils;
import com.swingsword.ssengine.utils.ScoreboardUtils;
import socket.SocketMessage;

public class MasterPlugin extends JavaPlugin {
	
	private static MasterPlugin instance;
	public Channel channel;
	
	@Override
	public void onEnable() {
		instance = this;
		
		ConfigUtils.updateType("cache");
		
		channel = new Channel();
		new ClientInstance();
		
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", channel);
		
		registerEvents();
		registerCommands();
		
		new SQLManager();
		new RankManager();
		new AchievementManager();
		new ServerManager();
		new ItemManager();
		new PlayerSessionManager();
		new StatManager();
		new LanguageManager();
		new BarManager();
		new ScoreboardManager();
		new InteractEntityManager(false);
		
		OptionsUtils.loadOptions();
		PlayerListener.loadBadWords();
		
		if(!getConfig().getString("loadedStaticGamemode").equals("null")) {
			GameManager.startGame(getConfig().getString("loadedStaticGamemode"), null);
		}
	}
	
	@Override
	public void onDisable() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			ScoreboardUtils.unload(all);
		}
		StatManager.saveAllStats(true);
		HologramUtils.unload();
		
		for (Player all : Bukkit.getOnlinePlayers()) {
			PlayerSessionManager.endSession(all, true, true);
			Channel.sendToServer(all, Channel.getBestHub());
		}
		
		ClientInstance.sc.sendMessage(new SocketMessage("sendMotd", MasterPlugin.getServerName(), ServerManager.currentMotd.replace(";" + Bukkit.getOnlinePlayers().size() + ";", ";0;")).toString());
		ConfigUtils.updateType("jar");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) { }
	}
	
	public void registerEvents() {
		PluginManager pm = Bukkit.getServer().getPluginManager();
		
		pm.registerEvents(new PlayerListener(), this);
		pm.registerEvents(new SpamListener(), this);
		pm.registerEvents(new OptionHandler(), this);
		pm.registerEvents(new InventoryListener(), this);
		pm.registerEvents(new EntityListener(), this);
		pm.registerEvents(new AccountCreationManager(), this);
		pm.registerEvents(new StaffCommand(), this);
		pm.registerEvents(new WorldListener(), this);
		
		//prevent events
		pm.registerEvents(new PlayerBuild(), this);
		pm.registerEvents(new PlayerBreak(), this);
		pm.registerEvents(new PlayerPickup(), this);
		pm.registerEvents(new PlayerAttackEntity(), this);
		pm.registerEvents(new PlayerAttackPlayer(), this);
		pm.registerEvents(new EntityDamage(), this);
	}
	
	public void registerCommands() {
		PluginDescriptionFile pdf = this.getDescription();
		for(String command : pdf.getCommands().keySet()) {
			getCommand(command).setExecutor(new NotRegisteredCommand());
		}
		getCommand("db").setExecutor(new DatabaseCommand());
		getCommand("friend").setExecutor(new FriendCommand());
		getCommand("friends").setExecutor(new FriendCommand());
		getCommand("block").setExecutor(new BlockCommand());
		getCommand("message").setExecutor(new MessageCommand());
		getCommand("msg").setExecutor(new MessageCommand());
		getCommand("inbox").setExecutor(new InboxCommand());
		getCommand("profile").setExecutor(new ProfileCommand());
		getCommand("search").setExecutor(new SearchCommand());
		getCommand("join").setExecutor(new JoinCommand());
		getCommand("party").setExecutor(new PartyCommand());
		getCommand("hub").setExecutor(new HubCommand());
		getCommand("ban").setExecutor(new BanCommand());
		getCommand("tempban").setExecutor(new TempbanCommand());
		getCommand("unban").setExecutor(new UnbanCommand());
		getCommand("mute").setExecutor(new MuteCommand());
		getCommand("unmute").setExecutor(new UnmuteCommand());
		getCommand("kick").setExecutor(new KickCommand());
		getCommand("inv").setExecutor(new InvCommand());
		getCommand("settings").setExecutor(new SettingsCommand());
		getCommand("vanish").setExecutor(new VanishCommand());
		getCommand("update").setExecutor(new UpdateCommand());
		getCommand("staff").setExecutor(new StaffCommand());
	}
	
	public static MasterPlugin getMasterPlugin() {
		return instance;
	}
	
	public static File getDataDirectory() {
		File dir = getServerDirectory();
		
		for (int x = 0; x < 5; x++) {
			if (x != 0) {
				dir = dir.getParentFile();
			}
			for (File files : dir.listFiles()) {
				if (files.isDirectory() && files.getName().equals("data")) {
					return files;
				}
			}
		}
		return null;
	}
	
	public static File getServerDirectory() {
		return new File(MasterPlugin.getMasterPlugin().getDataFolder().getParentFile().getAbsolutePath().replace("plugins", ""));
	}
	
	public static String getServerName() {
		return new File((instance.getDataFolder().getParentFile().getAbsolutePath().replace("\\", "/")).replace("/plugins", "")).getName();
	}
}
