package com.swingsword.ssengine.options;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OptionHandler implements Listener {

	public static HashMap<String, OptionInventory> playerOptionInv = new HashMap<String, OptionInventory>();
	
	public static void loadOptionInventory(Player player, OptionInventory inv) {
		playerOptionInv.put(player.getName(), inv);
	}
	
	public static OptionInventory getOptionInventory(String player) {
		return playerOptionInv.get(player);
	}
	
	public static void removeOptionInventory(String player) {
		playerOptionInv.remove(player);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		
		if(OptionHandler.getOptionInventory(player.getName()) != null && OptionHandler.getOptionInventory(player.getName()).inventory.equals(player.getOpenInventory().getTopInventory())) {
			event.setCancelled(true);
			
			int slot = event.getRawSlot();
			
			if(slot == 0 || slot == 1 || slot == 2 || slot == 9 || slot == 10 || slot == 11 || slot == 18 || slot == 19 || slot == 20) {
				OptionHandler.getOptionInventory(player.getName()).onDeny.run();
				
			} else if(slot == 6 || slot == 7 || slot == 8 || slot == 15 || slot == 16 || slot == 17 || slot == 24 || slot == 25 || slot == 26) {
				OptionHandler.getOptionInventory(player.getName()).onAccept.run();
			}
			
			return;
		}
	}
}
