package com.swingsword.ssengine.game;

import com.swingsword.ssengine.game.games.agar.Agar;
import com.swingsword.ssengine.game.games.buildit.BuildIt;
import com.swingsword.ssengine.game.games.dev.Dev;
import com.swingsword.ssengine.game.games.hub.Hub;
import com.swingsword.ssengine.game.games.minestrike.Minestrike;
import com.swingsword.ssengine.game.games.prison.Prison;
import com.swingsword.ssengine.game.games.rust.Rust;

public enum Gamemode {
	hub(Hub.class, 2),
	buildit(BuildIt.class),
	agar(Agar.class),
	minestrike(Minestrike.class),
	rust(Rust.class, 0),
	prison(Prison.class, 0),
	dev(Dev.class, 0);
	
	Class<? extends GamePlugin> gameClass;
	int servers = -1;
	
	Gamemode(Class<? extends GamePlugin> gameClass, int servers) {
		this.gameClass = gameClass;
		this.servers = servers;
	}
	
	Gamemode(Class<? extends GamePlugin> gameClass) {
		this.gameClass = gameClass;
	}
	
	public Class<? extends GamePlugin> getGameClass() {
		return gameClass;
	}
	
	public int getServerCount() {
		return servers;
	}
}
