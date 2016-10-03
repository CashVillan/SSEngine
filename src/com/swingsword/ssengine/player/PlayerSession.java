package com.swingsword.ssengine.player;

import org.bukkit.entity.Player;

public class PlayerSession {

	private Player player;
	private PlayerAccount account;
	public boolean created = false;
	
	public boolean hasBeenLoaded = false;
	
	public PlayerSession(Player player) {
		this.player = player;
		
		account = new PlayerAccount(player.getUniqueId(), player.getName());
	}
	
	public PlayerAccount getAccount() {
		if(account == null) {
			account = new PlayerAccount(player.getUniqueId(), player.getName());
		}
		
		return account;
	}
}
