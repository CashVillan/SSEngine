package com.swingsword.ssengine.game.games.rust.utils;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class PlayerUtils {
	
	public static boolean isHuman(Entity ent) {
		List<EntityType> humans = Arrays.asList(EntityType.CREEPER, EntityType.PIG_ZOMBIE, EntityType.PLAYER, EntityType.SKELETON, EntityType.SNOWMAN, EntityType.VILLAGER, EntityType.WITCH, EntityType.ZOMBIE);
		
		return humans.contains(ent.getType());
	}
	
	public boolean playerNear(Player player, int radius) {
		for (Entity all : player.getNearbyEntities(radius, radius, radius)) {
			if (all instanceof Player) {
				return true;
			}
		}
		return false;
	}

	public boolean playerNear(Chunk chunk) {
		for (Player all : Bukkit.getOnlinePlayers()) {
			for (int x = -Bukkit.getViewDistance(); x <= Bukkit.getViewDistance(); x++) {
				for (int z = -Bukkit.getViewDistance(); z <= Bukkit.getViewDistance(); z++) {
					if (!Bukkit.getWorld("world").getChunkAt(all.getLocation().clone().getChunk().getX() + x, all.getLocation().clone().getChunk().getZ() + z).equals(chunk)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static String getPlayerDirection(Player player) {
		String dir = "";
		float y = player.getLocation().getYaw();
		if (y < 0) {
			y += 360;
		}
		y %= 360;
		float i = (float) ((y + 8) / 90);

		if (i == 0) {
			dir = "S";
		} else if (i > 0.5 && i <= 1.5) {
			dir = "W";
		} else if (i > 1.5 && i <= 2.5) {
			dir = "N";
		} else if (i > 2.5 && i <= 3.5) {
			dir = "E";
		} else {
			dir = "S";
		}

		return dir;
	}
}
