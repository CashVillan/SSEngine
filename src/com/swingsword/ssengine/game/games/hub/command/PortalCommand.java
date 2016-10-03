package com.swingsword.ssengine.game.games.hub.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.swingsword.ssengine.game.games.hub.Hub;
import com.swingsword.ssengine.game.games.hub.utils.PortalUtils;

public class PortalCommand implements CommandExecutor {
	
	private Map<String, List<String>> selections = new HashMap<String, List<String>>();
	
	public void usages(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Portal Commands:");
		sender.sendMessage("[/portal create <destination>] Create portals.");
		sender.sendMessage("[/portal select <filter,list>] Get Selection.");
		sender.sendMessage("[/portal clear] Clear Selection.");
		sender.sendMessage("[/portal reload] Reload all files and data.");
		sender.sendMessage("[/portal forcesave] Force save portals.");
	}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("portal")) {
			if (sender.hasPermission("ss.portal")) {
				if (sender instanceof Player) {
					if (args.length == 0) {
						usages(sender);
					} else if (args.length == 1) {
						if (args[0].equalsIgnoreCase("reload")) {
							//TODO
							return true;
						} else if (args[0].equalsIgnoreCase("forcesave")) {
							//TODO
							return true;
						} else if (args[0].equalsIgnoreCase("clear")) {
							if(selections.containsKey(sender.getName())) {
								selections.remove(sender.getName());
								sender.sendMessage(ChatColor.GREEN + "Selection cleared.");
							} else {
								sender.sendMessage(ChatColor.RED + "You haven't selected anything.");
							}
							return true;
						}
					} else if (args.length == 2) {
						if (args[0].equalsIgnoreCase("select")) {
							Player player = (Player) sender;
							Selection selection = Hub.getInstance().worldEdit.getSelection(player);
							if (selection != null) {
								if (selection instanceof CuboidSelection) {
									List<Location> locations = getLocationsFromCuboid((CuboidSelection) selection);
									List<String> blocks = new ArrayList<String>();
									Integer count = 0;
									Integer filtered = 0;
									String[] ids = null;
									Boolean filter = false;
									if (!args[1].equals("0")) {
										ids = args[1].split(",");
										filter = true;
									}
									for (Location location : locations) {
										Block block = player.getWorld().getBlockAt(location);
										if (filter) {
											Boolean found = false;
											for (int i = 0; i < ids.length; i++) {
												String[] parts = ids[i].split(":");
												if (parts.length == 2) {
													if (parts[0].equals(String.valueOf(block.getTypeId())) && parts[1].equals(String.valueOf(block.getData()))) {
														found = true;
														break;
													}
												} else {
													if (parts[0].equals(String.valueOf(block.getTypeId()))) {
														found = true;
														break;
													}
												}
											}
											if (found) {
												blocks.add(block.getWorld().getName() + "#" + String.valueOf(block.getX()) + "#" + String.valueOf(block.getY())
														+ "#" + String.valueOf(block.getZ()));
												count++;
											} else {
												filtered++;
											}
										} else {
											blocks.add(block.getWorld().getName() + "#" + String.valueOf(block.getX()) + "#" + String.valueOf(block.getY())
													+ "#" + String.valueOf(block.getZ()));
											count++;
										}
									}
									this.selections.put(player.getName(), blocks);
									sender.sendMessage(ChatColor.GREEN + String.valueOf(count) + " blocks have been selected, " + String.valueOf(filtered) + " filtered.");
									sender.sendMessage(ChatColor.GREEN + "Use the selection in the create and remove commands.");
								} else {
									sender.sendMessage(ChatColor.RED + "Must be a cuboid selection!");
								}
							} else {
								sender.sendMessage(ChatColor.RED + "You have to first create a WorldEdit selection!");
							}
						} else if (args[0].equalsIgnoreCase("create")) {
							if (selections.containsKey(sender.getName())) {
								List<String> selection = selections.get(sender.getName());
								for (String block : selection) {
									PortalUtils.portalData.put(block, args[1]);
								}
								sender.sendMessage(ChatColor.GREEN + String.valueOf(selection.size()) + " portals have been created.");
								return true;
							}
							sender.sendMessage(ChatColor.RED + "You haven't selected anything.");
							return false;
						}
					}
				}
			}
		}
		return false;
	}
	
	private List<Location> getLocationsFromCuboid(CuboidSelection cuboid){
		List<Location> locations = new ArrayList<Location>();
		Location minLocation = cuboid.getMinimumPoint();
		Location maxLocation = cuboid.getMaximumPoint();
		for(int i1 = minLocation.getBlockX(); i1 <= maxLocation.getBlockX(); i1++){
			for(int i2 = minLocation.getBlockY(); i2 <= maxLocation.getBlockY(); i2++){
				for(int i3 = minLocation.getBlockZ(); i3 <= maxLocation.getBlockZ(); i3++){
					locations.add(new Location(cuboid.getWorld(), i1, i2, i3));
				}
			}
		}
		return locations;
	}
}
