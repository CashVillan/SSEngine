package com.swingsword.ssengine.game.games.minestrike.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.swingsword.ssengine.game.GameState;
import com.swingsword.ssengine.game.games.minestrike.Minestrike;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.games.minestrike.game.DeathManager;
import com.swingsword.ssengine.utils.ItemUtils;

public class PlayerJoin implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player player = e.getPlayer();
		
		Minestrike.resetInv(player);
		
		if(GameState.isState(GameState.IN_LOBBY)) {
			player.getInventory().setItem(8, ItemUtils.itemStackFromString("i=345;n=&bBack to Lobby"));
			
		} else {
			CSGOGame.loadPlayer(player);
			
			DeathManager.setSpectate(player, true);
		}
		
		CSGOGame.Deaths.put(e.getPlayer().getName(), 0);
		CSGOGame.Kills.put(e.getPlayer().getName(), 0);
		
		player.setResourcePack("https://www.dropbox.com/s/hu5fhkxd6td8r77/cs.zip?dl=1");
	}
}
