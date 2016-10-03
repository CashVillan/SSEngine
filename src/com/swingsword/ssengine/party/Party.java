package com.swingsword.ssengine.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

import com.swingsword.ssengine.MasterPlugin;

public class Party {

	public String host;
	
	public List<String> invites = new ArrayList<String>();
	public List<String> players = new ArrayList<String>();
	
	public Party(String data, boolean newParty) {
		if(newParty) {
			this.host = data;
			
		} else {
			String[] dataArray = data.split(";");
			
			host = dataArray[0].split(",")[0];
			for(int x = 1; x < dataArray[0].split(",").length; x++) {
				if(dataArray[0].split(",").length - 1 >= x) {
					players.add(dataArray[0].split(",")[x]);
				}
			}
			if(dataArray.length > 1) {
				for(int x = 0; x < dataArray[1].split(",").length; x++) {
					if(dataArray[1].split(",").length - 1 >= x) {
						invites.add(dataArray[1].split(",")[x]);
					}
				}
			}
		}
	}
	
	public void invite(String target) {
		if(!players.contains(target) && !invites.contains(target)) {
			invites.add(target);
		}
		
		MasterPlugin.getMasterPlugin().channel.sendParty(this);
		
		MasterPlugin.getMasterPlugin().channel.sendMessage(target, ChatColor.GREEN + "You have been invited to join '" + host + "'s party!");
	}
	
	public void acceptInvite(String target) {
		if(invites.contains(target)) {
			invites.remove(target);
			players.add(target);
		
			PartyManager.denyAllRequests(target);
			
			MasterPlugin.getMasterPlugin().channel.sendParty(this);
			
			notifyAll(ChatColor.GREEN + target + " has joined the party!");
		}
		
		
	}
	
	public void denyInvite(String target) {
		invites.remove(target);
		players.remove(target);
		
		MasterPlugin.getMasterPlugin().channel.sendParty(this);
	}
	
	public void leave(String target) {
		invites.remove(target);
		players.remove(target);
		
		notifyAll(ChatColor.RED + target + " left the party. ;(");
		
		if(target.equals(host)) {
			disband();
			
		} else {
			MasterPlugin.getMasterPlugin().channel.sendParty(this);
		}
	}
	
	public void disband() {
		PartyManager.removeParty(host);
		
		notifyAll(ChatColor.RED + "Your party has been disbanded.");
		
		MasterPlugin.getMasterPlugin().channel.disbandParty(host);
	}
	
	public void notifyAll(String message) {
		MasterPlugin.getMasterPlugin().channel.sendMessage(host, message);
		for(String player : players) {
			MasterPlugin.getMasterPlugin().channel.sendMessage(player, message);
		}
	}
	
	public String toString() {
		String data = host;
		
		for(String player : players) {
			data += "," + player;
		}
		data += ";";
		
		for(String invite : invites) {
			data += "," + invite;
		}
		
		return data;
	}
}
