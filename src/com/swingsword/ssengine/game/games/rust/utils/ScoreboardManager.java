package com.swingsword.ssengine.game.games.rust.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.DisplaySlot;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.Rust;
import com.swingsword.ssengine.game.games.rust.crafting.Crafting;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.utils.ScoreboardUtils;

public class ScoreboardManager {

	@SuppressWarnings("static-access")
	public static void loadScoreboard(Player player) {
		ScoreboardUtils.registerObjective(player, com.swingsword.ssengine.scoreboard.ScoreboardManager.scoreboardDisplay, DisplaySlot.SIDEBAR);

		ArrayList<String> finalList = new ArrayList<String>();
		
		List<String> configLines = Arrays.asList("&fHealth: <HEALTH>", "&fFood: <FOOD>", "&fRads: <RAD>", "<BL>", "<RADIATION>", "<CRAFTING>", "<WORKBENCH>", "<BLEED>", "<HUNGER>");

		for (int x = configLines.size() - 1; x >= 0; x--) {
			String spaces = "&7".replace("&", ChatColor.COLOR_CHAR + "");
			for (int y = 0; y < x; y++) {
				spaces = spaces + " ";
			}

			String line = configLines.get(x).replace("&", ChatColor.COLOR_CHAR + "").replace("<PLAYER>", player.getName()).replace("<RAD>", RadiationUtils.playerRad.get(player.getName()) + "").replace("<SPACE>", spaces);

			if (player.hasPotionEffect(PotionEffectType.SLOW) && player.hasPotionEffect(PotionEffectType.JUMP)) {
				line = line.replace("<BL>", ChatColor.RED + "Broken legs");
			} else {
				line = line.replace("<BL>", ChatColor.GOLD + "   ");
			}

			if (Rust.getInstance().bleeding.contains(player.getName())) {
				line = line.replace("<BLEED>", ChatColor.DARK_RED + "Bleeding");
			} else {
				line = line.replace("<BLEED>", ChatColor.DARK_RED + "   ");
			}

			if (PropUtils.playerFood.get(player.getName()) <= 500) {
				line = line.replace("<FOOD>", ChatColor.DARK_RED + "" + PropUtils.playerFood.get(player.getName()));
				line = line.replace("<HUNGER>", ChatColor.GOLD + "HUNGER");
			} else {
				line = line.replace("<FOOD>", ChatColor.WHITE + "" + PropUtils.playerFood.get(player.getName()));
				line = line.replace("<HUNGER>", ChatColor.DARK_BLUE + "      ");
			}

			if (RadiationUtils.playerRad.get(player.getName()) >= 500) {
				line = line.replace("<RADIATION>", ChatColor.YELLOW + "RADIATION");
			} else {
				line = line.replace("<RADIATION>", ChatColor.YELLOW + "    ");
			}

			if (Crafting.playerCraftAmount.containsKey(player)) {
				line = line.replace("<CRAFTING>", ChatColor.GRAY + "CRAFTING");
			} else {
				line = line.replace("<CRAFTING>", ChatColor.GRAY + "     ");
			}

			boolean workbench = false;
			for (int x1 = -2; x1 < 3; x1++) {
				for (int y = -2; y < 3; y++) {
					for (int z = -2; z < 3; z++) {
						if (player.getLocation().getBlock().getRelative(x1, y, z).getType() == Material.STONE_SLAB2) {
							workbench = true;
						}
					}
				}
			}

			if (workbench) {
				line = line.replace("<WORKBENCH>", ChatColor.GREEN + "WORKBENCH");
			} else {
				line = line.replace("<WORKBENCH>", ChatColor.GREEN + "      ");
			}

			int health = (int) Math.round(player.getHealth() * 5);

			if (player.getHealth() * 5 > 65) {
				line = line.replace("<HEALTH>", ChatColor.GREEN + "" + health);
			} else if (player.getHealth() * 5 <= 65 && player.getHealth() * 5 > 50) {
				line = line.replace("<HEALTH>", ChatColor.YELLOW + "" + health);
			} else {
				line = line.replace("<HEALTH>", ChatColor.DARK_RED + "" + health);
			}

			if (!ChatColor.stripColor(line).replace(" ", "").equals("")) {
				finalList.add(line);
			}
		}
		
		for(int x = 0; x < finalList.size(); x++) {
			ScoreboardUtils.addScore(player, finalList.get(x), x);
		}
	}
	
	public static void loopScoreboardLoad() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (PlayerSessionManager.playerSession.containsKey(all.getName()) && PlayerSessionManager.playerSession.get(all.getName()).hasBeenLoaded && PlayerSessionManager.playerSession.get(all.getName()).created) {
						loadScoreboard(all);
					}
				}
			}
		}, 40, 40);
	}
}
