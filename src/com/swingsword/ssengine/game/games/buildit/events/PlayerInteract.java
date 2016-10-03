package com.swingsword.ssengine.game.games.buildit.events;

import java.util.Arrays;
import java.util.HashSet;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.swingsword.ssengine.game.games.buildit.BuildArea;
import com.swingsword.ssengine.utils.ItemUtils;

public class PlayerInteract implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(BuildArea.buildAreaInReview == null) {
			if(event.getAction().name().contains("RIGHT")) {
				if(BuildArea.getArea(player) != null) {
					if(getTargetBlock(player) != null && !BuildArea.getArea(player).region.contains(BukkitUtil.toVector(getTargetBlock(player).getLocation().add(0.5, 1, 0.5)))) {
						event.setCancelled(true);
						
						player.sendMessage(ChatColor.RED + "You can't build outside your area!");
					}
					
				} else {
					event.setCancelled(true);
				}
			}
			
		} else {
			event.setCancelled(true);
			
			if(event.getAction().name().contains("RIGHT")) {
				if(player.getInventory().getHeldItemSlot() <= 5 && player.getItemInHand().getType() != Material.AIR) {
					BuildArea.buildAreaInReview.rating += player.getInventory().getHeldItemSlot();
					
					player.playSound(player.getLocation(), Sound.ENTITY_CAT_PURREOW, (float) (player.getInventory().getHeldItemSlot() + 1f) / 3f, 1);
					player.sendMessage(ChatColor.GREEN + "You voted: " + ItemUtils.getDisplayName(player.getItemInHand()));
					
					for(ItemStack item : player.getInventory().getContents()) {
						if(item != null) {
							item.setType(Material.AIR);
						}
					}
					player.getInventory().clear();
					player.updateInventory();
				}
			}
		}
	}
	
	public static Block getTargetBlock(Player player) {
		return player.getTargetBlock(new HashSet<Material>(Arrays.asList(Material.AIR)), 6);
	}
}
