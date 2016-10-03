package com.swingsword.ssengine.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.language.Language;
import com.swingsword.ssengine.player.PlayerSessionManager;

public class LanguageUtils {

	public static String translate(Player player, String input) {
		if (PlayerSessionManager.playerSession.containsKey(player.getName())) {
			Language lang = PlayerSessionManager.getSession(player).getAccount().getLanguage();
			input = translateLanguage(lang, input);
		}
		return input;
	}
	
	public static String translateLanguage(Language lang, String input) {
		if (lang.getId() != "English") {
			
			FileConfiguration cache = ConfigUtils.getConfig("cache");
			if (cache.contains("language." + lang.getId() + "." + input) && !cache.getString("language." + lang.getId() + "." + input).equals("")) {
				input = cache.getString("language." + lang.getId() + "." + input);
			}
		}
		return input;
	}
}
