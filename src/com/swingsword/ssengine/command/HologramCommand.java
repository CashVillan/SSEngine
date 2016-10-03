package com.swingsword.ssengine.command;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HologramCommand {
	
	HashMap<String, HashMap<String, String>> ct = new HashMap<>();
	HashMap<String, HashMap<String, String>> t = new HashMap<>();
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("hologram")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				Location loc = player.getLocation().add(0, 5, 0);
				for (int i = 0; i < 2; i++) {
					String type;
					if (i==0) {
						type = "t";
					} else {
						type = "ct";
					}
					for (int i1 = 0; i1 < 5; i++) {
						if (type.equals("ct")) {
						
						} else {
							
						}
					}
				}

			}
		}
		
		return false;
	}

	public void load() {
		HashMap<String, String> values = new HashMap<>();
		values.put("kills", "10");
		values.put("mvps", "4");
		values.put("deaths", "14");
		values.put("money", "16000");
		values.put("score", "0");
		ct.put("CashVillan", values);
		t.put("CashVillan", values);
	}
}
