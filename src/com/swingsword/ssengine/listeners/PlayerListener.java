package com.swingsword.ssengine.listeners;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.potion.PotionEffect;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.bungee.Channel;
import com.swingsword.ssengine.command.StaffCommand;
import com.swingsword.ssengine.command.VanishCommand;
import com.swingsword.ssengine.game.Game;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.GameState;
import com.swingsword.ssengine.game.team.Team;
import com.swingsword.ssengine.game.threads.LobbyCounter;
import com.swingsword.ssengine.player.PlayerProfileSettings;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.stats.StatManager;
import com.swingsword.ssengine.utils.LobbyUtils;
import com.swingsword.ssengine.utils.PlayerUtils;
import com.swingsword.ssengine.utils.ScoreboardUtils;
import com.swingsword.ssengine.utils.StringUtils;

@SuppressWarnings("deprecation")
public class PlayerListener implements Listener {
	
	public static ArrayList<String> badWords = new ArrayList<>();

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		event.setJoinMessage(null);
		
		StatManager.loadPlayerStats(player);
		ScoreboardUtils.load(player);
		PlayerSessionManager.startSession(player);

		//TODO
		if (Channel.serverName == null) {
			if (Bukkit.getOnlinePlayers().size() > 0) {
				MasterPlugin.getMasterPlugin().channel.getName();
			}
		}

		VanishCommand.load(player);
		
		if (!GameManager.currentGame.gamePlugin.getName().contains("Hub") && !player.isOp()) {
			player.setAllowFlight(false);
		}
		
		for(PotionEffect all : player.getActivePotionEffects()) {
			player.removePotionEffect(all.getType());	
		}
		if (GameManager.currentGame.hasLobby) {
			Game.resetPlayer(player);
		}

		Team.addToRandomTeam(player);
		GameManager.currentGame.onPlayerJoin(player);

		if (GameManager.currentGame.hasLobby) {
			if (GameState.isState(GameState.IN_LOBBY)) {
				LobbyUtils.teleportToLobby(player);
			}
		}
		
		if (Bukkit.getServer().getOnlinePlayers().size() == GameManager.currentGame.minPlayers && GameState.isState(GameState.IN_LOBBY) && !LobbyCounter.isRunning) {
			if(LobbyCounter.delay != -1) {
				new LobbyCounter(20);
			} else {
				Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new LobbyCounter(20), 20l, 20l);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		event.setQuitMessage(null);
		
		if(GameManager.currentGame != null) {
			GameManager.currentGame.onPlayerQuit(player);
			
			if(GameManager.currentGame.hasLobby) {
				Game.resetPlayer(player);
			}
			
			if(!GameState.isState(GameState.IN_LOBBY)) {
				if(Bukkit.getOnlinePlayers().size() - 1 < GameManager.currentGame.minPlayers) {
					if(GameManager.currentGame.endGameOnLessThanMinPlayers) {
						GameManager.currentGame.end();
					}
				}
			}
		}
		
		Channel.playerServer.remove(player.getName());
		Channel.playerParty.remove(player.getName());
		Channel.playerSessionRequests.remove(player.getName());
		VanishCommand.reset(player);
		ScoreboardUtils.removeGamemodeTeams(player);
		ScoreboardUtils.unload(player);
		StaffCommand.disable(player);
		
		String timeStamp = new SimpleDateFormat("yy/MM/dd_HH:mm:ss").format(Calendar.getInstance().getTime());
		PlayerSessionManager.getSession(player).getAccount().getCache().put("lastOnline", timeStamp + " " + Channel.serverName);
		
		StatManager.saveStatChanges(player, false);
		PlayerSessionManager.endSession(player, false, true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsyncPlayerLogin(AsyncPlayerPreLoginEvent event) {
		if (GameManager.currentGame != null) {
			if (!GameState.isState(GameState.IN_LOBBY) && !GameManager.currentGame.canJoinMidGame) {
				event.setLoginResult(Result.KICK_OTHER);
				event.disallow(Result.KICK_OTHER, ChatColor.RED + "Game already started!");

			} else if (Bukkit.getOnlinePlayers().size() == GameManager.currentGame.maxPlayers) {
				if (GameManager.currentGame != null) {
					event.setLoginResult(Result.KICK_OTHER);
					event.disallow(Result.KICK_OTHER, ChatColor.RED + "That server is full.");
				}
			}
		} else {
			event.setLoginResult(Result.KICK_OTHER);
			event.disallow(Result.KICK_OTHER, ChatColor.RED + "You can't join this server at this time.");
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		String msg = event.getMessage();
		
		if(PlayerSessionManager.getSession(player).created == false) {
			event.setCancelled(true);
			return;
		}
		if(PlayerSessionManager.getSession(player).getAccount().isLoaded()) {
			event.setFormat(StringUtils.getFormat(player) + ChatColor.AQUA + "%1$s " + ChatColor.WHITE + "%2$s");
			if(PlayerSessionManager.getSession(player).getAccount().isMuted()) {
				event.setCancelled(PlayerUtils.isConvicted(player, "mute"));
			}
		} else {
			event.setFormat(ChatColor.AQUA + " %1$s " + ChatColor.WHITE + "%2$s");
		}
		
		for (String words : msg.split(" ")) {
			if (badWords.contains(words.toLowerCase())) {
				String output = "";
				for (int i = 0; i < words.length(); i++) {
					output = output + "*";
					if (i == (words.length() - 1)) {
						msg = msg.replace(words, output);
					}
				}
			}
		}
		
		event.setMessage(msg);

		if(!player.isOp() && PlayerSessionManager.getSession(player).getAccount().getMainRank() != null && !PlayerSessionManager.getSession(player).getAccount().getMainRank().staff) {
			msg = msg.replace(" ", "").toLowerCase();

			for(String alias : StringUtils.getAliases().keySet()) {
				msg = msg.replace(alias, StringUtils.getAliases().get(alias));
			}
			
			for(String block : StringUtils.blockedKeys) {
				if(msg.contains(block.toLowerCase())) {
					event.setCancelled(true);
					
					for(Player p : Bukkit.getOnlinePlayers()) {
						if(p.isOp() || PlayerSessionManager.getSession(player).getAccount().getMainRank().staff) {
							p.sendMessage(ChatColor.BLUE + player.getName() + ChatColor.YELLOW + " was caught advertising!");
						}
					}
					
					player.sendMessage(ChatColor.RED + "Do not advertise!");
					
					return;
				}
			}
		}
		
		/*List<Player> remove = new ArrayList<Player>();
		for(Player all : event.getRecipients()) {
			if(PlayerSessionManager.getSession(all).created == true) {
				String lang = "all";
				if (PlayerSessionManager.getSession(all).getAccount().profileSettings.getSetting("lang") != null) {
					lang = PlayerSessionManager.getSession(all).getAccount().profileSettings.getSetting("lang");
				}
				
				if(!lang.equalsIgnoreCase("all") && (PlayerSessionManager.getSession(player).getAccount().getMainRank() == null || !PlayerSessionManager.getSession(player).getAccount().getMainRank().staff) && !lang.equalsIgnoreCase(PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("lang"))) {
					remove.add(all);
				}
			} else {
				remove.add(all);
			}
		}
		for(Player all : remove) {
			event.getRecipients().remove(all);
		}*/
		
		if(!event.isCancelled()) {
			if(StatManager.getStat(player, "b_chat") != 1) {
				StatManager.setStat(player, "b_chat", 1);
			}
			System.out.println(ChatColor.stripColor(event.getFormat().replace("%1$s", player.getDisplayName()).replace("%2$s", event.getMessage())));
			
			for(Player all : event.getRecipients()) {
				if(PlayerSessionManager.getSession(all).created == true) {
					if(!PlayerSessionManager.getSession(all).getAccount().getBlocks().contains(player.getUniqueId()) && !PlayerSessionManager.getSession(player).getAccount().getBlocks().contains(all.getUniqueId())) {	
						event.setCancelled(true);
						
						String message = getHoverMessage(all, player);
												
						StringUtils.sendClickMessage(all, event.getFormat().replace("%1$s", player.getDisplayName()).replace("%2$s", event.getMessage()), message, (message.contains("profile: ") ? "/search stop" : "/search " + player.getName()));
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (PlayerSessionManager.playerSession.get(player.getName()) != null) {
			if (!PlayerSessionManager.getSession(player).created && player.isOnGround()) {
				Location to = event.getFrom();
				to.setPitch(event.getTo().getPitch());
				to.setYaw(event.getTo().getYaw());
				event.setTo(to);
			}
		}
		if(GameManager.currentMap != null) {
			if(GameManager.currentMap.border != null) {
				if(player.getWorld().getName().equals(GameManager.currentMap.world.getName())) {
					if(!GameManager.currentMap.border.containsBlock(event.getTo()) && GameManager.currentMap.border.containsBlock(event.getFrom())) {
						player.teleport(event.getFrom());
						player.sendMessage(ChatColor.RED + "You can't leave the map!");
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		
		if(event.getMessage().toLowerCase().startsWith("/tell")) {
			event.setMessage(event.getMessage().replaceFirst("tell", "msg"));
		
		} else if(event.getMessage().toLowerCase().startsWith("/me")) {
			event.setCancelled(true);
			
			player.sendMessage("Unknown command. Type \"/help\" for help.");
		}
	}
	
	public static String getHoverMessage(Player all, Player player) {
		String message = ChatColor.GRAY + "Click to view " + player.getName() + "'s profile";
		String badMessage = ChatColor.RED + "You can't view " + player.getName() + "'s profile: ";
		
		if(PlayerSessionManager.getSession(player).getAccount().getBlocks().contains(all.getUniqueId())) {
			message = badMessage + "blocked";
		}
		if(!PlayerProfileSettings.isAllowed(all, PlayerSessionManager.getSession(player).getAccount(), "1")) {
			if(Integer.parseInt(PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("1")) == 1) {
				message = badMessage + "friends only";
			} else {
				message = badMessage + "private";
			}
		}
		
		if(all.equals(player)) {
			message = ChatColor.GRAY + "Click to view your profile";
		}
		
		return message;
	}
	
	public static void loadBadWords() {
		badWords.add("fuck");
		badWords.add("fucking");
		badWords.add("shit");
		badWords.add("ass");
		badWords.add("piss");
		badWords.add("asshole");
		badWords.add("motherfucker");
		badWords.add("fucker");
		badWords.add("cunt");
		badWords.add("bitch");
		badWords.add("damn");
		badWords.add("dick");
		badWords.add("pussy");
		badWords.add("vagina");
		badWords.add("fag");
		badWords.add("bastard");
		badWords.add("slut");
		badWords.add("douche");
		badWords.add("cock");
		badWords.add("whore");
		badWords.add("penis");
	}
}
