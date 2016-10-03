package com.swingsword.ssengine.game.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.swingsword.ssengine.game.GameManager;
import com.swingsword.ssengine.game.GameState;

public class LobbyCounter extends BukkitRunnable {

	public static boolean isRunning = false;
	public static int Time = -1;
	public static int delay = -1;
	
	@SuppressWarnings("static-access")
	public LobbyCounter(int delay) {
		if(!isRunning) {
			this.Time = 0;
			this.delay = delay;
			
			isRunning = true;
		}
	}
	
	public void run() {
		if(Time == -1) {
			isRunning = false;
			return;
		}
		
		if(Time == delay) {		
			if(Bukkit.getOnlinePlayers().size() < GameManager.currentGame.minPlayers) {
				Bukkit.broadcastMessage(ChatColor.RED + "Not enough players to start the game. The timer got reset.");
				
			} else {
				GameManager.currentGame.start();
				GameState.setState(GameState.IN_GAME);
			}
			
			Time = -1;
			isRunning = false;
			
		} else {
			for(Player all : Bukkit.getOnlinePlayers()) {
				all.playSound(all.getLocation(), Sound.BLOCK_NOTE_PLING, 1, (float) 1);
				//ChatUtils.sendActionBar(all, ChatColor.GREEN + "" + ChatColor.BOLD + "Starting in " + (delay - Time));
			}
				
			Time = Time + 1;
		}
	}
}
