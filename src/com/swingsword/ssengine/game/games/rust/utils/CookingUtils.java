package com.swingsword.ssengine.game.games.rust.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.utils.ConfigUtils;

public class CookingUtils {

	public static HashMap<Inventory, Integer> invDelay = new HashMap<Inventory, Integer>();
	public static HashMap<String, Inventory> fireInventory = new HashMap<String, Inventory>();
	public static HashMap<String, Inventory> furnaceInventory = new HashMap<String, Inventory>();
	
	public static ItemStack sulfur = new ItemStack(Material.GLOWSTONE_DUST, 4);
	public static ItemStack metal = new ItemStack(Material.FLINT, 7);
	public static ItemStack leather = new ItemStack(Material.LEATHER, 1);
	public static ItemStack charcoal = new ItemStack(Material.COAL, 5, (byte) 1);
	
	public static void loadCooks() {
		ItemMeta mm = metal.getItemMeta();
		mm.setDisplayName(ChatColor.WHITE + "Metal Fragments");
		metal.setItemMeta(mm);

		ItemMeta sm = sulfur.getItemMeta();
		sm.setDisplayName(ChatColor.WHITE + "Sulfur");
		sulfur.setItemMeta(sm);

		ItemMeta lm = leather.getItemMeta();
		lm.setDisplayName(ChatColor.WHITE + "Leather");
		leather.setItemMeta(lm);

		ItemMeta cm = charcoal.getItemMeta();
		cm.setDisplayName(ChatColor.WHITE + "Charcoal");
		charcoal.setItemMeta(cm);
		
		FileConfiguration cookLocs = ConfigUtils.getConfig("data/cookLocs");
		
		if (!cookLocs.contains("fires")) {
			cookLocs.createSection("fires");
			ConfigUtils.saveConfig(cookLocs, "data/cookLocs");
		}
		if (!cookLocs.contains("furnaces")) {
			cookLocs.createSection("furnaces");
			ConfigUtils.saveConfig(cookLocs, "data/cookLocs");
		}

		for (String all : cookLocs.getConfigurationSection("fires").getKeys(false)) {
			Inventory inv = CookingUtils.getCampfireInv();

			CookingUtils.fireInventory.put(new SimpleLocation(LocationUtils.RealLocationFromString(all).add(0, 1, 0)).toString(), inv);
			invDelay.put(inv, 1440);
		}

		for (String all : cookLocs.getConfigurationSection("furnaces").getKeys(false)) {
			Inventory inv = CookingUtils.getFurnaceInv();

			CookingUtils.furnaceInventory.put(new SimpleLocation(LocationUtils.RealLocationFromString(all).add(0, 1, 0)).toString(), inv);
			invDelay.put(inv, 1440);
		}
		passMinute();
	}
	
	public static Inventory getCampfireInv() {
		Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "Campfire");

		ItemStack toggle = new ItemStack(Material.INK_SACK);
		toggle.setDurability((short) 6);
		ItemMeta togglem = toggle.getItemMeta();
		togglem.setDisplayName(ChatColor.RED + "Off");
		toggle.setItemMeta(togglem);
		inv.setItem(13, toggle);

		ItemStack blue = new ItemStack(Material.INK_SACK);
		blue.setDurability((short) 4);
		ItemMeta bluem = blue.getItemMeta();
		bluem.setDisplayName(ChatColor.BLACK + " ");
		blue.setItemMeta(bluem);

		ItemStack brown = new ItemStack(Material.INK_SACK);
		brown.setDurability((short) 3);
		ItemMeta brownm = brown.getItemMeta();
		brownm.setDisplayName(ChatColor.BLACK + " ");
		brown.setItemMeta(brownm);

		ItemStack orange = new ItemStack(Material.INK_SACK);
		orange.setDurability((short) 14);
		ItemMeta orangem = orange.getItemMeta();
		orangem.setDisplayName(ChatColor.BLACK + " ");
		orange.setItemMeta(orangem);

		ItemStack green = new ItemStack(Material.INK_SACK);
		green.setDurability((short) 10);
		ItemMeta greenm = green.getItemMeta();
		greenm.setDisplayName(ChatColor.BLACK + " ");
		green.setItemMeta(greenm);

		ItemStack pink = new ItemStack(Material.INK_SACK);
		pink.setDurability((short) 9);
		ItemMeta pinkm = pink.getItemMeta();
		pinkm.setDisplayName(ChatColor.BLACK + " ");
		pink.setItemMeta(pinkm);

		ItemStack food = new ItemStack(Material.RAW_CHICKEN);
		food.setDurability((short) 15);
		ItemMeta foodm = food.getItemMeta();
		foodm.setDisplayName(ChatColor.RESET + "Raw food goes here.");
		food.setItemMeta(foodm);
		inv.setItem(3, food);

		ItemStack cooked = new ItemStack(Material.COOKED_CHICKEN);
		cooked.setDurability((short) 15);
		ItemMeta cookedm = cooked.getItemMeta();
		cookedm.setDisplayName(ChatColor.RESET + "Cooked food goes here.");
		cooked.setItemMeta(cookedm);
		inv.setItem(21, cooked);

		ItemStack coal = new ItemStack(Material.COAL);
		coal.setDurability((short) 2);
		ItemMeta coalm = coal.getItemMeta();
		coalm.setDisplayName(ChatColor.RESET + "Charcoal goes here.");
		coal.setItemMeta(coalm);
		inv.setItem(5, coal);

		ItemStack fuel = new ItemStack(Material.SLIME_BALL);
		ItemMeta fuelm = fuel.getItemMeta();
		fuelm.setDisplayName(ChatColor.RESET + "Fuel goes here.");
		fuelm.setLore(Arrays.asList(ChatColor.GRAY + "Fuel consists of:", ChatColor.GRAY + "Wood", ChatColor.GRAY + "Wood Planks", ChatColor.GRAY + "Low Grade Fuel"));
		fuel.setItemMeta(fuelm);
		inv.setItem(23, fuel);

		int[] blues = { 9, 10, 11, 15, 16, 17 };
		for (int all : blues) {
			inv.setItem(all, blue);
		}

		inv.setItem(4, orange);
		inv.setItem(12, brown);
		inv.setItem(14, green);
		inv.setItem(22, pink);

		return inv;
	}

	public static Inventory getFurnaceInv() {
		Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST, "Furnace");

		ItemStack toggle = new ItemStack(Material.INK_SACK);
		toggle.setDurability((short) 6);
		ItemMeta togglem = toggle.getItemMeta();
		togglem.setDisplayName(ChatColor.RED + "Off");
		toggle.setItemMeta(togglem);
		inv.setItem(13, toggle);

		ItemStack blue = new ItemStack(Material.INK_SACK);
		blue.setDurability((short) 4);
		ItemMeta bluem = blue.getItemMeta();
		bluem.setDisplayName(ChatColor.BLACK + " ");
		blue.setItemMeta(bluem);

		ItemStack brown = new ItemStack(Material.INK_SACK);
		brown.setDurability((short) 3);
		ItemMeta brownm = brown.getItemMeta();
		brownm.setDisplayName(ChatColor.BLACK + " ");
		brown.setItemMeta(brownm);

		ItemStack orange = new ItemStack(Material.INK_SACK);
		orange.setDurability((short) 14);
		ItemMeta orangem = orange.getItemMeta();
		orangem.setDisplayName(ChatColor.BLACK + " ");
		orange.setItemMeta(orangem);

		ItemStack green = new ItemStack(Material.INK_SACK);
		green.setDurability((short) 10);
		ItemMeta greenm = green.getItemMeta();
		greenm.setDisplayName(ChatColor.BLACK + " ");
		green.setItemMeta(greenm);

		ItemStack pink = new ItemStack(Material.INK_SACK);
		pink.setDurability((short) 9);
		ItemMeta pinkm = pink.getItemMeta();
		pinkm.setDisplayName(ChatColor.BLACK + " ");
		pink.setItemMeta(pinkm);

		ItemStack food = new ItemStack(Material.SPECKLED_MELON);
		food.setDurability((short) 15);
		ItemMeta foodm = food.getItemMeta();
		foodm.setDisplayName(ChatColor.RESET + "Smeltable items go here.");
		food.setItemMeta(foodm);
		inv.setItem(3, food);

		ItemStack cooked = new ItemStack(Material.FLINT);
		cooked.setDurability((short) 15);
		ItemMeta cookedm = cooked.getItemMeta();
		cookedm.setDisplayName(ChatColor.RESET + "Smelted items go here.");
		cooked.setItemMeta(cookedm);
		inv.setItem(21, cooked);

		ItemStack coal = new ItemStack(Material.COAL);
		coal.setDurability((short) 2);
		ItemMeta coalm = coal.getItemMeta();
		coalm.setDisplayName(ChatColor.RESET + "Charcoal goes here.");
		coal.setItemMeta(coalm);
		inv.setItem(5, coal);

		ItemStack fuel = new ItemStack(Material.SLIME_BALL);
		ItemMeta fuelm = fuel.getItemMeta();
		fuelm.setDisplayName(ChatColor.RESET + "Fuel goes here.");
		fuelm.setLore(Arrays.asList(ChatColor.GRAY + "Fuel consists of:", ChatColor.GRAY + "Wood", ChatColor.GRAY + "Wood Planks", ChatColor.GRAY + "Low Grade Fuel"));
		fuel.setItemMeta(fuelm);
		inv.setItem(23, fuel);

		int[] blues = { 9, 10, 11, 15, 16, 17 };
		for (int all : blues) {
			inv.setItem(all, blue);
		}

		inv.setItem(4, orange);
		inv.setItem(12, brown);
		inv.setItem(14, green);
		inv.setItem(22, pink);

		return inv;
	}

	public static SimpleLocation getFireInvLoc(Inventory inv) {
		SimpleLocation loc = null;

		for (String all : fireInventory.keySet()) {
			if (fireInventory.get(all).equals(inv)) {
				loc = new SimpleLocation(all);
				break;
			}
		}

		return loc;
	}

	public static SimpleLocation getFurnaceInvLoc(Inventory inv) {
		SimpleLocation loc = null;

		for (String all : furnaceInventory.keySet()) {
			if (furnaceInventory.get(all).equals(inv)) {
				loc = new SimpleLocation(all);
				break;
			}
		}

		return loc;
	}

	public static boolean containsFireFuel(Inventory inv) {
		boolean contains = false;

		if (fireInventory.containsValue(inv)) {
			for (int all : new int[] { 24, 25, 26 }) {
				if (inv.getItem(all) != null) {
					if (inv.getItem(all).getType() == Material.STICK || inv.getItem(all).getType() == Material.SLIME_BALL || inv.getItem(all).getType() == Material.COAL) {
						contains = true;
					}
				}
			}
		}

		return contains;
	}

	public static boolean containsFurnaceFuel(Inventory inv) {
		boolean contains = false;

		if (furnaceInventory.containsValue(inv)) {
			for (int all : new int[] { 24, 25, 26 }) {
				if (inv.getItem(all) != null) {
					if (inv.getItem(all).getType() == Material.STICK || inv.getItem(all).getType() == Material.SLIME_BALL || inv.getItem(all).getType() == Material.COAL) {
						contains = true;
					}
				}
			}
		}

		return contains;
	}

	public static boolean isFull(Inventory inv) {
		boolean full = false;

		for (int all : new int[] { 18, 19, 20 }) {
			if (inv.getItem(all) != null) {
				if (inv.getItem(all).getAmount() >= 64) {
					inv.getItem(all).setAmount(64);

					full = true;
				}
			}
		}
		return full;
	}
	
	@SuppressWarnings("deprecation")
	public static void passMinute() {
		ItemStack toggle = new ItemStack(Material.INK_SACK);
		toggle.setDurability((short) 6);
		ItemMeta togglem = toggle.getItemMeta();
		togglem.setDisplayName(ChatColor.RED + "Off");
		toggle.setItemMeta(togglem);

		// CAMPFIRE
		for (Inventory all : fireInventory.values()) {
			boolean contains = false;
			boolean isFull = isFull(all);

			for (ItemStack items : all.getContents()) {
				if (items != null) {
					if (items.getData().getData() == 13) {
						contains = true;
					}
				}
			}

			if (contains) {
				// FUEL
				if (all.getItem(24) != null) {
					if (all.getItem(24).getAmount() - 1 <= 0) {
						all.setItem(24, null);
					} else {
						all.getItem(24).setAmount(all.getItem(24).getAmount() - 1);
					}

				} else if (all.getItem(25) != null) {
					if (all.getItem(25).getAmount() - 1 <= 0) {
						all.setItem(25, null);
					} else {
						all.getItem(25).setAmount(all.getItem(25).getAmount() - 1);
					}

				} else if (all.getItem(26) != null) {
					if (all.getItem(26).getAmount() - 1 <= 0) {
						all.setItem(26, null);
					} else {
						all.getItem(26).setAmount(all.getItem(26).getAmount() - 1);
					}
				}
			}

			if (contains && !isFull) {
				// CHARCOAL
				if (all.getItem(6) != null && all.getItem(6).getAmount() + 5 <= 64) {
					ItemStack coal = all.getItem(6);
					coal.setAmount(coal.getAmount() + 5);
					all.setItem(6, coal);

				} else if (all.getItem(7) != null && all.getItem(7).getAmount() + 5 <= 64) {
					ItemStack coal = all.getItem(7);
					coal.setAmount(coal.getAmount() + 5);
					all.setItem(7, coal);

				} else if (all.getItem(8) != null && all.getItem(8).getAmount() + 5 <= 64) {
					ItemStack coal = all.getItem(8);
					coal.setAmount(coal.getAmount() + 5);
					all.setItem(8, coal);

				} else {
					if (all.getItem(6) == null) {
						all.setItem(6, charcoal);

					} else if (all.getItem(7) == null) {
						all.setItem(7, charcoal);

					} else if (all.getItem(8) == null) {
						all.setItem(8, charcoal);
					}
				}

				// COOKING
				for (int x = 0; x < 3; x++) {
					if (all.getItem(x + 18) != null && all.getItem(x) != null) {
						if (all.getItem(x).getType() == Material.RAW_CHICKEN && all.getItem(x + 18).getType() == Material.COOKED_CHICKEN) {
							all.getItem(x + 18).setAmount(all.getItem(x + 18).getAmount() + 1);
						}

					} else if (all.getItem(x) != null) {
						if (all.getItem(x).getType() == Material.RAW_CHICKEN) {
							all.setItem(x + 18, new ItemStack(Material.COOKED_CHICKEN));
						}
					}

					if (all.getItem(x) != null && all.getItem(x + 18) != null) {
						if (all.getItem(x + 18).getAmount() < all.getItem(x + 18).getType().getMaxStackSize()) {
							if (all.getItem(x).getAmount() - 1 > 0) {
								all.getItem(x).setAmount(all.getItem(x).getAmount() - 1);
							} else {
								all.setItem(x, new ItemStack(Material.AIR));
							}
						}
					}
				}

			} else if (isFull) {
				all.setItem(13, toggle);
			}

			if (!containsFireFuel(all)) {
				// getFireInvLoc(all).getBlock().setType(Material.STEP);
				// getFireInvLoc(all).getBlock().setData((byte) 0);
				// TODO
			}
		}

		// FURNACE
		for (Inventory all : furnaceInventory.values()) {
			boolean contains = false;
			boolean isFull = isFull(all);

			for (ItemStack items : all.getContents()) {
				if (items != null) {
					if (items.getData().getData() == 13) {
						contains = true;
					}
				}
			}

			if (contains) {
				// FUEL
				if (all.getItem(24) != null) {
					if (all.getItem(24).getAmount() - 1 <= 0) {
						all.setItem(24, null);
					} else {
						all.getItem(24).setAmount(all.getItem(24).getAmount() - 1);
					}

				} else if (all.getItem(25) != null) {
					if (all.getItem(25).getAmount() - 1 <= 0) {
						all.setItem(25, null);
					} else {
						all.getItem(25).setAmount(all.getItem(25).getAmount() - 1);
					}

				} else if (all.getItem(26) != null) {
					if (all.getItem(26).getAmount() - 1 <= 0) {
						all.setItem(26, null);
					} else {
						all.getItem(26).setAmount(all.getItem(26).getAmount() - 1);
					}
				}
			}

			if (contains && !isFull) {
				// CHARCOAL
				if (all.getItem(6) != null && all.getItem(6).getAmount() + 5 <= 64) {
					ItemStack coal = all.getItem(6);
					coal.setAmount(coal.getAmount() + 5);
					all.setItem(6, coal);

				} else if (all.getItem(7) != null && all.getItem(7).getAmount() + 5 <= 64) {
					ItemStack coal = all.getItem(7);
					coal.setAmount(coal.getAmount() + 5);
					all.setItem(7, coal);

				} else if (all.getItem(8) != null && all.getItem(8).getAmount() + 5 <= 64) {
					ItemStack coal = all.getItem(8);
					coal.setAmount(coal.getAmount() + 5);
					all.setItem(8, coal);

				} else {
					if (all.getItem(6) == null) {
						all.setItem(6, charcoal);

					} else if (all.getItem(7) == null) {
						all.setItem(7, charcoal);

					} else if (all.getItem(8) == null) {
						all.setItem(8, charcoal);
					}
				}

				// COOKING
				for (int x = 0; x < 3; x++) {
					if (all.getItem(x + 18) != null && all.getItem(x) != null) {
						if (all.getItem(x).getType() == Material.FERMENTED_SPIDER_EYE && all.getItem(x + 18).getType() == Material.FLINT) {
							all.getItem(x + 18).setAmount(all.getItem(x + 18).getAmount() + 7);

						} else if (all.getItem(x).getType() == Material.SPECKLED_MELON && all.getItem(x + 18).getType() == Material.GLOWSTONE_DUST) {
							all.getItem(x + 18).setAmount(all.getItem(x + 18).getAmount() + 4);

						} else if (all.getItem(x).getType() == Material.EMPTY_MAP && all.getItem(x + 18).getType() == Material.LEATHER) {
							all.getItem(x + 18).setAmount(all.getItem(x + 18).getAmount() + 1);
						}

					} else if (all.getItem(x) != null) {
						if (all.getItem(x).getType() == Material.FERMENTED_SPIDER_EYE) {
							all.setItem(x + 18, metal);

						} else if (all.getItem(x).getType() == Material.SPECKLED_MELON) {
							all.setItem(x + 18, sulfur);

						} else if (all.getItem(x).getType() == Material.EMPTY_MAP) {
							all.setItem(x + 18, leather);
						}
					}

					if (all.getItem(x) != null && all.getItem(x + 18) != null) {
						if (all.getItem(x + 18).getAmount() < all.getItem(x + 18).getType().getMaxStackSize()) {
							if (all.getItem(x).getAmount() - 1 > 0) {
								all.getItem(x).setAmount(all.getItem(x).getAmount() - 1);
							} else {
								all.setItem(x, new ItemStack(Material.AIR));
							}
						}
					}
				}

			} else if (isFull) {
				all.setItem(13, toggle);
			}

			if (!containsFurnaceFuel(all)) {
				if (contains) {
					getFurnaceInvLoc(all).getBlock().setData((byte) (getFurnaceInvLoc(all).getBlock().getData() - 4));
				}
			}
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				passMinute();
			}
		}, 60 * 20);
	}
	
	public static boolean canMove(Inventory inv, int slot) {
		List<Integer> moveable = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 6, 7, 8, 18, 19, 20, 24, 25, 26));

		if (inv.getItem(slot) != null) {
			return moveable.contains(slot) && inv.getItem(slot).getType() != Material.INK_SACK;
		} else {
			return moveable.contains(slot);
		}
	}

	@SuppressWarnings("deprecation")
	public static int findSlot(Player player, Inventory inv, ItemStack i, String type) {
		List<Material> cookable = new ArrayList<Material>(Arrays.asList(Material.RAW_CHICKEN));
		List<Material> smeltable = new ArrayList<Material>(Arrays.asList(Material.SPECKLED_MELON, Material.FERMENTED_SPIDER_EYE, Material.EMPTY_MAP));
		List<Material> fuel = new ArrayList<Material>(Arrays.asList(Material.STICK, Material.SLIME_BALL, Material.COAL));

		if (type.equalsIgnoreCase("campfire")) {
			smeltable.clear();
		}

		if (type.equalsIgnoreCase("furnace")) {
			cookable.clear();
		}

		if (cookable.contains(i.getType())) {
			for (int x = 0; x <= 2; x++) {
				if (inv.getItem(x) == null) {
					return x;
				}
			}

		} else if (smeltable.contains(i.getType())) {
			for (int x = 0; x <= 2; x++) {
				if (inv.getItem(x) == null) {
					return x;
				}
			}

		} else if (fuel.contains(i.getType()) && i.getData().getData() == 0 || fuel.contains(i.getType()) && i.getType() != Material.COAL) {
			for (int x = 24; x <= 26; x++) {
				if (inv.getItem(x) == null) {
					return x;
				}
			}
		}
		return -1;
	}
}
