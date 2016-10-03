package com.swingsword.ssengine.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.database.SQLManager;
import com.swingsword.ssengine.player.PlayerAccount;
import com.swingsword.ssengine.player.PlayerProfileSettings;
import com.swingsword.ssengine.utils.AnvilUtils;
import com.swingsword.ssengine.utils.InventoryUtils;
import com.swingsword.ssengine.utils.ItemUtils;

public class SearchCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2, final String[] arg3) {
		Player player = null;
		
		if(arg0 instanceof Player) {
			player = (Player) arg0;
		}
		
		if(arg2.equalsIgnoreCase("search")) {
			if(player != null) {
				final Player finalPlayer = player;
				
				if(arg3.length == 0) {
					AnvilUtils gui = new AnvilUtils(player, new AnvilUtils.AnvilClickEventHandler(){
						@Override
						public void onAnvilClick(AnvilUtils.AnvilClickEvent event){
						if(event.getSlot() == AnvilUtils.AnvilSlot.OUTPUT){
							
						if(event.getClickedItem().getItemMeta().getDisplayName().contains(ChatColor.YELLOW + "")) {
							event.setWillClose(true);
							event.setWillDestroy(true);
						} else {
							event.setWillClose(false);
							event.setWillDestroy(false);
							
							openProfile(finalPlayer, event.getName(), event.getClickedItem());
						}
						
						} else {
						event.setWillClose(false);
						event.setWillDestroy(false);
						}
						}
					});
					
					gui.setSlot(AnvilUtils.AnvilSlot.INPUT_LEFT, ItemUtils.itemStackFromString("i=421;n=Enter username"));
					gui.open();
					player.setLevel(0);
					player.setExp(0);
						
				} else {
					if(!arg3[0].equals("stop")) {
						openProfile(player, arg3[0], null);
					}
				}
			}
		}

		return false;
	}
	
	public static void openProfile(final Player player, final String target, final ItemStack item) {
		Bukkit.getScheduler().runTaskAsynchronously(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() { 
				UUID uuid = SQLManager.getSQL("global").getUUID(target);
				
				if(uuid != null) {
					PlayerAccount account = new PlayerAccount(SQLManager.getSQL("global").getValues(uuid));
					
					if(PlayerProfileSettings.isAllowed(player, account, "1")) {
						InventoryUtils.openPlayerInventory(player, account.getCache());
					} else {
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + ChatColor.RED + "You can't view this profile.");
					}
					
				} else {
					String message = ChatColor.RED + "0 results for " + ChatColor.YELLOW + target;
					
					if(item != null) {
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(message);
						item.setItemMeta(meta);
					} else {
						player.sendMessage(ChatColor.GRAY + "" + ChatColor.BOLD + "> " + message);
					}
				}
			}
		});
	}
}
