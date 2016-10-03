package com.swingsword.ssengine.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.language.Language;
import com.swingsword.ssengine.language.LanguageManager;
import com.swingsword.ssengine.utils.ItemUtils;
import com.swingsword.ssengine.utils.LanguageUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class AccountCreationManager implements Listener {

	public static HashMap<Player, HashMap<String, String>> createSettings = new HashMap<>();
	public static List<String> progressNames = Arrays.asList("Choose a Language", "Choose a Sex", "Account Confirmation");
	public static List<String> changingInv = new ArrayList<String>();
	
	public static void initCreation(Player player) {
		HashMap<String, String> settings = new HashMap<>();
		settings.put("sex", "m");
		settings.put("lang", "English");
		
		createSettings.put(player, settings);
		player.openInventory(getOptionInv(player, 0));
	}
	
	public static void completeCreation(final Player player) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				SQLManager.getSQL("global").createAccount(player.getUniqueId(), player.getName(), PlayerAccount.getGlobalDefaults(createSettings.get(player)));
				SQLManager.getSQL("games").createAccount(player.getUniqueId(), player.getName(), PlayerAccount.getGamesDefaults());
				
				PlayerSessionManager.getSession(player).getAccount().updateCache();
				
				createSettings.remove(player);
				changingInv.remove(player.getName());
				PlayerSessionManager.getSession(player).created = true;
				
				player.closeInventory();
				StringUtils.sendTitle(player, 10, 60, 5, ChatColor.AQUA + "" + ChatColor.BOLD + "SwingSword", ChatColor.WHITE + "Version: Alpha 1.01");
			}
		});
	}
	
	//Progress behaviour
	
	public static Inventory getOptionInv(Player player, int progress) {
		Language lang = LanguageManager.getLanguage(createSettings.get(player).get("lang"));
		Inventory inv = Bukkit.createInventory(null, 27, LanguageUtils.translateLanguage(lang, getProgressTitle(progress)));
		
		switch (progress) {
		case 0:
			addContinue(player, inv);

			ItemStack language = ItemUtils.createItem(Material.PAPER, 1, (byte) 0, ChatColor.AQUA + LanguageUtils.translateLanguage(lang, "Language"), Arrays.asList(ChatColor.GRAY + LanguageUtils.translateLanguage(lang, "Current") + ": " + ChatColor.WHITE + createSettings.get(player).get("lang"), "", ChatColor.YELLOW + LanguageUtils.translateLanguage(lang, "Click to change your Language")));

			inv.setItem(13, language);

			break;
		case 1:
			addContinue(player, inv);
			addPrevious(player, inv);

			ItemStack sex = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.AQUA + LanguageUtils.translateLanguage(lang, "Sex"), Arrays.asList(ChatColor.GRAY + LanguageUtils.translateLanguage(lang, "Current") + ": " + ChatColor.WHITE + LanguageUtils.translateLanguage(lang, StringUtils.formatSex(createSettings.get(player).get("sex"))), "", ChatColor.YELLOW + LanguageUtils.translateLanguage(lang, "Click to change your Sex")));
			SkullMeta sexMeta = (SkullMeta) sex.getItemMeta();
			if (createSettings.get(player).get("sex").equals("f")) {
				sexMeta.setOwner("MHF_Alex");
			}
			sex.setItemMeta(sexMeta);

			inv.setItem(13, sex);

			break;
		case 2:
			addPrevious(player, inv);

			ItemStack playerHead = ItemUtils.createItem(Material.SKULL_ITEM, 1, (byte) 3, ChatColor.WHITE + player.getDisplayName(), Arrays.asList(ChatColor.GRAY + LanguageUtils.translateLanguage(lang, "Language") + ": " + ChatColor.WHITE + createSettings.get(player).get("lang"), ChatColor.GRAY + LanguageUtils.translateLanguage(lang, "Sex") + ": " + ChatColor.WHITE + LanguageUtils.translateLanguage(lang, StringUtils.formatSex(createSettings.get(player).get("sex"))), "", ChatColor.YELLOW + LanguageUtils.translateLanguage(lang, "Click to create your Account!")));
			SkullMeta meta = (SkullMeta) playerHead.getItemMeta();
			meta.setOwner(player.getName());
			playerHead.setItemMeta(meta);
			inv.setItem(13, playerHead);

			break;
		}

		return inv;
	}
	
	public static String getProgressTitle(int progress) {
		return progressNames.get(progress);
	}
	
	public static int getProgressId(Player player, String title) {
		Language lang = LanguageManager.getLanguage(createSettings.get(player).get("lang"));
		for(int x = 0; x < progressNames.size(); x++) {
			if(LanguageUtils.translateLanguage(lang, progressNames.get(x)).equals(LanguageUtils.translateLanguage(lang, title))) {
				return x;
			}
		}
		return -1;
	}
	
	public static int getProgressId(Player player, Inventory inv) {
		return getProgressId(player, inv.getTitle());
	}
	
	public static void refreshInv(Player player) {
		Inventory inv = player.getOpenInventory().getTopInventory();
		int id = getProgressId(player, inv);
		inv.clear();
		player.sendMessage(id + " id");
		ItemStack[] content = getOptionInv(player, id).getContents();
		for(int x = 0; x < content.length; x++) {
			if(content[x] != null) {
				inv.setItem(x, content[x]);
			}
		}
	}
	
	//Inv behaviour
	
	@EventHandler
	public void onPlayerClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Inventory inv = player.getOpenInventory().getTopInventory();
		if (createSettings.containsKey(player)) {
			Language lang = LanguageManager.getLanguage(createSettings.get(player).get("lang"));
			event.setCancelled(true);
			int progress = getProgressId(player, inv);

			if (progress != -1) {
				if (event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null && event.getCurrentItem().getItemMeta().getDisplayName() != null) {
					ItemStack item = event.getCurrentItem();

					if (item.getItemMeta().getDisplayName().contains(LanguageUtils.translateLanguage(lang, "Continue"))) {
						changingInv.add(player.getName());
						player.openInventory(getOptionInv(player, progress + 1));
						changingInv.remove(player.getName());
					}
					if (item.getItemMeta().getDisplayName().contains(LanguageUtils.translateLanguage(lang, "Previous"))) {
						changingInv.add(player.getName());
						player.openInventory(getOptionInv(player, progress - 1));
						changingInv.remove(player.getName());
					}

					// Setting behaviour
					if (item.getItemMeta().getDisplayName().contains(LanguageUtils.translateLanguage(lang, "Language"))) {
						createSettings.get(player).put("lang", nextLanguage(createSettings.get(player).get("lang")));

						player.closeInventory();
						player.openInventory(getOptionInv(player, progress));

					}

					if (item.getItemMeta().getDisplayName().contains(LanguageUtils.translateLanguage(lang, "Sex"))) {
						if (createSettings.get(player).get("sex").equals("f")) {
							createSettings.get(player).put("sex", "m");
						} else {
							createSettings.get(player).put("sex", "f");
						}

						refreshInv(player);
					}

					if (item.getItemMeta().getDisplayName().contains(player.getName())) {
						changingInv.add(player.getName());
						completeCreation(player);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		final Player player = (Player) event.getPlayer();
		Inventory inv = player.getOpenInventory().getTopInventory();
		
		if (createSettings.containsKey(player) && progressNames.contains(inv.getTitle())) {
			final int progress = getProgressId(player, inv);

			if (PlayerSessionManager.getSession(player).created == false) {
				if (!changingInv.contains(player.getName())) {
					Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
						public void run() {
							if (player.isOnline() && player.getOpenInventory().getTopInventory().getType().equals(InventoryType.CRAFTING)) {
								player.closeInventory();
								player.openInventory(getOptionInv(player, progress));
							}
						}
					}, 20);
				}
			}
		}
	}
	
	//Inv utils
	
	public static void addContinue(Player player, Inventory inv) {
		int[] slots = { 6, 7, 8, 15, 16, 17, 24, 25, 26 };
		ItemStack item = ItemUtils.createItem(Material.EMERALD_BLOCK, 1, (byte) 0, ChatColor.GREEN + LanguageUtils.translateLanguage(LanguageManager.getLanguage(createSettings.get(player).get("lang")), "Continue"), null);
		
		for(int slot : slots) {
			inv.setItem(slot, item);
		}
	}
	
	public static void addPrevious(Player player, Inventory inv) {
		int[] slots = { 0, 1, 2, 9, 10, 11, 18, 19, 20 };
		ItemStack item = ItemUtils.createItem(Material.REDSTONE_BLOCK, 1, (byte) 0, ChatColor.RED + LanguageUtils.translateLanguage(LanguageManager.getLanguage(createSettings.get(player).get("lang")), "Previous"), null);

		for(int slot : slots) {
			inv.setItem(slot, item);
		}
	}
	
	//Other
	
	public static String nextLanguage(String lang) {
		for(int x = 0; x < LanguageManager.languages.size(); x++) {			
			if(LanguageManager.languages.get(x).getId().equalsIgnoreCase(lang)) {
				if(x == LanguageManager.languages.size() - 1) {
					return LanguageManager.languages.get(0).getId();
				} else {
					return LanguageManager.languages.get(x + 1).getId();

				}
			}
		}
		return "English";
	}
}
