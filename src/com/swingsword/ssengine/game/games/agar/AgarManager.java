package com.swingsword.ssengine.game.games.agar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.Main;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.utils.ScoreboardUtils;

import net.minecraft.server.v1_10_R1.NBTTagCompound;

public class AgarManager implements Listener {

	public static HashMap<Player, Slime> agaring = new HashMap<Player, Slime>();
	public static HashMap<Player, List<Slime>> splitSlimes = new HashMap<Player, List<Slime>>();
	public static HashMap<Entity, Integer> mass = new HashMap<Entity, Integer>();
	public static HashMap<Slime, ArmorStand> holograms = new HashMap<Slime, ArmorStand>();
	public static ArrayList<Player> stopped = new ArrayList<Player>();
	public static ArrayList<Slime> noTeleport = new ArrayList<Slime>();
	public static ArrayList<Entity> delay = new ArrayList<Entity>();
	
	public static ArrayList<Entity> food = new ArrayList<Entity>();
	public static ArrayList<Entity> virus = new ArrayList<Entity>();
	
	public static int mapSize = 200;
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if(agaring.values().contains(event.getEntity()) || agaring.keySet().contains(event.getEntity())) {
			event.setCancelled(true);
		}
	}
	
	public static void die(Player player) {
		player.teleport(new Location(player.getWorld(), 0, 64, 0));
		PlayerStats stats = PlayerStats.getStats(player);
		
		ItemStack item = new ItemStack(Material.EMERALD_BLOCK);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + "Match Results");
		List<String> lore = new ArrayList<String>();
		lore.add(ChatColor.GRAY + "Food Eaten: " + stats.foodEaten);
		lore.add(ChatColor.GRAY + "Highest Mass: " + stats.highestMass);
		lore.add(ChatColor.GRAY + "Time Played: " + Agar.getTimeString(stats.timeAlive / (20 / ThreadManager.updateSpeed)));
		lore.add(ChatColor.GRAY + "Leaderboard Time: " + Agar.getTimeString(stats.leaderboardTime));
		lore.add(ChatColor.GRAY + "Cells Eaten: " + stats.cellsEaten);
		lore.add(ChatColor.GRAY + "Top Position: " + stats.topPosition);
		lore.add("");
		lore.add(ChatColor.YELLOW + "Click to Continue!");
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		Inventory inv = Bukkit.createInventory(null, 27, "Agar - " + player.getName());
		for(int x = 10; x < 17; x++) {
			inv.setItem(x, item);
		}
		player.openInventory(inv);
	}
	
	public static void spawnPlayer(Player player) {
		int x = new Random().nextInt(mapSize + 1);
		int z = new Random().nextInt(mapSize + 1);
		Location spawn = new Location(Bukkit.getWorld("map"), x + 0.5f, Bukkit.getWorld("map").getHighestBlockYAt(x, z), z + 0.5f);
		
		if(AgarManager.agaring.containsKey(player)) {
			stopAgar(player);
		}
		
		player.teleport(spawn.clone().add(0, 15, 0));
		AgarManager.agaring.put(player, makeNewSlime(spawn, 10));
		AgarManager.splitSlimes.put(player, new ArrayList<Slime>());
		player.setAllowFlight(true);
		player.setFlying(true);
		updateHeight(player, AgarManager.agaring.get(player));
		player.setAllowFlight(true);
		player.setFlying(true);
		new PlayerStats(player);
	}
	
	public static int getTotalMass(Player player) {
		int total = 0;
		
		if(player.isOnline()) {
			total = mass.get(agaring.get(player));
			for(Entity all : splitSlimes.get(player)) {
				total += mass.get(all);
			}
		}
		
		return total;
	}
	
	public static void stopAgar(Player player) {
		destroySlime(AgarManager.agaring.get(player));
		
		AgarManager.agaring.get(player).remove();
		AgarManager.agaring.remove(player);
		
		List<Slime> slimes = new ArrayList<Slime>();
		for(Slime all : AgarManager.splitSlimes.get(player)) {
			slimes.add(all);
		}
		for(Slime all : slimes) {
			destroySlime(all);
		}
		
		AgarManager.splitSlimes.remove(player);
		player.setAllowFlight(false);
		PlayerStats.removeStats(player);
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			for(String entry : all.getScoreboard().getEntries()) {
				if(entry.contains(player.getName())) {
					ScoreboardUtils.removeScore(all, entry);
				}
			}
		}
	}
	
	public static void updateHeight(Player player, Slime slime) {
		if(AgarManager.mass.get(slime) != null) {
			if(AgarManager.getSize(AgarManager.mass.get(slime)) != slime.getSize()) {
				Location playerLoc = slime.getLocation().clone().add(0, slime.getSize() + 15, 0);
				playerLoc.setPitch(player.getLocation().getPitch());
				playerLoc.setYaw(player.getLocation().getYaw());
				
				player.teleport(playerLoc);
				player.setAllowFlight(true);
				player.setFlying(true);
			}
			
			holograms.get(slime).teleport(slime.getLocation().clone().add(0, (getSize(mass.get(slime)) / 2), 0));
			
		} else {
			AgarManager.spawnPlayer(player);
		}
	}
	
	public static Player getOwner(Entity ent) {
		for(Player all : agaring.keySet()) {
			if(agaring.get(all).equals(ent)) {
				return all;
			}
		}
		for(Player all : splitSlimes.keySet()) {
			if(splitSlimes.get(all).contains(ent)) {
				return all;
			}
		}
		return null;
	}
	
	public static int getSize(int mass) {
		return (int) Math.pow(mass, 0.35f);
	}
	
	public static float getSpeed(int mass) {
		float speed = (float) (0.5f - (float) Math.pow((float) mass * 0.00015f, 0.5f));
		if(speed < 0.09) {
			speed = 0.09f;
		}
		
		return speed;
	}
	
	public static int getShrinkSpeed(int mass) {
		double speed = Math.pow(mass, 0.35) - 7;
		
		if(speed > 0) {
			return (int) (speed + 0.5);
		} else {
			return 0;	
		}
	}
	
	public static List<Slime> split(Player player) {
		List<Slime> toAdd = new ArrayList<Slime>();
		
		Slime mainSlime = AgarManager.agaring.get(player);
	    Vector vec = new Vector(player.getLocation().getDirection().getX(), 0, player.getLocation().getDirection().getZ());

		for(Slime slime : AgarManager.splitSlimes.get(player)) {
			if(AgarManager.mass.get(slime) >= 36 && splitSlimes.get(player).size() < 15) {
			    final Slime newSlime = makeNewSlime(slime.getLocation().clone(), AgarManager.mass.get(slime) / 2);
			    AgarManager.mass.put(slime, AgarManager.mass.get(slime) / 2);
				
				toAdd.add(newSlime);
			    delay(newSlime, 600 + ((7 / 3) * AgarManager.mass.get(slime)));
			    
				AgarManager.noTeleport.add(newSlime);
				Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						AgarManager.noTeleport.remove(newSlime);
					}
				}, 8);
				launch(newSlime, 20, vec.clone().multiply(1f));
			}
		}
		
		if(mainSlime != null && AgarManager.mass.get(mainSlime) >= 36 && splitSlimes.get(player).size() < 15) {
		    final Slime newSlime = makeNewSlime(mainSlime.getLocation().clone(), AgarManager.mass.get(mainSlime) / 2);
			AgarManager.mass.put(mainSlime, AgarManager.mass.get(mainSlime) / 2);
			
			toAdd.add(newSlime);
			delay(newSlime, 600 + (((7 / 3) / 100) * AgarManager.mass.get(mainSlime)));

			AgarManager.noTeleport.add(newSlime);
			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					AgarManager.noTeleport.remove(newSlime);
				}
			}, 8);
			launch(newSlime, 20, vec.clone().multiply(1f));
		}
		
		return toAdd;
	}
	
	public static List<Slime> explode(Slime slime) {
		int netto = 50;
		int amount = 6 + (int) (AgarManager.mass.get(slime) / 300);
		int childSize = AgarManager.mass.get(slime) / (amount + 1);
		List<Slime> toAdd = new ArrayList<Slime>();

		for(int x = 0; x < amount; x++) {
			netto -= childSize;
			
			final Slime newSlime = makeNewSlime(slime.getLocation().clone(), childSize);
			
			toAdd.add(newSlime);
			delay(newSlime, 600 + (((7 / 3) / 100) * childSize));
			
			AgarManager.noTeleport.add(newSlime);
			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					AgarManager.noTeleport.remove(newSlime);
				}
			}, 20);
			launch(newSlime, 20, new Vector(Math.sin((float) 2 * (float) Math.PI * (float) ((float) x / (float) amount)), 0, Math.cos((float) 2 * (float) Math.PI * (float) ((float) x / (float) amount))).multiply((float) 0.25f));
		}
		
		AgarManager.mass.put(slime, AgarManager.mass.get(slime) + netto);

		return toAdd;
	}
	
	public static void feed(Player player) {
		Slime slime = agaring.get(player);
		
		if(mass.get(slime) >= 35) {
		    Vector vec = new Vector(player.getLocation().getDirection().getX(), 0, player.getLocation().getDirection().getZ());
	
			Slime food = (Slime) slime.getWorld().spawnEntity(slime.getLocation(), EntityType.SLIME);
			food.setSize(2);
			setNoAI(food);
			AgarManager.mass.put(slime, AgarManager.mass.get(slime) - 16);
			AgarManager.mass.put(food, 12);
			AgarEntity.agarEntities.add(new AgarEntity(food, false, true));
			
			launch(food, 20, vec.clone().multiply(1f));
			
			delay(food, 10);
		}
	}
	
	public static Slime makeNewSlime(Location loc, int mass) {
		Slime slime = (Slime) loc.getWorld().spawnEntity(loc, EntityType.SLIME);
		slime.setSize(getSize(mass));
		AgarManager.mass.put(slime, mass);
		
		setNoAI(slime);
	    
		ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
		stand.setVisible(false);
		stand.setBasePlate(false);
		stand.setGravity(false);
		
		//AgarManager.display.put(slime, stand);
		ArmorStand h = (ArmorStand) loc.getWorld().spawnEntity(loc.clone().add(0, 3, 0), EntityType.ARMOR_STAND);
	    h.setCustomName("10");
	    h.setCustomNameVisible(true);
	    h.setBasePlate(false);
	    h.setVisible(false);
	    h.setGravity(false);
		holograms.put(slime, h);
		
		AgarEntity.agarEntities.add(new AgarEntity(slime, true, true));
		
		return slime;
	}
	
	public static void setNoAI(Entity ent) {
		net.minecraft.server.v1_10_R1.Entity nmsEnt = ((CraftEntity) ent).getHandle();
	    NBTTagCompound tag = new NBTTagCompound();
	    nmsEnt.e(tag);
	    
	    nmsEnt.c(tag);
	    tag.setInt("NoAI", 1);
	    nmsEnt.f(tag);
	}
	
	public static void destroySlime(Slime slime) {
		AgarEntity.agarEntities.remove(AgarEntity.getAgarEntity(slime));
		
		slime.remove();
		
		AgarManager.mass.remove(slime);
		
		if(holograms.containsKey(slime)) {
			holograms.get(slime).remove();
			holograms.remove(slime);
		}
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			if(AgarManager.splitSlimes.containsKey(all)) {
				AgarManager.splitSlimes.get(all).remove(slime);
			}
		}
	}
	
	public static Vector getMovementDirection(Player player) {
		float yaw = player.getLocation().getYaw();
		
		if(yaw < 0) {
			yaw = 360 - yaw;
		}
		
		float x;
		float z;
		
		x = (float) Math.sin(((float) 2 * (float) Math.PI) * (float) ((float) yaw / (float) 360));
		z = (float) Math.cos(((float) 2 * (float) Math.PI) * (float) ((float) yaw / (float) 360));
		
		if(x > 0 && player.getLocation().getDirection().getX() < 0) {
			x = -x;
		} else if(x < 0 && player.getLocation().getDirection().getX() > 0) {
			x = -x;
		}
		
		if(z > 0 && player.getLocation().getDirection().getZ() < 0) {
			z = -z;
		} else if(z < 0 && player.getLocation().getDirection().getZ() > 0) {
			z = -z;
		}
		
		return new Vector(x, 0, z);
	}
	
	public static Vector getMovementDirection(Entity child, Entity mother) {
		double x = mother.getLocation().getX() - child.getLocation().getX();
		double z = mother.getLocation().getZ() - child.getLocation().getZ();
		
		if(x > 1) {
			x = 1;
		}
		if(x < -1) {
			x = -1;
		}
		if(z > 1) {
			z = 1;
		}
		if(z < -1) {
			z = -1;
		}
		
		return new Vector(x, 0, z);
	}
	
	//public static void repel(Entity ent1, Entity ent2, int distance) { TODO
	//	if(ent1.getLocation().distance(ent2.getLocation()) <= distance) {
	//		ent2.teleport(ent2.getLocation().add((new Random().nextDouble() * 4) - 2, 0, (new Random().nextDouble() * 4) - 2));
	//	}
	//}
	
	public static void spawnFood(World world) {
		int x = new Random().nextInt(mapSize + 1);
		int z = new Random().nextInt(mapSize + 1);
		Location spawn = new Location(world, x + 0.5f, world.getHighestBlockYAt(x, z) - 1, z + 0.5f);
		
		ArmorStand stand = (ArmorStand) world.spawnEntity(spawn, EntityType.ARMOR_STAND);
		stand.setVisible(false);
		stand.setBasePlate(false);
		stand.setHelmet(new ItemStack(Material.WOOL, 1, (byte) new Random().nextInt(16)));
		
		food.add(stand);
		
		AgarEntity.agarEntities.add(new AgarEntity(stand, false, true));
	}
	
	public static void spawnVirus(World world, Location loc) {
		int x = new Random().nextInt(mapSize + 1);
		int z = new Random().nextInt(mapSize + 1);
		
		Location spawn;
		if(loc == null) {
			spawn = new Location(world, x + 0.5f, world.getHighestBlockYAt(x, z) - 1.5f, z + 0.5f);
		} else {
			spawn = loc;
		}
		
		Guardian ent = (Guardian) world.spawnEntity(spawn, EntityType.GUARDIAN);
		ent.setElder(true);
		
		setNoAI(ent);
		
		virus.add(ent);
		
		AgarEntity.agarEntities.add(new AgarEntity(ent, false, true));
	}
	
	public static void moveToSecondaryCell(Player player, Slime target) {
		Slime oldSlime = agaring.get(player);
		
		
		agaring.put(player, target);
		holograms.put(target, holograms.get(oldSlime));
		splitSlimes.get(player).remove(target);
		
		updateHeight(player, target);
		
		holograms.remove(oldSlime);
	}
	
	public static void launch(final Entity ent, final int left, final Vector direction) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				ent.teleport(ent.getLocation().add(direction.multiply((float) (left + 5) / (float) 20)));
				
				if(left > 0) {
					launch(ent, left - 1, direction);
				}
			}
		}, 1);
	}
	
	public static void delay(final Entity ent, int time) {
		AgarManager.delay.add(ent);
		Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			public void run() {
				AgarManager.delay.remove(ent);
			}
		}, time);
	}
}
