package com.swingsword.ssengine.game.games.minestrike.guns;

import org.bukkit.Material;

public class SkinnedGun extends Gun {

	String skinName;
	
	public SkinnedGun(String name, Material material, int id, String skinName) {
		super(name, material, id, Gun.getGun(name).getSelectionSlot(), Gun.getGun(name).getTeam());
		
		this.skinName = skinName;
	}

	public String getSkin() {
		return skinName;
	}
}
