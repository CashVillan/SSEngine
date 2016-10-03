package com.swingsword.ssengine.party;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.stats.StatManager;

public class PartyManager {

	public static ArrayList<Party> parties = new ArrayList<Party>();
	
	public static Party createParty(Player host) {
		PartyManager.denyAllRequests(host.getName());
		
		if(getParty(host.getName(), false) == null) {
			Party party = new Party(host.getName(), true);
			addParty(party);
			
			MasterPlugin.getMasterPlugin().channel.sendParty(party);
			
			host.sendMessage(ChatColor.GREEN + "Created a party.");
			
			return party;
			
		} else {
			host.sendMessage(ChatColor.RED + "You already have a party.");
		}
		
		return null;
	}
	
	@SuppressWarnings("deprecation")
	public static void addParty(Party party) {
		if(getParty(party.host, true) != null) {
			removeParty(party.host);
		}
		
		if(Bukkit.getOfflinePlayer(party.host).isOnline()) {
			Player owner = Bukkit.getPlayer(party.host);
			
			if(StatManager.getStat(owner, "b_maxinparty") < 5 && StatManager.getStat(owner, "b_maxinparty") < party.players.size()) {
				StatManager.addStat(owner, "b_maxinparty", party.players.size());
			}
		}
		
		parties.add(party);
	}
	
	public static Party getParty(String player, boolean host) {
		for(Party all : parties) {
			if(host) {
				if(all.host.equals(player)) {
					return all;
				}
				
			} else {
				if(all.host.equals(player) || all.players.contains(player)) {
					return all;
				}
			}
		}
		
		return null;
	}
	
	public static List<String> getInvites(String player) {
		List<String> invites = new ArrayList<String>();
		
		for(Party all : parties) {
			if(all.invites.contains(player)) {
				invites.add(all.host);
			}
		}
		
		return invites;
	}
	
	public static void removeParty(String host) {
		List<Party> remove = new ArrayList<Party>();
		
		for(Party all : parties) {
			if(all.host.equals(host)) {
				remove.add(all);
			}
		}
		
		for(Party all : remove) {
			parties.remove(all);
		}
	}
	
	public static void denyAllRequests(String player) {
		for(Party all : PartyManager.parties) {
			if(all.invites.contains(player)) {
				all.invites.remove(player);
				
				MasterPlugin.getMasterPlugin().channel.sendParty(all);
			}
		}
	}
}
