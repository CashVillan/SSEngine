package com.swingsword.ssengine.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldListener implements Listener {
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if (event.toWeatherState()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void on(BlockFadeEvent event) {
		if (event.getBlock().getType() == Material.GRASS && event.getNewState().getType() == Material.DIRT) {
			event.setCancelled(true);
		} 
	}
	
	@EventHandler
	public void onSpread(BlockSpreadEvent event) {
		if (event.getNewState().getType() == Material.BROWN_MUSHROOM || event.getNewState().getType() == Material.RED_MUSHROOM) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onChange(BlockFormEvent event) {
		if (event.getNewState().getType() == Material.BROWN_MUSHROOM || event.getNewState().getType() == Material.RED_MUSHROOM) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDecay(LeavesDecayEvent event) {
		event.setCancelled(true);
	}

}
