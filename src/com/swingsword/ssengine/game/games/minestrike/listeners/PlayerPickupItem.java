package com.swingsword.ssengine.game.games.minestrike.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.swingsword.ssengine.game.games.minestrike.game.DeathManager;
import com.swingsword.ssengine.game.games.minestrike.guns.Ammo;
import com.swingsword.ssengine.game.games.minestrike.guns.Gun;
import com.swingsword.ssengine.game.games.minestrike.guns.GunType;
import com.swingsword.ssengine.game.games.minestrike.utils.ChatUtils;
import com.swingsword.ssengine.game.team.Team;

public class PlayerPickupItem implements Listener {

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		
		if(DeathManager.dead.contains(player.getName())) {
			event.setCancelled(true);
			return;
		}
		
		if(event.getItem().getItemStack().getType() == Material.TNT) {
			event.setCancelled(true);
			
			if(Team.hasTeam(player) && Team.getTeam(player).getName().equals("T")) {
				event.getItem().remove();
				player.getInventory().setItem(7, event.getItem().getItemStack());
				
				player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
				ChatUtils.sendTitle(player, 2, 10, 2, ChatColor.RED + "Alert", "You picked up the bomb");
			}
		}
		
		if(Gun.isGun(event.getItem().getItemStack())) {
			event.setCancelled(true);
			
			Gun gun = Gun.getGun(event.getItem().getItemStack().getItemMeta().getDisplayName());
			
			if(Ammo.getAmmoLeft(player, gun, event.getItem().getItemStack()) - 1 <= 127) {
				event.getItem().getItemStack().setAmount(Ammo.getAmmoLeft(player, gun, event.getItem().getItemStack()));
			}
			
			if(gun.getGunType() == GunType.PRIMARY && player.getInventory().getItem(0) == null) {
				event.getItem().remove();
				player.getInventory().setItem(0, event.getItem().getItemStack());
				
			} else if(gun.getGunType() == GunType.SECONDARY && player.getInventory().getItem(1) == null) {
				event.getItem().remove();
				player.getInventory().setItem(1, event.getItem().getItemStack());
			}
		}
	}
}
