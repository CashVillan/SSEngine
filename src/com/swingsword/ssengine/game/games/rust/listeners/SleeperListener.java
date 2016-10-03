package com.swingsword.ssengine.game.games.rust.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.utils.BackpackUtils;
import com.swingsword.ssengine.game.games.rust.utils.SpawnUtils;
import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.StringUtils;

import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.techcable.npclib.NPC;
import net.techcable.npclib.NPCLib;

public class SleeperListener implements Listener {
		
	/*@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if(SpawnUtils.dead.contains(player.getName())) {
			if(SpawnUtils.getRandomSpawn() != null) {
				SpawnUtils.respawnPlayer(player, SpawnUtils.getRandomSpawn());
			}
		}
		
		if(!(player.getInventory().getItem(0) != null && player.getInventory().getItem(0).getType() == Material.WOOD_AXE && player.getInventory().getItem(1) != null && player.getInventory().getItem(1).getType() == Material.BOOK && player.getInventory().getItem(2) != null && player.getInventory().getItem(2).getType() == Material.TORCH && player.getInventory().getItem(3) != null && player.getInventory().getItem(3).getType() == Material.AIR)) {
			NPC offlinePlayer = new NPC(null, player.getLocation().add(0.5, 0, 0.5), 20, player.getInventory().getContents());
			
			offlinePlayer.getEntity().setMetadata("name", new FixedMetadataValue(Main.plugin, player.getName()));
			
			//PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) offlinePlayer.getEntity()).getHandle());
	        //for(Player p : Bukkit.getOnlinePlayers()) {
	         //   ((CraftPlayer) p).getHandle().playerConnection.sendPacket(info);
			//}
	        
			offlinePlayer.getEntity().setHelmet(player.getInventory().getHelmet());
			offlinePlayer.getEntity().setChestplate(player.getInventory().getChestplate());
			offlinePlayer.getEntity().setLeggings(player.getInventory().getLeggings());
			offlinePlayer.getEntity().setBoots(player.getInventory().getBoots());
			
			SpawnUtils.playerNPC.put(player.getName(), offlinePlayer);
		}
	}*/
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		if(SpawnUtils.dead.contains(player.getName())) {
			if(SpawnUtils.getRandomSpawn() != null) {
				SpawnUtils.respawnPlayer(player, SpawnUtils.getRandomSpawn());
			}
		}
		if (!player.getGameMode().equals(GameMode.CREATIVE)) {
			NPC offlinePlayer = NPCLib.getNPCRegistry(MasterPlugin.getMasterPlugin()).createLivingNPC("", EntityType.PLAYER);
			offlinePlayer.setProtected(false);
			offlinePlayer.spawn(player.getLocation());
			offlinePlayer.getEntity().setMetadata("name", new FixedMetadataValue(MasterPlugin.getMasterPlugin(), player.getName()));

			PacketPlayOutPlayerInfo info = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) offlinePlayer.getEntity()).getHandle());
			for (Player p : Bukkit.getOnlinePlayers()) {
				((CraftPlayer) p).getHandle().playerConnection.sendPacket(info);
			}

			((HumanEntity) offlinePlayer.getEntity()).getInventory().setContents(player.getInventory().getContents());
			((HumanEntity) offlinePlayer.getEntity()).getInventory().setHelmet(player.getInventory().getHelmet());
			((HumanEntity) offlinePlayer.getEntity()).getInventory().setChestplate(player.getInventory().getChestplate());
			((HumanEntity) offlinePlayer.getEntity()).getInventory().setLeggings(player.getInventory().getLeggings());
			((HumanEntity) offlinePlayer.getEntity()).getInventory().setBoots(player.getInventory().getBoots());

			SpawnUtils.playerNPC.put(player.getName(), offlinePlayer);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final String name = player.getName();
		
		if (SpawnUtils.playerNPC.containsKey(player.getName())) {
			try {
				SpawnUtils.playerNPC.get(player.getName()).despawn();
			} catch (Exception e) { }
			
			SpawnUtils.playerNPC.remove(player.getName());
		}

		if (ConfigUtils.getConfig("data/deadPlayers").getStringList("deadPlayers").contains(player.getName())) {
			SpawnUtils.oldDeaths.add(player);

			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					if(player.isOnline()) {
						List<String> deadPlayers = ConfigUtils.getConfig("data/deadPlayers").getStringList("deadPlayers");
						if(deadPlayers != null) {
							int remove = 0;
							
							for(String all : deadPlayers) {
								if(all.equals(name)) {
									remove++;
								}
							}
							for(int x = 0; x < remove; x++) {
								deadPlayers.remove(name);
							}
							FileConfiguration config = ConfigUtils.getConfig("data/deadPlayers");
							config.set("deadPlayers", deadPlayers);
							ConfigUtils.saveConfig(config, "data/deadPlayers");
						}
						
						StringUtils.sendTitle(player, 5, 40, 5, ChatColor.RED + "ALERT", ChatColor.GRAY + "You died while offline.");
						SpawnUtils.respawnPlayer(player, SpawnUtils.getRandomSpawn());
					}
				}
			}, 5l);
		}
	}

	@EventHandler
	public void onNPCDeath(PlayerDeathEvent event) {
		if(NPCLib.getNPCRegistry(MasterPlugin.getMasterPlugin()).isNPC(event.getEntity())) {
			List<MetadataValue> data = event.getEntity().getMetadata("name");
			MetadataValue name = data.get(0);
			
			List<String> deadPlayers = ConfigUtils.getConfig("data/deadPlayers").getStringList("deadPlayers");
			if(deadPlayers == null) {
				deadPlayers = new ArrayList<String>();
			}
			
			deadPlayers.add(name.asString());
			FileConfiguration config = ConfigUtils.getConfig("data/deadPlayers");
			config.set("deadPlayers", deadPlayers);
			ConfigUtils.saveConfig(config, "data/deadPlayers");
			
			BackpackUtils.createBackpack(event.getEntity().getLocation().getBlock().getLocation(), event.getEntity().getInventory(), name.asString());
			
			event.getEntity().getInventory().clear();
			event.getDrops().clear();
			
			SpawnUtils.playerNPC.remove(name);
			
			try {
				NPCLib.getNPCRegistry(MasterPlugin.getMasterPlugin()).getAsNPC(event.getEntity()).despawn();
			} catch (Exception e) { }
		}
	}
	
	/*@EventHandler(priority=EventPriority.LOWEST)
	public void onNPCDeath(EntityDamageEvent event) {
		NPC npc = NPC.getNPC(event.getEntity());
		
		if(npc != null) {
			if(npc.getHealth() < 0) {
				List<MetadataValue> data = event.getEntity().getMetadata("name");
				String name = data.get(0).asString();
				
				List<String> deadPlayers = ConfigUtils.getConfig("data/deadPlayers").getStringList("deadPlayers");
				if(deadPlayers == null) {
					deadPlayers = new ArrayList<String>();
				}
				
				deadPlayers.add(name);
				FileConfiguration config = ConfigUtils.getConfig("data/deadPlayers");
				config.set("deadPlayers", deadPlayers);
				ConfigUtils.saveConfig(config, "data/deadPlayers");
				
				BackpackUtils.createBackpack(event.getEntity().getLocation().getBlock().getLocation(), npc.getItems(), name);
				
				for(ItemStack armor : npc.getEntity().getEquipment().getArmorContents()) {
					if(armor.getType() != Material.AIR) {
						npc.getEntity().getWorld().dropItemNaturally(npc.getEntity().getLocation(), armor);
					}
				}
				npc.getEntity().getEquipment().clear();
								
				SpawnUtils.playerNPC.remove(name);
				npc.remove();
				
				event.setCancelled(true);
				
				try {
					NPC.getNPC(event.getEntity()).remove();
				} catch (Exception e) { event.getEntity().remove(); }
			}
		}
	}*/
}
