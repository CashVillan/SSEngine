package com.swingsword.ssengine.game.games.rust.building;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import com.swingsword.ssengine.game.games.rust.utils.SimpleLocation;

public class Foundation {

	public String location = null;
	
	private HashMap<String, String> oldBlocks = new HashMap<String, String>();
	
	private ArrayList<String> props = new ArrayList<String>();
	
	@SuppressWarnings("unused")
	private Building plugin;
    public Foundation(Building Plugin) {
        this.plugin = Plugin;   
	}
    
	public Foundation(Location mainLocation, HashMap<String, String> oldblocks) {
		this.location = new SimpleLocation(mainLocation).toString().replace(".0", "");
		
		this.oldBlocks = oldblocks;
	}
	
	public Location getLocation() {
		return new SimpleLocation(location).toLocation();
	}
	
	public HashMap<String, String> getOldBlocks() {
		return this.oldBlocks;
	}
	
	public ArrayList<String> getPropLocations() {
		return this.props;
	}
	
	@SuppressWarnings("deprecation")
	public void addProp(World world, Location loc, String type, Material mat, byte data) {
		if(loc != null) {
			if(type.contains("Ceiling") || !oldBlocks.containsKey(loc)) {
				oldBlocks.put(new SimpleLocation(loc.clone()).toString(), type);
				props.add(new SimpleLocation(loc.clone()).toString());
			}
			
			if(type.contains("Ceiling") && loc.getBlock().getType().isSolid()) {
				return;
				
			} else {
				loc.getBlock().setTypeIdAndData(mat.getId(), data, true);
			}
		}
	}
	
	public void loadProp(Location loc) {
		props.add(new SimpleLocation(loc.getBlock().getLocation()).toString());
	}
}
