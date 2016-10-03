package com.swingsword.ssengine.player;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.language.Language;
import com.swingsword.ssengine.language.LanguageManager;
import com.swingsword.ssengine.listeners.PlayerListener;
import com.swingsword.ssengine.rank.Rank;
import com.swingsword.ssengine.rank.RankManager;
import com.swingsword.ssengine.stats.StatManager;
import com.swingsword.ssengine.utils.BlockUtils;
import com.swingsword.ssengine.utils.ExpUtils;
import com.swingsword.ssengine.utils.FriendUtils;
import com.swingsword.ssengine.utils.GeolocationUtils;
import com.swingsword.ssengine.utils.LanguageUtils;
import com.swingsword.ssengine.utils.PlayerUtils;
import com.swingsword.ssengine.utils.ScoreboardUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class PlayerAccount {
	
	private final UUID uuid;
	private String name = null;
	
	private HashMap<String, Object> cache = new HashMap<String, Object>();
	public PlayerSettings settings;
	public PlayerProfileSettings profileSettings;
	private boolean loaded = false;
	
	public PlayerAccount(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		
		updateCache();
	}
	
	public PlayerAccount(UUID uuid) {
		this.uuid = uuid;
		
		updateCache();
	}
	
	public PlayerAccount(HashMap<String, Object> cache) {
		if(cache != null && cache.get("uuid") != null) {
			this.uuid = UUID.fromString((String) cache.get("uuid"));
			this.name = (String) cache.get("name");
			this.cache = cache;
			
			loadAccount(cache, false);
			
		} else {
			this.uuid = null;
		}
	}
	
	public void updateCache() {
		//Global
		
		if(SQLManager.getSQL("global") != null && SQLManager.getSQL("global").isConnected()) {
			new BukkitRunnable() {
				public void run() {
					boolean exists = SQLManager.getSQL("global").accountExists(uuid);
					
					if(!exists && Bukkit.getOfflinePlayer(uuid).isOnline()) {						
						AccountCreationManager.initCreation(Bukkit.getPlayer(uuid));
					}
										
					if(exists && Bukkit.getOfflinePlayer(uuid).isOnline()) {
						Player player = Bukkit.getPlayer(uuid);
						
						if(PlayerSessionManager.getSession(player) != null) {
							PlayerSessionManager.getSession(player).created = true;
						}
					}
					
					if(exists) {
						final HashMap<String, Object> finalSqlCache = SQLManager.getSQL("global").getValues(uuid);
						new BukkitRunnable() {
							public void run() {
								if(finalSqlCache != null) {
									cache = finalSqlCache;
									
									loadAccount(finalSqlCache, true);
								}
							}
						}.runTask(MasterPlugin.getMasterPlugin());
					}
				}
			}.runTaskAsynchronously(MasterPlugin.getMasterPlugin());
		}
	}
	
	public void loadAccount(HashMap<String, Object> cache, boolean loadWhenOn) {
		loaded = true;
		
		settings = PlayerSettings.fromString((String) cache.get("settings"));
		profileSettings = PlayerProfileSettings.fromString((String) cache.get("profileSettings"));;
		
		if(Bukkit.getOfflinePlayer(uuid).isOnline() && loadWhenOn) {
			final Player player = Bukkit.getPlayer(uuid);
			
			PlayerUtils.sendTab(player);
			
			Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					try {
						HashMap<String, String> geoData = GeolocationUtils.resolveIP(InetAddress.getByName(Bukkit.getPlayer(name).getAddress().getAddress().getHostAddress()));
						if (profileSettings.getSetting("loc") != null && !profileSettings.getSetting("loc").equals(geoData.get("country")) || profileSettings.getSetting("timezone") != null && !profileSettings.getSetting("timezone").equals(geoData.get("timezone"))) {
						}
						profileSettings.setSetting("loc", geoData.get("country"));
						profileSettings.setSetting("timezone", geoData.get("timezone"));
						profileSettings.setSetting("longitude", geoData.get("longitude"));
					} catch (UnknownHostException e) { }
				}
			});
						
			if (isBanned()) {
				if (PlayerUtils.isConvicted(player, "ban")) {
					return;
				}
			}
			
			if (isMuted()) {
				PlayerUtils.isConvicted(player, "mute");
			}
			if(PlayerSessionManager.getSession(player) != null) {
				if(getMainRank() != null && getMainRank().joinMsg != null) {
					for(Player all : Bukkit.getOnlinePlayers()) {
						String message = PlayerListener.getHoverMessage(all, player);
						
						StringUtils.sendClickMessage(all, getMainRank().joinMsg.replace("<PLAYER>", player.getName()).replace("&", ChatColor.COLOR_CHAR + ""), message, (message.contains("profile: ") ? "/search stop" : "/search " + player.getName()));
					}
				}
				
				PlayerSessionManager.getSession(player).hasBeenLoaded = true;
			}
			
			if(player.getOpenInventory().getTopInventory() != null) {
				if(player.getOpenInventory().getTopInventory().getTitle().contains("Friends")) {
					FriendUtils.openFriendInventory(player, 0);
					
				} else if(player.getOpenInventory().getTopInventory().getTitle().contains("Requests")) {
					FriendUtils.openFriendInventory(player, 1);
				}
			}
			
			if (PlayerSessionManager.playerAttachment.containsKey(player)) {
				player.removeAttachment(PlayerSessionManager.playerAttachment.get(player));
				PlayerSessionManager.playerAttachment.remove(player);
			}
			
			PermissionAttachment attachment = player.addAttachment(MasterPlugin.getMasterPlugin());
			PlayerSessionManager.playerAttachment.put(player, attachment);
			for (Rank all : getRanks()) {
				for (String permission : all.perms) {
					attachment.setPermission(permission, true);
				}
			}
			
			ExpUtils.startAnimateExpBar(player, getExp());
			
			if (GameManager.currentGame.gamePlugin.getName().contains("Hub")) {
				if (PlayerSessionManager.getSession(player).created) {
					StringUtils.sendTitle(player, 10, 60, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "SwingSword", ChatColor.WHITE + "Version: Alpha 1.01");
					com.swingsword.ssengine.game.games.hub.utils.PlayerUtils.loadPlayerInventory(player);
					if (PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("fly") != null) {
						boolean flying = PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("fly") != null && PlayerSessionManager.getSession(player).getAccount().profileSettings.getSetting("fly").equals("1");
						player.setAllowFlight(flying);
					}

					ScoreboardUtils.registerObjective(player, "(" + PlayerSessionManager.getSession(player).getAccount().getExp() + "/" + ExpUtils.expNeeded(ExpUtils.getLevel(PlayerSessionManager.getSession(player).getAccount().getExp()) + 1) + ")", DisplaySlot.BELOW_NAME);
					Objective o = player.getScoreboard().getObjective(DisplaySlot.BELOW_NAME);
					o.getScore(player.getName()).setScore(ExpUtils.getLevel(getExp()));

					for (Player all : Bukkit.getOnlinePlayers()) {
						if (all.getScoreboard() != null && all.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) != null) {
							o.getScore(all.getName()).setScore(ExpUtils.getLevel(PlayerSessionManager.getSession(all).getAccount().getExp()));
							if (all.getScoreboard().getObjective(DisplaySlot.BELOW_NAME) != null) {
								all.getScoreboard().getObjective(DisplaySlot.BELOW_NAME).getScore(player.getName()).setScore(ExpUtils.getLevel(getExp()));
							}
						}
					}
				}
			}
		}
	}

	public boolean isLoaded() {
		return loaded;
	}
	
	public HashMap<String, Object> getCache() {
		return cache;
	}
	
	public ItemStack toItemStack(Player viewer, List<String> extraLore) {
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		
		meta.setDisplayName(ChatColor.AQUA + LanguageUtils.translate(viewer,  "Details"));
		meta.setOwner(name);
		
		if(isLoaded()) {
			List<String> lore = new ArrayList<String>();
			
			lore.addAll(Arrays.asList(ChatColor.GRAY + LanguageUtils.translate(viewer, "Ranks") + ": " + getRanksDisplay(), ChatColor.GRAY + LanguageUtils.translate(viewer, "Credits") + ": " + ChatColor.YELLOW + cache.get("credits"), ChatColor.GRAY + LanguageUtils.translate(viewer, "Level") + ": " + ChatColor.GOLD + ExpUtils.getLevel(getExp()) + ChatColor.GRAY + " (" + ChatColor.GREEN + ExpUtils.getCurrentExp(getExp()) + ChatColor.GRAY + "/" + ChatColor.GREEN + ExpUtils.expNeeded(ExpUtils.getLevel(getExp()) + 1) + ChatColor.GRAY + ")", ""));
			if(profileSettings.getSetting("sex") != null) {
				lore.add(ChatColor.GRAY + LanguageUtils.translate(viewer, "Sex") + ": " + ChatColor.WHITE + StringUtils.formatSex(profileSettings.getSetting("sex")));
			}
			if(profileSettings.getSetting("lang") != null) {
				lore.add(ChatColor.GRAY + LanguageUtils.translate(viewer, "Language") + ": " + ChatColor.WHITE + profileSettings.getSetting("lang"));
			}
			
			SimpleDateFormat f = new SimpleDateFormat("h:mm a");
			if(profileSettings.getSetting("timezone") != null && !profileSettings.getSetting("timezone").equals("")) {
				f.setTimeZone(TimeZone.getTimeZone(profileSettings.getSetting("timezone")));
			} else {
				f.setTimeZone(TimeZone.getTimeZone("Europe/London"));
			}
		    Calendar cal = Calendar.getInstance();
		    if(profileSettings.getSetting("timezone") != null && !profileSettings.getSetting("timezone").equals("")) {
		    	lore.add(ChatColor.GRAY + LanguageUtils.translate(viewer, "Location") + ": " + ChatColor.WHITE + profileSettings.getSetting("loc") + " " + f.format(cal.getTime().getTime()));
		    } else if(profileSettings.getSetting("longitude") != null) {
		    	lore.add(ChatColor.GRAY + LanguageUtils.translate(viewer, "Location") + ": " + ChatColor.WHITE + profileSettings.getSetting("loc") + " " + f.format(cal.getTime().getTime() + (GeolocationUtils.getGMT(Float.parseFloat(profileSettings.getSetting("longitude"))) * 3600 * 1000)));
		    }
		    
			if(extraLore != null) {
				for(String all : extraLore) {
					lore.add(all);
				}
			}
			
			meta.setLore(lore);
			
		} else {
			meta.setLore(Arrays.asList(ChatColor.GRAY + LanguageUtils.translate(viewer, "Loading") + "..."));
		}
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public Rank getMainRank() {
		Rank main = null;
		for (Rank all : getRanks()) {
			if (all.staff) {
				main = all;
				break;
			} else {
				if (all.altRank != null && profileSettings.getSetting("sex").equals("f")) {
					main = all.getAltRank();
				} else {
					main = all;
				}
			}
		}
		return main;
	}
	
	public void addScoreboard(String key, String values) {
		HashMap<String, String> sb = StringUtils.stringToMap((String) cache.get("sb"));
		sb.put(key, values);
		
		cache.put("sb", StringUtils.mapToString(sb));
	}
	
	public List<Rank> getRanks() {
		List<Rank> ranks = new ArrayList<>();
		
		for(String rank : StringUtils.stringToList((String) cache.get("ranks"))) {
			if(RankManager.getRank(rank) != null) {
				ranks.add(RankManager.getRank(rank));
			}
		}
		return ranks;
	}
	
	public void addCredits(final int amount) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				SQLManager.getSQL("global").setValue(uuid, "credits", (Integer.parseInt((String) cache.get("credits")) + amount) + "");
				
				if(Bukkit.getOfflinePlayer(uuid).isOnline()) {
					StatManager.addStat(Bukkit.getPlayer(uuid), "g_total_credits", amount);
				}
			}
		});
	}
	
	public void addGamerscore(final int amount) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				SQLManager.getSQL("global").setValue(uuid, "gamerscore", (Integer.parseInt((String) cache.get("gamerscore")) + amount) + "");
			}
		});
	}
	
	public Language getLanguage() {
		if (profileSettings.getSetting("lang") != null) {
			if (LanguageManager.getLanguage(profileSettings.getSetting("lang")) != null) {
				return LanguageManager.getLanguage(profileSettings.getSetting("lang"));
			}
		}
		return LanguageManager.getLanguage("english");
	}
	
	public int getExp() {
		if(cache.containsKey("exp")) {
			return Integer.parseInt((String) cache.get("exp"));
		} else {
			return 0;
		}
	}
	
	public HashMap<UUID, Integer> getFriends() {
		return FriendUtils.deformat((String) cache.get("friends"));
	}
	
	public HashSet<UUID> getBlocks() {
		return BlockUtils.deformat((String) cache.get("blocks"));
	}
	
	public boolean isMuted() {
		if(StringUtils.stringToMap((String) cache.get("mute")).get("muted") != null && StringUtils.stringToMap((String) cache.get("mute")).get("muted").equals("1")) {
			return true;
		}
		return false;
	}
	
	public boolean isBanned() {
		if(StringUtils.stringToMap((String) cache.get("ban")).get("banned") != null && StringUtils.stringToMap((String) cache.get("ban")).get("banned").equals("1")) {
			return true;
		}
		return false;
	}
	
	public String getRanksDisplay() {
		String ranks = "";
		for (Rank all : getRanks()) {
			if (all.staff == true) {
				ranks = all.getRankDisplay() + ranks;

			} else {
				if (RankManager.getRank(all.altRank) != null && profileSettings.getSetting("sex").equals("f")) {
					ranks = ranks + all.getAltRank().getRankDisplay();
				} else {
					ranks = ranks + all.getRankDisplay();
				}
			}
		}
		return ranks;
	}
	
	public void saveSettings() {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(isLoaded()) {
					SQLManager.getSQL("global").setValue(uuid, "settings", settings.toString());
					SQLManager.getSQL("global").setValue(uuid, "profileSettings", profileSettings.toString());
				}
			}
		});
	}
	
	public void saveFriends() {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(isLoaded()) {
					SQLManager.getSQL("global").setValue(uuid, "friends", FriendUtils.format(FriendUtils.getFriends(Bukkit.getPlayer(uuid))));
				}
			}
		});
	}
	
	public void saveBlocks() {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(isLoaded()) {
					SQLManager.getSQL("global").setValue(uuid, "blocks", BlockUtils.format(BlockUtils.getBlocks(Bukkit.getPlayer(uuid))));
				}
			}
		});
	}
	
	public void saveGotBlocks() {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				if(isLoaded()) {
					SQLManager.getSQL("global").setValue(uuid, "gotBlocks", (String) getCache().get("gotBlocks"));
				}
			}
		});
	}
	
	public void saveAccount(boolean shutdown) {	
		if (!shutdown) {
			Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					if (isLoaded()) {
						cache.put("profileSettings", profileSettings.toString());
						SQLManager.getSQL("global").setValues(uuid, cache);

						PlayerSessionManager.removeSession((String) cache.get("name"));
					}
				}
			});
			return;
		}

		if (isLoaded()) {
			cache.put("profileSettings", profileSettings.toString());
			SQLManager.getSQL("global").setValues(uuid, cache);

			PlayerSessionManager.removeSession((String) cache.get("name"));
		}
	}
	
	//Global
	
	public static HashMap<String, Object> getGlobalDefaults(HashMap<String, String> map) {
		HashMap<String, Object> entries = new HashMap<>();
		
		entries.put("ranks", "");
		entries.put("sb", "");
		entries.put("settings", "111000");
		entries.put("credits", "0");
		entries.put("exp", "0");
		entries.put("gamerscore", "0");
		entries.put("friends", "");
		entries.put("lastOnline", "");
		entries.put("blocks", "");
		entries.put("gotBlocks", "");
		entries.put("inv", "");
		entries.put("inbox", "");
		entries.put("ban", "");//
		entries.put("mute", "");//
		entries.put("created", Calendar.getInstance().getTime().getTime() + "");
		
		String profileSettings = "loc: ;/1:2;/2:2;/3:2;/msg:1;/vb:1;/am:1";
		for (String key : map.keySet()) {
			profileSettings += ";/" + key + ":" + map.get(key);
		}
		entries.put("profileSettings", profileSettings);
		return entries;
	}
	
	//Games
	
	public static HashMap<String, Object> getGamesDefaults() {
		HashMap<String, Object> entries = new HashMap<String, Object>();
		
		entries.put("stats", "");
		
		return entries;
	}
}
