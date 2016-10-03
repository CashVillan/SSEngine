package com.swingsword.ssengine.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.swingsword.ssengine.party.Party;
import com.swingsword.ssengine.party.PartyManager;
import com.swingsword.ssengine.utils.PartyUtils;

public class PartyCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, String[] arg3) {
		Player player = null;
		
		if(arg0 instanceof Player) {
			player = (Player) arg0;
		}
		
		if(player != null) {
			if(arg2.equalsIgnoreCase("party")) {
				if(arg3.length == 0) {
					player.openInventory(PartyUtils.getPartyInv(player, 0));
					
				} else {
					if(arg3[0].equalsIgnoreCase("help")) {
						sendMessage(player, "Commands:");
						sendMessage(player, "[/party] Open the party inventory.");
						sendMessage(player, "[/party create] Create a party.");
						sendMessage(player, "[/party invite <player>] Invite player to your party.");
						sendMessage(player, "[/party accept <player>] Accept a party request.");
						sendMessage(player, "[/party deny <player>] Deny a party request.");
						sendMessage(player, "[/party leave] Leave your party.");
					}
					
					if(arg3[0].equalsIgnoreCase("create")) {
						if(PartyManager.getParty(player.getName(), false) == null) {
							PartyManager.createParty(player);
							
						} else {
							sendMessage(player, ChatColor.RED + "You are already part of a party, leave first.");
						}
						
					} else if(arg3[0].equalsIgnoreCase("invite")) {
						Party party;
						if(PartyManager.getParty(player.getName(), false) == null) {
							party = PartyManager.createParty(player);
						} else {
							party = PartyManager.getParty(player.getName(), false);
						}
						
						if(arg3.length == 2) {
							String target = arg3[1];
							
							if(PartyManager.getParty(target, false) == null) {
								if(!PartyManager.getInvites(target).contains(party.host)) {
									party.invite(target);
									
									player.sendMessage(ChatColor.GREEN + "Invited " + target + " to join your party.");
									
								} else {
									sendMessage(player, ChatColor.RED + "You have already requested that player to join your party.");
								}
								
							} else {
								sendMessage(player, ChatColor.RED + "That player has already joined a party.");
							}
						}
						
					} else if(arg3.length == 2) {
						String targetHost = arg3[1];
						
						if(!PartyManager.getInvites(player.getName()).contains(targetHost)) {
							sendMessage(player, ChatColor.RED + "You did not get an invite from that player.");
							
							return false;
						}
						
						Party party = PartyManager.getParty(targetHost, true);
						
						if(party != null) {
							if(arg3[0].equalsIgnoreCase("accept")) {
								if(party.invites.contains(player.getName())) {
									party.acceptInvite(player.getName());
									
								} else {
									player.sendMessage(ChatColor.RED + "You have already joined a party.");
								}
							}
							
							if(arg3[0].equalsIgnoreCase("deny")) {
								if(party.invites.contains(player.getName())) {
									party.denyInvite(player.getName());
									
									player.sendMessage(ChatColor.RED + "Denied party request.");
									
								} else {
									player.sendMessage(ChatColor.RED + "You have already joined a party.");
								}
							}
						}
						
					} else {
						if(PartyManager.getParty(player.getName(), false) != null) {
							Party party = PartyManager.getParty(player.getName(), false);
							
							if(arg3[0].equalsIgnoreCase("leave")) {
								party.leave(player.getName());
							}
						} else {
							sendMessage(player, ChatColor.RED + "You currently don't have a party.");
						}
					}
				}
			}
		}
		
		return false;
	}
	
	//Methods
	
	public void sendMessage(Player player, String message) {
		if(player != null) {
			player.sendMessage(message);
		} else {
			System.out.println(ChatColor.stripColor(message));
		}
	}
}
