package com.swingsword.ssengine.achievements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AchievementManager {

	public static ArrayList<Achievement> achievements = new ArrayList<Achievement>();
	
	public AchievementManager() {
		loadAchievements();
	}
	
	@SuppressWarnings("serial")
	public void loadAchievements() {
		// Global
		new Achievement("Best Friends", "Add a friend for the first time", "g", new HashMap<String, Integer>() {{ put("b_friends", 1); }}, 5);
		new Achievement("Partier", "Party up for the first time", "g", new HashMap<String, Integer>() {{ put("b_maxinparty", 1); }}, 5);
		new Achievement("Baby Steps", "Used chat for the first time", "g", new HashMap<String, Integer>() {{ put("b_chat", 1); }}, 5);
		new Achievement("Getting There", "Play a minigame", "g", new HashMap<String, Integer>() {{ put("g_games_played", 1); }}, 5);
		new Achievement("Popular", "Add 10 friends", "g", new HashMap<String, Integer>() {{ put("b_friends", 10); }}, 10);
		new Achievement("Cops Called!", "Party up with 5 of more players", "g", new HashMap<String, Integer>() {{ put("b_maxinparty", 5); }}, 10);
		new Achievement("Rankup!", "Retrieve a rank", "g", new HashMap<String, Integer>() {{ put("b_rank", 1); }}, 15);
		new Achievement("Do I Like This Server?", "Win 100 minigames", "g", new HashMap<String, Integer>() {{ put("g_wins", 100); }}, 15);
		new Achievement("Dinner's Ready", "Win 500 minigames", "g", new HashMap<String, Integer>() {{ put("g_wins", 500); }}, 25);
		new Achievement("Addicted", "Win 1000 minigames", "g", new HashMap<String, Integer>() {{ put("g_wins", 1000); }}, 50);
		
		// Counter Strike
		new Achievement("Ace", "Kill all enemies in a single round", "cs", new HashMap<String, Integer>() {{ put("cs_friends", 1); }}, 10);
		new Achievement("Spray N Pray", "Get a kill while flashed", "cs", new HashMap<String, Integer>() {{ put("cs_friends", 1); }}, 10);
		new Achievement("KOBE!", "Get a kill with a grenade", "cs", new HashMap<String, Integer>() {{ put("cs_friends", 1); }}, 10);
		new Achievement("Clutch or Kick", "Kill the last enemy while being the last one alive", "cs", new HashMap<String, Integer>() {{ put("cs_friends", 1); }}, 10);
		new Achievement("One Deag", "Get a headshot with the Deagle", "cs", new HashMap<String, Integer>() {{ put("cs_friends", 1); }}, 10);
		new Achievement("Gotta Win Those", "Get 25 kills with a knife", "cs", new HashMap<String, Integer>() {{ put("cs_friends", 1); }}, 15);
		new Achievement("KennyS", "Get 25 kills with the AWP", "cs", new HashMap<String, Integer>() {{ put("cs_friends", 1); }}, 15);
		new Achievement("Planter", "Plant 50 bombs", "cs", new HashMap<String, Integer>() {{ put("cs_friends", 1); }}, 25);
		new Achievement("Defuser", "Defuse 50 bombs", "cs", new HashMap<String, Integer>() {{ put("cs_friends", 1); }}, 25);
		new Achievement("Striker", "Win 50 games", "cs", new HashMap<String, Integer>() {{ put("cs_friends", 1); }}, 25);

		// Agar
		new Achievement("Hungry", "Eat 1000 food", "ar", new HashMap<String, Integer>() {{ put("ar_food_eaten", 1); }}, 5);
		new Achievement("Bukkit List", "Live for at least 15 minutes", "ar", new HashMap<String, Integer>() {{ put("ar_time_alive", 900); }}, 10);
		new Achievement("Killer", "Eat another cell", "ar", new HashMap<String, Integer>() {{ put("ar_cells_eaten", 1); }}, 10);
		new Achievement("Infected", "Eat a virus", "ar", new HashMap<String, Integer>() {{ put("cs_friends", 1); }}, 10);
		new Achievement("Rookie", "Reach a mass of 500", "ar", new HashMap<String, Integer>() {{ put("ar_highest_mass", 500); }}, 15);
		new Achievement("Pro", "Reach a mass of 1000", "ar", new HashMap<String, Integer>() {{ put("ar_highest_mass", 1000); }}, 25);
		new Achievement("Massacre", "Eat a total of 50 cells", "ar", new HashMap<String, Integer>() {{ put("ar_cells_eaten", 50); }}, 25);
		
		// Rust
		new Achievement("Home Sweet Home", "Place your first home", "rt", new HashMap<String, Integer>() {{ put("rt_kills", 1); }}, 5);
		new Achievement("Roomates", "Enter a door code", "rt", new HashMap<String, Integer>() {{ put("rt_kills", 1); }}, 5);
		new Achievement("Bandit", "Get 50 kills", "rt", new HashMap<String, Integer>() {{ put("rt_kills", 50); }}, 10);
		new Achievement("Care Package", "Open a airdrop crate", "rt", new HashMap<String, Integer>() {{ put("rt_kills", 1); }}, 10);
		new Achievement("Bullseye", "Get 50 headshots", "rt", new HashMap<String, Integer>() {{ put("rt_headshots", 50); }}, 10);
		new Achievement("Radioactive", "Reach 500 rads and survive", "rt", new HashMap<String, Integer>() {{ put("rt_kills", 1); }}, 10);
		new Achievement("Scout", "Get 100 kills", "rt", new HashMap<String, Integer>() {{ put("rt_kills", 100); }}, 15);
		new Achievement("Raider", "Place a C4", "rt", new HashMap<String, Integer>() {{ put("rt_kills", 1); }}, 20);
		new Achievement("Marksman", "Get 100 headshots", "rt", new HashMap<String, Integer>() {{ put("rt_headshots", 100); }}, 20);
		new Achievement("One Man Army", "Get 250 Kills", "rt", new HashMap<String, Integer>() {{ put("rt_kills", 250); }}, 25);
		
		// BuildIt

		
	}
	
	public static ArrayList<Achievement> getAchievements() {
		return achievements;
	}
	
	public static Achievement getAchievement(String name) {
		for(Achievement all : achievements) {
			if(all.name.equals(name)) {
				return all;
			}
		}
		return null;
	}
	
	public static List<Achievement> getAchievementsWithPrefix(String prefix) {
		List<Achievement> achievements = new ArrayList<Achievement>();
		
		for(Achievement all : getAchievements()) {
			if(all.prefix.equals(prefix)) {
				achievements.add(all);
			}
		}
		
		return achievements;
	}
}
