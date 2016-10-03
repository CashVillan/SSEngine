package com.swingsword.ssengine.game;

public enum GameState {
	
	IN_LOBBY(false), IN_GAME(false), POST_GAME(false), RESETTING(false);
	
	private boolean canJoin;
	
	private static GameState CurrentState = GameState.IN_LOBBY;
	
	GameState(boolean canJoin) {
		this.canJoin = canJoin;
	}
	
	public boolean canJoin() {
		return canJoin;
	}
	
	public static void setState(GameState state) {
		GameState.CurrentState = state;
	}
	
	public static boolean isState(GameState state) {
		return GameState.CurrentState == state;
	}
	
	public static GameState getState() {
		return CurrentState;
	}
	
}
