package com.swingsword.ssengine.entity;

import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.server.ServerManager;
import com.swingsword.ssengine.utils.HologramUtils;

import net.minecraft.server.v1_10_R1.NBTTagCompound;

public class InteractEntity implements Listener {

	private static HashMap<Entity, InteractEntity> entities = new HashMap<Entity, InteractEntity>();
	
	public static InteractEntity getInteractEntity(Entity entity) {
		return entities.get(entity);
	}
	
	public static Set<Entity> getInteractEntities() {
		return entities.keySet();
	}
	
	public static HashMap<Entity, InteractEntity> getRealInteractEntities() {
		return entities;
	}
	
	public Location loc;
	public Entity ent;
	public String line1;
	public String line2;
	public String command;
	public boolean canMove;
	
	public InteractEntity(final Location loc, final LivingEntity ent, final String line1, final String line2, Vector nameTagLoc, String onClick, final boolean canMove) {
		this.loc = loc;
		this.line1 = line1;
		this.line2 = line2;
		this.ent = ent;
		this.command = onClick;
		this.canMove = canMove;
		
		entities.put(ent, this);
		
		ent.setRemoveWhenFarAway(false);
		ent.setFireTicks(-999999);
		//setNoAI(ent);
		
		HologramUtils.create(ChatColor.stripColor(line1), loc.clone().add(0, 0.5, 0), line1);
		
		Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				String finalLine2 = line2.replace("<SERVER>", ServerManager.getOnlineCount(command.split(" ")[1]) + "");
				
				if(!HologramUtils.exists(ChatColor.stripColor(line1) + "1")) {
					HologramUtils.create(ChatColor.stripColor(line1) + "1", loc.clone().add(0, 0.5, 0).add(0, -0.25, 0), finalLine2);
				} else {
					HologramUtils.update(ChatColor.stripColor(line1) + "1", finalLine2);
				}
			}
		}, 40, 40);
	}
	
	public static void setNoAI(Entity ent) {
		net.minecraft.server.v1_10_R1.Entity nmsEnt = ((org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity) ent).getHandle();
	    NBTTagCompound tag = new NBTTagCompound();
	    nmsEnt.e(tag); //sets nbt tag //brb
	    
	    nmsEnt.c(tag);
	    tag.setInt("NoAI", 1);
	    nmsEnt.f(tag);
	    
	    
	}
}