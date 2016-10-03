package com.swingsword.ssengine.entity;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.LocationUtils;
import com.swingsword.ssengine.utils.StringUtils;
import com.swingsword.ssengine.utils.VectorUtils;

public class InteractEntityManager {
	
	public static boolean enabled = false;
	
	@SuppressWarnings("deprecation")
	public InteractEntityManager(boolean hub) {		
		if(hub) {
			ConfigUtils.updateType("entity");
			for (String item : ConfigUtils.getConfig("cache").getStringList("entity")) {
				List<String> configdata = StringUtils.stringToList(item);
				Location loc = LocationUtils.stringToLocation(configdata.get(0));
				EntityType type = EntityType.valueOf(configdata.get(1));
				ItemStack mobItem = null;
				if (!configdata.get(2).equals("null")) {
					mobItem = new ItemStack(Material.getMaterial(Integer.parseInt(configdata.get(2))), 1);
				}
				String line1 = configdata.get(3);
				String line2 = configdata.get(4);
				Vector vec = VectorUtils.stringToVector(configdata.get(5));
				String cmd = "/" + configdata.get(6);
	
				LivingEntity entity = (LivingEntity) Bukkit.getWorld("map").spawnEntity(loc, type);
				if(entity instanceof Skeleton) {
					Skeleton s = (Skeleton) entity;
					s.setSkeletonType(SkeletonType.WITHER);
				}
				if(mobItem != null) {
					entity.getEquipment().setItemInHand(mobItem);
				}
				
				new InteractEntity(loc, entity, line1, line2, vec, cmd, false);
			}
		}
		
		if(!enabled) {
			enabled = true;

			Bukkit.getScheduler().scheduleSyncRepeatingTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					for(InteractEntity all : InteractEntity.getRealInteractEntities().values()) {
						all.ent.teleport(all.loc.clone());
					}
				}
			}, 0, 0);
		}		
	}
}
