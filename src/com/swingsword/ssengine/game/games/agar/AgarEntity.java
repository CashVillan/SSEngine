package com.swingsword.ssengine.game.games.agar;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Entity;

public class AgarEntity {

	public static List<AgarEntity> agarEntities = new ArrayList<AgarEntity>();
	
	public static AgarEntity getAgarEntity(Entity ent) {
		for(AgarEntity ents : agarEntities) {
			if(ents.entity.equals(ent)) {
				return ents;
			}
		}
		return null;
	}
	
	//
	
	public Entity entity;
	public boolean canEat;
	public boolean canbeEaten;
	
	public AgarEntity(Entity entity, boolean canEat, boolean canBeEaten) {
		this.entity = entity;
		this.canEat = canEat;
		this.canbeEaten = canBeEaten;
		
		agarEntities.add(this);
	}
}
