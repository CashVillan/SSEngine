package com.swingsword.ssengine.game.games.rust.utils;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;

public class PropUtils {
	
	public static ArrayList<Player> healing = new ArrayList<Player>();
	public static HashMap<String, Integer> playerFood = new HashMap<String, Integer>();
	
	public static void healPlayer(final Player player, final int healsLeft) {
		if (healing.contains(player)) {
			if (player.getHealth() < player.getMaxHealth()) {
				if (player.getHealth() + 2d > player.getMaxHealth()) {
					player.setHealth(player.getMaxHealth());
					player.sendMessage(ChatColor.GRAY + "You're done healing yourself.");
					healing.remove(player);
				} else {
					player.setHealth(player.getHealth() + 2d);

					if (healsLeft > 0) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
							public void run() {
								if (player.isOnline()) {
									healPlayer(player, healsLeft - 1);
								}
							}
						}, 60L);
					} else {
						player.sendMessage(ChatColor.GRAY + "Your medkit is empty.");
						healing.remove(player);
					}
				}
			} else {
				player.sendMessage(ChatColor.GRAY + "You're done healing yourself.");
				healing.remove(player);
			}
		}
	}
	
	public static void eat(Player player, int food) {
		if (playerFood.get(player.getName()) < 3000) {
			if (player.getItemInHand().getAmount() > 1) {
				player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);

			} else {
				player.setItemInHand(null);
			}
			player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EAT, 1, 1);

			if (playerFood.get(player.getName()) + food < 3000) {
				playerFood.put(player.getName(), playerFood.get(player.getName()) + food);
			} else {
				playerFood.put(player.getName(), 3000);
			}

		} else {
			player.sendMessage(ChatColor.RED + "You are not hungry.");
		}
	}
}
