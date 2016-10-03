package com.swingsword.ssengine.game.games.minestrike.game;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.game.games.minestrike.listeners.EntityDamageByEntity;
import com.swingsword.ssengine.game.games.minestrike.listeners.PlayerDeath;
import com.swingsword.ssengine.game.games.minestrike.utils.ChatUtils;
import com.swingsword.ssengine.game.team.Team;
import com.swingsword.ssengine.stats.StatManager;
import com.swingsword.ssengine.utils.ItemUtils;

public class DeathManager {

	public static ArrayList<String> dead = new ArrayList<String>();
	
	public static void die(Player player) {
		Player killer = player.getKiller();
		
		StatManager.addStat(player, "cs_deaths", 1);
		if (CSGOGame.Deaths.get(player.getName()) != null) {
			CSGOGame.Deaths.put(player.getName(), CSGOGame.Deaths.get(player.getName()) + 1);
		}
		
		if(killer != null) {
			StatManager.addStat(killer, "cs_kills", 1);
			if (CSGOGame.Kills.get(player.getName()) != null) {
				CSGOGame.Kills.put(killer.getName(), CSGOGame.Kills.get(killer.getName()) + 1);
			}
		}
		
		dropSlot(player, 0);
		dropSlot(player, 1);
		dropSlot(player, 7);

		player.setHealth(20f);
		setSpectate(player, true);
		
		String title = "";
		String sub = "";
		if(EntityDamageByEntity.playerDamageSet.get(player) != null && EntityDamageByEntity.playerDamageSet.get(player).containsKey(killer)) {
			title = ChatColor.GRAY + "Damage taken: " + ChatColor.WHITE + ((int) EntityDamageByEntity.getTotal(EntityDamageByEntity.playerDamageSet.get(player).get(killer)) * 5) + ChatColor.GRAY + " in " + ChatColor.WHITE + EntityDamageByEntity.playerDamageSet.get(player).get(killer).size() + " hit/s" + ChatColor.GRAY + " from " + killer.getName();
		}
		if(EntityDamageByEntity.playerDamageSet.get(killer) != null && EntityDamageByEntity.playerDamageSet.get(killer).containsKey(player)) {
			sub = ChatColor.GRAY + "Damage given: " + ChatColor.WHITE + ((int) EntityDamageByEntity.getTotal(EntityDamageByEntity.playerDamageSet.get(killer).get(player)) * 5) + ChatColor.GRAY + " in " + ChatColor.WHITE + EntityDamageByEntity.playerDamageSet.get(killer).get(player).size() + " hit/s" + ChatColor.GRAY + " to " + killer.getName();
		}
		
		ChatUtils.sendTitle(player, 10, 100, 10, title, sub);
		
		PlayerDeath.checkWin();
	}
	
	@SuppressWarnings("deprecation")
	public static void reset() {
		ArrayList<String> remove = new ArrayList<String>();
		for(String all : dead) {
			remove.add(all);
			
			if(Bukkit.getOfflinePlayer(all).isOnline()) {
				Player player = Bukkit.getPlayer(all);
				
				setSpectate(player, false);
			}
		}
		for(String all : remove) {
			dead.remove(all);
		}
	}
	
	public static void setSpectate(Player player, boolean spectate) {
		player.getInventory().clear();
		
		player.setFallDistance(-10f);
		
		player.setAllowFlight(spectate);
		player.setFlying(spectate);
		
		if(spectate) {
			dead.add(player.getName());
			
			player.getInventory().setItem(0, CustomItems.spectate);
			player.getInventory().setItem(8, CustomItems.backToLobby);
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.hidePlayer(player);
			}
			
		} else {			
			player.getInventory().clear();
			
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.showPlayer(player);
			}
		}
	}
	
	public static Inventory getSpectateInventory() {
		Inventory inv = Bukkit.createInventory(null, 27, "Spectate");
		
		inv.setItem(0, ItemUtils.itemStackFromString("i=377;n=&cTerrorists"));
		inv.setItem(18, ItemUtils.itemStackFromString("i=369;n=&3Counter-Terrorists"));

		for(Player all : Team.getTeam("T").getPlayers()) {
			if(!DeathManager.dead.contains(all.getName())) {
				ItemStack item = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, all.getDisplayName(), null);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setOwner(all.getName());
				item.setItemMeta(meta);
				
				inv.setItem(getFirstFreeFrom(inv, 2), item);
			}
		}
		for(Player all : Team.getTeam("CT").getPlayers()) {
			if(!DeathManager.dead.contains(all.getName())) {
				ItemStack item = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, all.getDisplayName(), null);
				SkullMeta meta = (SkullMeta) item.getItemMeta();
				meta.setOwner(all.getName());
				item.setItemMeta(meta);
				
				inv.setItem(getFirstFreeFrom(inv, 20), item);
			}
		}
		
		return inv;
	}
	
	public static int getFirstFreeFrom(Inventory inv, int slot) {
		for(int x = slot; x < inv.getSize(); x++) {
			if(inv.getItem(x) == null) {
				return x;
			}
		}
		return -1;
	}
	
	public static void dropSlot(Player player, int slot) {
		if(player.getInventory().getItem(slot) != null && player.getInventory().getItem(slot).getType() != Material.AIR) {
			ItemStack item = player.getInventory().getItem(slot).clone();
			item.setAmount(1);
			
			player.getWorld().dropItem(player.getEyeLocation(), item);
		}
	}
}
