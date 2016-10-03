package com.swingsword.ssengine.game.threads;

import org.bukkit.scheduler.BukkitTask;

public class Timer {

	private String id;
	private BukkitTask task;
	private int amount;
	private boolean cancelled = false;
	
	public Timer(String id, BukkitTask task, int amount) {
		this.id = id;
		this.task = task;
		this.amount = amount;
	}
	
	public String getId() {
		return id;
	}
	
	public BukkitTask getTask() {
		return task;
	}
	
	public int getLeft() {
		return amount;
	}
	
	public void setLeft(int amount) {
		this.amount = amount;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}
	
	public void cancel() {
		cancelled = true;
	}
}
