package com.swingsword.ssengine.game.threads;

import java.util.ArrayList;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.swingsword.ssengine.MasterPlugin;

public class TimerHandler {
	
	private static ArrayList<Timer> timers = new ArrayList<Timer>();
	
	public static Timer createTimer(final String id, int amount, int delay, final Runnable onProgress, final Runnable onCancel, final Runnable onEnd) {
		BukkitRunnable runnable = new BukkitRunnable() {
			public void run() {
				if(getTimer(id) == null || getTimer(id).isCancelled()) {
					this.cancel();
					removeTimer(id);
					
					if(onCancel != null) {
						onCancel.run();
					}
					
					return;
					
				} else {
					int amount = getTimer(id).getLeft();
					if(amount > 0) {
						if(onProgress != null) {
							onProgress.run();
						}
						
						if(getTimer(id) == null || getTimer(id).isCancelled()) {
							this.cancel();
							removeTimer(id);
							
							if(onCancel != null) {
								onCancel.run();
							}
							
							return;
						}
						
						amount = amount - 1;
						getTimer(id).setLeft(amount);
						
					} else {
						this.cancel();
						removeTimer(id);
						
						if(onEnd != null) {
							onEnd.run();
						}
						
						return;
					}
				}
			}
		};
		
		BukkitTask task = runnable.runTaskTimer(MasterPlugin.getMasterPlugin(), 0, delay);
		
		Timer timer = new Timer(id, task, amount);
		timers.add(timer);
		return timer;
	}
	
	public static Timer getTimer(String id) {
		for(Timer timer : timers) {
			if(timer.getId().equals(id)) {
				return timer;
			}
		}
		return null;
	}
	
	public static void removeTimer(String id) {
		for(Timer timer : timers) {
			if(timer.getId().equals(id)) {
				timers.remove(timer);
				return;
			}
		}
	}
}
