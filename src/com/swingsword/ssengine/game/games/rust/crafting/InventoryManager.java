package com.swingsword.ssengine.game.games.rust.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.games.rust.utils.ItemUtils;
import com.swingsword.ssengine.utils.ConfigUtils;

public class InventoryManager {
	
	public static Inventory craftInv = null;
	public static ItemStack back = new ItemStack(Material.ARROW);
	
	public static ArrayList<ItemStack> sections = new ArrayList<>();
	public static HashMap<ItemStack, ItemStack> itemResult = new HashMap<>();
	public static HashMap<ItemStack, ArrayList<ItemStack>> itemNeeded = new HashMap<ItemStack, ArrayList<ItemStack>>();
	public static HashMap<ItemStack, Integer> itemDelay = new HashMap<>();
	public static HashMap<ItemStack, String> itemPermission = new HashMap<>();
	public static HashMap<ItemStack, Boolean> itemWorkbench = new HashMap<>();

	public static Inventory getSection(Player player, ItemStack stack, String menu) {
		Inventory inv = Bukkit.createInventory(null, 27, "Crafting - " + menu.replace("_", " "));
		String id = stack.getTypeId() + "";
		menu = menu.replace(" ", "_");

		FileConfiguration mapConfig = GameManager.currentMap.getMapConfig();
		if (ChatColor.stripColor(menu).contains("Miscellaenous")) {
			id = stack.getTypeId() + ":12";
		}
		for (String craft : mapConfig.getConfigurationSection("crafting.menu." + id + ";" + menu).getKeys(false)) {
			int itemId2 = Integer.parseInt(craft.split(";")[0].split(":")[0]);
			byte itemData2 = 0;

			if (craft.split(";")[0].split(":").length == 2) {
				itemData2 = Byte.parseByte(craft.split(";")[0].split(":")[1]);
			}

			String name2 = ChatColor.WHITE + "" + ChatColor.BOLD + craft.split(";")[1].split(":")[0].replace("_", " ");
			ItemStack item2 = new ItemStack(Material.getMaterial(itemId2), 1, itemData2);
			ItemMeta meta2 = item2.getItemMeta();
			meta2.setDisplayName(name2);

			int ritemId = Integer.parseInt(mapConfig.getString("crafting.menu." + id + ";" + menu + "." + craft + ".result").split(";")[0].split(":")[0]);
			int ramount = Integer.parseInt(mapConfig.getString("crafting.menu." + id + ";" + menu + "." + craft + ".result").split(";")[1]);
			String rname = mapConfig.getString("crafting.menu." + id + ";" + menu + "." + craft + ".result").split(";")[2].replace("&", ChatColor.COLOR_CHAR + "").replace("_", " ");
			ItemStack ritem = new ItemStack(Material.getMaterial(ritemId), ramount, itemData2);
			ItemMeta rmeta = ritem.getItemMeta();
			rmeta.setDisplayName(rname);
			ritem.setItemMeta(rmeta);

			ArrayList<ItemStack> neededItems = new ArrayList<ItemStack>();
			List<String> neededLore = new ArrayList<String>();
			neededLore.add(ChatColor.GRAY + "Needed:");

			for (String needed : mapConfig.getStringList("crafting.menu." + id + ";" + menu + "." + craft + ".needed")) {
				int nitemId = Integer.parseInt(needed.split(";")[0].split(":")[0]);
				byte nitemData = 0;

				if (needed.split(";")[0].split(":").length == 2) {
					nitemData = Byte.parseByte(needed.split(";")[0].split(":")[1]);
				}

				int namount = Integer.parseInt(needed.split(";")[1]);
				String nname = null;
				if (needed.split(";").length == 3) {
					nname = needed.split(";")[2].replace("&", ChatColor.COLOR_CHAR + "").replace("_", " ");
				}
				ItemStack nitem = new ItemStack(Material.getMaterial(nitemId), namount, nitemData);
				ItemMeta nmeta = nitem.getItemMeta();
				if (nname != null) {
					nmeta.setDisplayName(nname);
				}
				nitem.setItemMeta(nmeta);

				neededItems.add(nitem);

				if (nname != null) {
					for (String all : mapConfig.getString("crafting.neededLayout").replace("&", ChatColor.COLOR_CHAR + "").replace("<ITEM>", nname).replace("<AMOUNT>", namount + "").split(";")) {
						neededLore.add(all);
					}
				} else {
					for (String all : mapConfig.getString("crafting.neededLayout").replace("&", ChatColor.COLOR_CHAR + "").replace("<ITEM>", nitem.getType().name().toLowerCase()).replace("<AMOUNT>", namount + "").split(";")) {
						neededLore.add(all);
					}
				}
			}

			if (neededLore.size() == 1) {
				neededLore.add(ChatColor.GRAY + "Nothing");
			}

			for (String all : mapConfig.getStringList("crafting.menu." + id + ";" + menu + "." + craft + ".lore")) {
				if (!ChatColor.stripColor(all).contains("Workbench")) {
					if (all.contains("seconds")) {
						if (player.hasPermission("ss.rust.halfcraft")) {
							all = ChatColor.GRAY + "Takes " + all.split(" ")[1] + ChatColor.GOLD + "/2" + ChatColor.GRAY + " seconds to craft each";
						}
					}
					neededLore.add(all.replace("&", ChatColor.COLOR_CHAR + ""));
				}
			}

			neededLore.add("");

			ArrayList<String> flaws = new ArrayList<>();

			if (!mapConfig.contains("crafting.menu." + id + ";" + menu + "." + craft + ".permission") || Crafting.hasResearched(player.getName(), mapConfig.getString("crafting.menu." + id + ";" + menu + "." + craft + ".permission"))) {
				ItemStack item = null;

				for (ItemStack all : InventoryManager.itemResult.keySet()) {
					if (all.getItemMeta().getDisplayName().equals(name2)) {
						item = all;
					}
				}

				if (item != null) {
					for (ItemStack all : InventoryManager.itemNeeded.get(item)) {
						if (!ItemUtils.containsItem(player, all.clone(), all.clone().getAmount())) {
							flaws.add(ChatColor.RED + "You dont have needed items!");
						}
					}

					if (mapConfig.contains("crafting.menu." + id + ";" + menu + "." + craft + ".workbench")) {
						boolean workbenchClose = false;

						for (int x = -2; x < 3; x++) {
							for (int y = -2; y < 3; y++) {
								for (int z = -2; z < 3; z++) {
									if (player.getLocation().getBlock().getRelative(x, y, z).getType() == Material.STONE_SLAB2) {
										workbenchClose = true;
									}
								}
							}
						}

						if (!workbenchClose) {
							flaws.add(ChatColor.RED + "You are not near a workbench.");
						}
					}
				}
			} else {
				flaws.add(ChatColor.RED + "You dont have this researched!");
			}

			if (flaws.isEmpty()) {
				flaws.add(ChatColor.YELLOW + "Click to craft");
			}

			for (String flaw : flaws) {
				neededLore.add(flaw);
			}

			meta2.setLore(neededLore);
			item2.setItemMeta(meta2);
			inv.addItem(item2);
		}
		inv.setItem(26, back);

		return inv;
	}

	@SuppressWarnings("deprecation")
	public static void loadInventory() {
		craftInv = Bukkit.createInventory(null, 27, "Crafting");
		
		ItemMeta backMeta = back.getItemMeta();
		backMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Back");
		back.setItemMeta(backMeta);
		FileConfiguration mapConfig = GameManager.currentMap.getMapConfig();
		
		if(mapConfig.contains("crafting.menu")) {
			for (String menu : mapConfig.getConfigurationSection("crafting.menu").getKeys(false)) {
				int itemId = Integer.parseInt(menu.split(";")[0].split(":")[0]);
				byte itemData = 0;

				if (mapConfig.getString("crafting.menu." + menu).split(";")[0].split(":").length == 2) {
					itemData = Byte.parseByte(menu.split(";")[0].split(":")[1]);
				}

				String name = ChatColor.WHITE + "" + ChatColor.BOLD + menu.split(";")[1].replace("_", " ");;
				ItemStack item = new ItemStack(Material.getMaterial(itemId), 1, itemData);
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(name);
				item.setItemMeta(meta);

				craftInv.addItem(item);

				for (String craft : mapConfig.getConfigurationSection("crafting.menu." + menu).getKeys(false)) {
					if (!craft.equals("id")) {
						int itemId2 = Integer.parseInt(craft.split(";")[0].split(":")[0]);
						byte itemData2 = 0;

						if (craft.split(";")[0].split(":").length == 2) {
							itemData2 = Byte.parseByte(craft.split(";")[0].split(":")[1]);
						}

						String name2 = ChatColor.WHITE + "" + ChatColor.BOLD + craft.split(";")[1].split(":")[0].replace("&", ChatColor.COLOR_CHAR + "").replace("_", " ");
						ItemStack item2 = new ItemStack(Material.getMaterial(itemId2), 1, itemData2);
						ItemMeta meta2 = item2.getItemMeta();
						meta2.setDisplayName(name2);

						int ritemId = Integer.parseInt(mapConfig.getString("crafting.menu." + menu + "." + craft + ".result").split(";")[0].split(":")[0]);
						int ramount = Integer.parseInt(mapConfig.getString("crafting.menu." + menu + "." + craft + ".result").split(";")[1]);
						String rname = mapConfig.getString("crafting.menu." + menu + "." + craft + ".result").split(";")[2].replace("&", ChatColor.COLOR_CHAR + "").replace("_", " ");
						ItemStack ritem = new ItemStack(Material.getMaterial(ritemId), ramount, itemData2);
						ItemMeta rmeta = ritem.getItemMeta();
						rmeta.setDisplayName(rname);
						ritem.setItemMeta(rmeta);

						ArrayList<ItemStack> neededItems = new ArrayList<ItemStack>();
						List<String> neededLore = new ArrayList<String>();
						neededLore.add(ChatColor.GRAY + "Needed:");

						for (String needed : mapConfig.getStringList("crafting.menu." + menu + "." + craft + ".needed")) {
							int nitemId = Integer.parseInt(needed.split(";")[0].split(":")[0]);
							byte nitemData = 0;

							if (needed.split(";")[0].split(":").length == 2) {
								nitemData = Byte.parseByte(needed.split(";")[0].split(":")[1]);
							}

							int namount = Integer.parseInt(needed.split(";")[1]);
							String nname = null;
							if (needed.split(";").length == 3) {
								nname = needed.split(";")[2].replace("&", ChatColor.COLOR_CHAR + "").replace("_", " ");
							}
							ItemStack nitem = new ItemStack(Material.getMaterial(nitemId), namount, nitemData);
							ItemMeta nmeta = nitem.getItemMeta();
							if (nname != null) {
								nmeta.setDisplayName(nname);
							}
							nitem.setItemMeta(nmeta);

							neededItems.add(nitem);

							if (nname != null) {
								for (String all : mapConfig.getString("crafting.neededLayout").replace("&", ChatColor.COLOR_CHAR + "").replace("<ITEM>", nname).replace("<AMOUNT>", namount + "").split(";")) {
									neededLore.add(all);
								}
							} else {
								for (String all : mapConfig.getString("crafting.neededLayout").replace("&", ChatColor.COLOR_CHAR + "").replace("<ITEM>", nitem.getType().name().toLowerCase()).replace("<AMOUNT>", namount + "").split(";")) {
									neededLore.add(all);
								}
							}
						}

						if (neededLore.size() == 1) {
							neededLore.add(ChatColor.GRAY + "Nothing");
						}

						for (String all : mapConfig.getStringList("crafting.menu." + menu + "." + craft + ".lore")) {
							neededLore.add(all.replace("&", ChatColor.COLOR_CHAR + ""));
						}

						meta2.setLore(neededLore);
						item2.setItemMeta(meta2);

						itemResult.put(item2, ritem);
						itemNeeded.put(item2, neededItems);
						if (mapConfig.getString("crafting.menu." + menu + "." + craft + ".permission") != null) {
							itemPermission.put(item2, mapConfig.getString("crafting.menu." + menu + "." + craft + ".permission"));
						}
						if (mapConfig.getString("crafting.menu." + menu + "." + craft + ".workbench") != null) {
							itemWorkbench.put(item2, mapConfig.getBoolean("crafting.menu." + menu + "." + craft + ".workbench"));
						}
						itemDelay.put(ritem, mapConfig.getInt("crafting.menu." + menu + "." + craft + ".craftDelay"));
					}
					
					sections.add(item);
				}
			}
		}
	}
}
