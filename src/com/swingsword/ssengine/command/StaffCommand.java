package com.swingsword.ssengine.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.utils.ItemUtils;

public class StaffCommand implements CommandExecutor, Listener {

	static ArrayList<String> vanish = new ArrayList<>();
	static ArrayList<String> fly = new ArrayList<>();
	static ArrayList<String> ncp = new ArrayList<>();

	static HashMap<String, ItemStack[]> invs = new HashMap<>();
	static HashMap<String, Location> locs = new HashMap<>();
	public static ArrayList<String> toggled = new ArrayList<>();

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("staff")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("ss.staff")) {
					Player player = (Player) sender;
					if (toggled.contains(player.getName())) {
						disable(player);
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Staff mode disabled!");
					} else {
						enable(player);
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Staff mode enabled!");
					}
					return true;
				}
				sender.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "No Permission.");
			}
		}
		return false;
	}

	public static void enable(Player player) {
		invs.put(player.getName(), player.getInventory().getContents());
		locs.put(player.getName(), player.getLocation());
		toggled.add(player.getName());

		player.getInventory().clear();
		giveItems(player);
	}

	public static void disable(Player player) {
		if (invs.containsKey(player.getName())) {
			player.getInventory().clear();
			for (ItemStack all : invs.get(player.getName())) {
				if (all != null) {
					player.getInventory().addItem(all);
				}
			}
			invs.remove(player.getName());
		}
		if (locs.containsKey(player.getName())) {
			player.teleport(locs.get(player.getName()));
			locs.remove(player.getName());
		}
		
		if (fly.contains(player.getName())) {
			player.setAllowFlight(false);
			player.setFlying(false);
			fly.remove(player.getName());
		}

		if (vanish.contains(player.getName())) {
			for (Player all : Bukkit.getOnlinePlayers()) {
				all.showPlayer(player);
			}
			vanish.remove(player.getName());
		}
		
		ncp.remove(player.getName());
		toggled.remove(player.getName());
	}

	static void giveItems(Player player) {
		player.getInventory().setItem(2, ItemUtils.itemStackFromString("i=30;n=&7Vanish"));
		player.getInventory().setItem(3, ItemUtils.itemStackFromString("i=340;n=&fTeleport"));
		player.getInventory().setItem(4, ItemUtils.itemStackFromString("i=138;n=&7Fly"));
		player.getInventory().setItem(5, ItemUtils.itemStackFromString("i=54;n=&fInvsee"));
		player.getInventory().setItem(6, ItemUtils.itemStackFromString("i=339;n=&7NCP Notifications"));
	}

	static Inventory getTeleportMenu() {
		Inventory inv = Bukkit.createInventory(null, 54, "Staff - Teleport");
		for (Player all : Bukkit.getOnlinePlayers()) {
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
			SkullMeta meta = (SkullMeta) item.getItemMeta();

			meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + all.getName());
			meta.setOwner(all.getName());
			meta.setLore(Arrays.asList("", ChatColor.YELLOW + "Click to teleport to " + all.getName() + "."));

			item.setItemMeta(meta);
			inv.addItem(item);
		}

		return inv;
	}

	static Inventory getInvseeMenu() {
		Inventory inv = Bukkit.createInventory(null, 54, "Staff - Invsee");
		for (Player all : Bukkit.getOnlinePlayers()) {
			ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);
			SkullMeta meta = (SkullMeta) item.getItemMeta();

			meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + all.getName());
			meta.setOwner(all.getName());
			meta.setLore(Arrays.asList("", ChatColor.YELLOW + "Click to see " + all.getName() + "'s Inventory."));

			item.setItemMeta(meta);
			inv.addItem(item);
		}

		return inv;
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		if (toggled.contains(player.getName())) {
			if (event.getCurrentItem().getTypeId() != 0) {
				if (event.getClickedInventory().getTitle().equals("Inventory")) {
					event.setCancelled(true);
				} else if (event.getClickedInventory().getTitle().endsWith("Teleport")) {
					if (Bukkit.getPlayer(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) != null) {
						event.setCancelled(true);
						player.teleport(Bukkit.getPlayer(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())).getLocation());
						player.closeInventory();
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "Teleported to " + Bukkit.getPlayer(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())).getName() + ".");
					}
				} else if (event.getClickedInventory().getTitle().endsWith("Invsee")) {
					if (Bukkit.getPlayer(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) != null) {
						event.setCancelled(true);
						player.closeInventory();
						player.openInventory(Bukkit.getPlayer(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())).getInventory());
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (toggled.contains(player.getName())) {
			if (ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).contains("Vanish")) {
				if (vanish.contains(player.getName())) {
					player.getInventory().setItem(2, ItemUtils.itemStackFromString("i=30;n=&7Vanish"));

					for (Player all : Bukkit.getOnlinePlayers()) {
						all.showPlayer(player);
					}

					vanish.remove(player.getName());
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "You're no longer hidden.");
				} else {
					player.getInventory().setItem(2, ItemUtils.itemStackFromString("i=30;n=&aVanish"));

					for (Player all : Bukkit.getOnlinePlayers()) {
						all.hidePlayer(player);
					}

					vanish.add(player.getName());
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "You're now hidden.");
				}
			} else if (ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).contains("Teleport")) {
				player.openInventory(getTeleportMenu());
			} else if (ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).contains("Fly")) {
				if (fly.contains(player.getName())) {
					player.getInventory().setItem(4, ItemUtils.itemStackFromString("i=138;n=&7Fly"));

					player.setAllowFlight(false);
					player.setFlying(false);

					fly.remove(player.getName());
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "You're no longer flying.");
				} else {
					player.getInventory().setItem(4, ItemUtils.itemStackFromString("i=138;n=&aFly"));

					player.setAllowFlight(true);
					player.setFlying(true);

					fly.add(player.getName());
					player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.AQUA + "You're now flying.");
				}
			} else if (ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).contains("Invsee")) {
				player.openInventory(getInvseeMenu());
			} else if (ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName()).contains("NCP Notifications")) {
				if (ncp.contains(player.getName())) {
					player.getInventory().setItem(6, ItemUtils.itemStackFromString("i=339;n=&7NCP Notifications"));
					ncp.remove(player.getName());
				} else {
					player.getInventory().setItem(6, ItemUtils.itemStackFromString("i=339;n=&aNCP Notifications"));
					ncp.add(player.getName());
				}
			}
		}
	}
}
