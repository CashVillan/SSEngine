package com.swingsword.ssengine.game.games.minestrike.database;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.game.games.minestrike.guns.Gun;
import com.swingsword.ssengine.game.games.minestrike.guns.SkinnedGun;

public class Loadout {
	
	public List<Integer> items = new ArrayList<Integer>();
	
	public void addGun(String gunName) {
		Gun gun = Gun.getGun(gunName);
		
		List<Integer> remove = new ArrayList<Integer>();
		for(int gunId : items) {
			if(Gun.getGun(gunId).getSelectionSlot() == gun.getSelectionSlot()) {
				remove.add(gunId);
			}
		}
		for(int gunId : remove) {
			items.remove(new Integer(gunId));
		}
		
		if(gun instanceof SkinnedGun) {
			items.add(gun.getId());
		}
	}
	
	public void saveLoadout(final Player player) {
		final Loadout finalLoadout = this;
		
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				SQLManager.getSQL("global").setValue(player.getUniqueId(), "csLoadout", format(finalLoadout));
			}
		});
	}
	
	public static Loadout deformat(String strLoadout) {
		Loadout lo = new Loadout();
		
		if(strLoadout != null) {
			for(String item : strLoadout.split(",")) {
				if(!item.equals("")) {
					lo.items.add(Integer.parseInt(item));
				}
			}
		}
		
		return lo;
	}
	
	public static String format(Loadout loadout) {
		String items = "";
		
		for(int gunId : loadout.items) {
			if(items.equals("")) {
				items = gunId + "";
			} else {
				items = items + "," + gunId;
			}
		}
		
		return items;
	}
	
	public static Loadout getLoadout(final Player player) {
		String data = (String) SQLManager.getSQL("global").getValue(player.getUniqueId(), "csLoadout");
		
		if(data != null) {
			return deformat(data);
			
		} else {
			return new Loadout();
		}	
	}
	
	public static void saveLoadout(final Player player, final String loadout) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				SQLManager.getSQL("global").setValue(player.getUniqueId(), "csLoadout", loadout);
			}
		});
	}
}
