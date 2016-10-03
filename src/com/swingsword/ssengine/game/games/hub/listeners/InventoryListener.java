package com.swingsword.ssengine.game.games.hub.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import com.swingsword.ssengine.game.games.hub.utils.HubScoreboard;
import com.swingsword.ssengine.game.games.hub.utils.SelectorUtils;
import com.swingsword.ssengine.player.PlayerSessionManager;
import com.swingsword.ssengine.server.ServerManager;
import com.swingsword.ssengine.utils.IntegerUtils;
import com.swingsword.ssengine.utils.InventoryUtils;
import com.swingsword.ssengine.utils.ItemUtils;
import com.swingsword.ssengine.utils.LanguageUtils;
import com.swingsword.ssengine.utils.ProfileUtils;
import com.swingsword.ssengine.utils.ScoreboardUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class InventoryListener implements Listener {
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(player.getItemInHand() != null && player.getItemInHand().getItemMeta() != null && player.getItemInHand().getItemMeta().getDisplayName() != null) {
			if (event.hasItem()) {
				if (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.AQUA + LanguageUtils.translate(player, "Your Profile"))) {
					event.setCancelled(true);
					ProfileUtils.openProfileMenu(player);
				} else if (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.AQUA + "/settings")) {
					event.setCancelled(true);
					ProfileUtils.openProfileSettingsMenu(player);
				} else if (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.AQUA + LanguageUtils.translate(player, "Games Menu"))) {
					event.setCancelled(true);
					SelectorUtils.openGamesMenu(player);
				} else if (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.AQUA + LanguageUtils.translate(player, "Hub Selector"))) {
					event.setCancelled(true);
					player.openInventory(ServerManager.getJoinInventory(player, "Hub"));
				} else if (player.getItemInHand().getItemMeta().getDisplayName().contains(ChatColor.AQUA + "/scoreboard")) {
					event.setCancelled(true);
					player.openInventory(HubScoreboard.getScoreboardInv(player));
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryDragEvent(InventoryDragEvent event) {
		if (event.getInventory().getType() != InventoryType.CREATIVE)
			event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) {
		if (event.getSource().getType() != InventoryType.CREATIVE && event.getDestination().getType() != InventoryType.CREATIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void inventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		//scoreboard
		
		if(event.getClickedInventory() == null) {
			return;
		}
		
		if (ChatColor.stripColor(event.getClickedInventory().getTitle()).toLowerCase().contains("scoreboard")) {
			event.setCancelled(true);

			if (ChatColor.stripColor(event.getClickedInventory().getTitle()).toLowerCase().equals("scoreboard")) {
				if (event.getRawSlot() >= 11 && event.getRawSlot() <= 15) {
					int option = event.getRawSlot() - 11;

					if (event.getAction().name().contains("ALL")) {
						Inventory inv = Bukkit.createInventory(null, 27, "Scoreboard - Change option " + (option + 1));
						for (int x = 9; x < 18; x++) {
							inv.setItem(x, ItemUtils.createItem(Material.PAPER, 1, (byte) 0,
									ChatColor.WHITE + "" + ChatColor.BOLD + HubScoreboard.getOption(x - 9), null));
						}
						inv.setItem(22, ItemUtils.createItem(Material.REDSTONE_COMPARATOR, 1, (byte) 0, ChatColor.GRAY + "Cancel", null));

						player.openInventory(inv);

					} else {
						Inventory inv = Bukkit.createInventory(null, 27, "Scoreboard - Change color for " + (option + 1));

						for (int x = 0; x <= 15; x++) {
							if (IntegerUtils.wooltoChatColor((byte) x) != ChatColor.WHITE || x == 0) {
								inv.setItem(
										InventoryUtils.getFirstFreeFrom(inv, 9),
										ItemUtils.createItem(Material.WOOL, 1, (byte) x, IntegerUtils.wooltoChatColor((byte) x) + "" + ChatColor.BOLD + IntegerUtils.wooltoChatColor((byte) x).name().replace("_", " "), null));
							}
						}
						inv.setItem(26, ItemUtils.createItem(Material.REDSTONE_COMPARATOR, 1, (byte) 0, ChatColor.GRAY + "Cancel", null));

						player.openInventory(inv);
					}
				}

			} else {
				if (event.getCurrentItem().getType() == Material.REDSTONE_COMPARATOR) {
					player.openInventory(HubScoreboard.getScoreboardInv(player));
				}

				if (event.getCurrentItem().getItemMeta().getDisplayName() != null
						&& HubScoreboard.getChar(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) != null) {
					if (!event.getClickedInventory().getTitle().contains("color")) {
						int slot = Integer.parseInt(ChatColor.stripColor(event.getClickedInventory().getTitle()).toCharArray()[ChatColor.stripColor(event.getClickedInventory().getTitle()).length() - 1] + "") - 1;
						String color = HubScoreboard.getOption(player, slot).toCharArray()[1] + "";

						HashMap<String, String> sb = StringUtils.stringToMap((String) PlayerSessionManager.getSession(player).getAccount().getCache().get("sb"));
						
						String newLayout = HubScoreboard.changeLayout(sb.get("hub"),
								HubScoreboard.getChar(ChatColor.stripColor(HubScoreboard.getOption(player, slot).split(":")[0])),
								HubScoreboard.getChar(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())), color);
						
						//ScoreboardUtils.createNewScoreboard(player);
						ScoreboardUtils.load(player);
						
						sb.put("hub", newLayout);
						
						PlayerSessionManager.getSession(player).getAccount().getCache().put("sb", StringUtils.mapToString(sb));
						player.openInventory(HubScoreboard.getScoreboardInv(player));
					}

				} else if (event.getCurrentItem().getItemMeta().getDisplayName() != null) {
					if (event.getClickedInventory().getTitle().contains("color")) {
						int slot = Integer.parseInt(ChatColor.stripColor(event.getClickedInventory().getTitle()).toCharArray()[ChatColor.stripColor(event.getClickedInventory().getTitle()).length() - 1] + "") - 1;

						HashMap<String, String> sb = StringUtils.stringToMap((String) PlayerSessionManager.getSession(player).getAccount().getCache().get("sb"));

						@SuppressWarnings("deprecation")
						String newLayout = HubScoreboard.changeLayout(sb.get("hub"), HubScoreboard.getChar(ChatColor.stripColor(HubScoreboard.getOption(player, slot).split(":")[0])), HubScoreboard.getChar(ChatColor.stripColor(HubScoreboard.getOption(player, slot).split(":")[0])), IntegerUtils.wooltoChatColor(event.getCurrentItem().getData().getData()).getChar() + "");
						ScoreboardUtils.load(player);

						sb.put("hub", newLayout);

						PlayerSessionManager.getSession(player).getAccount().getCache().put("sb", StringUtils.mapToString(sb));
						player.openInventory(HubScoreboard.getScoreboardInv(player));
					}
				}
			}
			
		} else if(event.getClickedInventory().getTitle().toLowerCase().contains("games menu")) {
			event.setCancelled(true);
			
			if(event.getCurrentItem() != null) {
				
			}
		}
	}
}
