package com.swingsword.ssengine.game.games.rust.utils;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;

public class EffectUtils {
	
	@SuppressWarnings("deprecation")
	public static void playFlareEffect(final Location loc, final int amountLeft) {
		if (loc != null) {
			if (loc.getBlock().getRelative(0, -1, 0).getType().isSolid() && loc.getBlock().getRelative(0, -1, 0).getType().isOccluding()) {
				for (Player all : Bukkit.getOnlinePlayers()) {
					if (all.getLocation().distance(loc) < 30) {
						all.sendBlockChange(loc.getBlock().getRelative(0, -1, 0).getLocation(), Material.GLOWSTONE, (byte) 0);
					}
					for (int x = 0; x < 10; x++) {
						loc.getWorld().playEffect(loc, Effect.LAVA_POP, 30);
					}
				}
			}

			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					if (amountLeft - 1 > 0) {
						playFlareEffect(loc, amountLeft - 1);
					}
				}
			}, 10L);
		}
	}

}
