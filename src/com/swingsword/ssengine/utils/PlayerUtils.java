package com.swingsword.ssengine.utils;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.language.Language;
import com.swingsword.ssengine.language.LanguageManager;
import com.swingsword.ssengine.player.AccountCreationManager;
import com.swingsword.ssengine.player.PlayerSessionManager;

public class PlayerUtils {
	
	public static boolean isConvicted(Player player, String type) {
		if (PlayerSessionManager.getSession(player).getAccount().isLoaded()) {
			HashMap<String, Object> cache = PlayerSessionManager.getSession(player).getAccount().getCache();
			if (type.equals("mute")) {
				if (PlayerSessionManager.getSession(player).getAccount().isMuted()) {
					if (StringUtils.stringToMap((String) cache.get("mute")).get("date") != null) {
						Date date = new Date(Long.parseLong(StringUtils.stringToMap((String) cache.get("mute")).get("date")));
						if(new Date().after(date)) {
							PlayerSessionManager.getSession(player).getAccount().getCache().put("mute", "");
							
							player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Your mute has expired.");
							return false;
						}
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "You've been muted for: " + StringUtils.stringToMap((String) cache.get("mute")).get("reason"));
						return true;
					}
				}
			}
			if (type.equals("ban")) {
				if (PlayerSessionManager.getSession(player).getAccount().isBanned()) {
					if (StringUtils.stringToMap((String) cache.get("ban")).get("date") != null) {
						Date date = new Date(Long.parseLong(StringUtils.stringToMap((String) cache.get("ban")).get("date")));
						if (new Date().after(date)) {
							PlayerSessionManager.getSession(player).getAccount().getCache().put("ban", "");
							
							player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Your ban has expired.");
							return false;
						}
					}
					player.kickPlayer(ChatColor.RED + "You've been banned for: " + StringUtils.stringToMap((String) cache.get("ban")).get("reason"));
					return true;
				}
			}
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	public static void sendTab(Player player) {
		Language lang = null;
		if (PlayerSessionManager.playerSession.containsKey(player.getName()) && PlayerSessionManager.getSession(player).getAccount().isLoaded()) {
			lang = PlayerSessionManager.getSession(player).getAccount().getLanguage();
		} else {
			if (AccountCreationManager.createSettings.containsKey(player)) {
				lang = LanguageManager.getLanguage(AccountCreationManager.createSettings.get(player.getName()).get("lang"));
			}
		}
		if (lang != null) {
			StringUtils.sendTabTitle(player, ChatColor.WHITE + "" + ChatColor.BOLD + "SwingSword Network " + ChatColor.GREEN + MasterPlugin.getServerName(), ChatColor.WHITE + LanguageUtils.translateLanguage(lang, "Goto") + " " + ChatColor.AQUA + "www.swingsword.com" + ChatColor.WHITE + " " + LanguageUtils.translateLanguage(lang, "for forums, shop, and more") + "!");
		} else {
			StringUtils.sendTabTitle(player, ChatColor.WHITE + "" + ChatColor.BOLD + "SwingSword Network " + ChatColor.GREEN + MasterPlugin.getServerName(), ChatColor.WHITE + "Goto " + ChatColor.AQUA + "www.swingsword.com" + ChatColor.WHITE + " for forums, shop, and more!");
		}
	}
}
