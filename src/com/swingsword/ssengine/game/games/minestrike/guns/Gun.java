package com.swingsword.ssengine.game.games.minestrike.guns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.minestrike.game.CSGOGame;
import com.swingsword.ssengine.game.games.minestrike.utils.ChatUtils;
import com.swingsword.ssengine.game.games.minestrike.utils.ParticleEffect;
import com.swingsword.ssengine.game.games.minestrike.utils.ParticleEffect.ParticleType;
import com.swingsword.ssengine.game.threads.TimerHandler;
import com.swingsword.ssengine.stats.StatManager;
import com.swingsword.ssengine.utils.ItemUtils;

public class Gun {

	private String name;
	private Material mat;
	private String typeName;
	private int magSize;
	private int spawnAmmo;
	private int bullets;
	private float acc;
	private float knockback;
	private int rpm;
	private int zoom;
	private boolean scope;
	private long reloadt;
	private String sound;
	private int killReward;
	private int price;
	private GunType type;
	public float spaceModifier = 1.7f;
	
	private int id;
	private int selectInventorySlot;
	private String team;
	
	private double damage;
	private double headshotDamage;
	private double legDamage;
	private double armourDamage;
	private double armourHeadshotDamage;
    
    public Gun(String name, Material material, int id, int selectInventorySlot, String team) {
        this.name = name;
        this.mat = material;
        this.id = id;
        this.selectInventorySlot = selectInventorySlot;
        this.team = team;
    }
    
    //SET
    public void setName(String name) {
    	this.name = name;
    }
    
    public void setMaterial(Material mat) {
    	this.mat = mat;
    }
    
    public void setTypeName(String type) {
    	this.typeName = type;
    }
    
    public void setType(GunType type) {
    	this.type = type;
    }
    
    public void setSound(String sound) {
    	this.sound = sound;
    }
    
    public void setMagazineSize(int size) {
    	this.magSize = size;
    }
    
    public void setSpawnAmmo(int amount) {
    	this.spawnAmmo = amount;
    }
    
    public void setBullets(int amount) {
    	this.bullets = amount;
    }
    
    public void setAccurracy(float accurracy) {
    	this.acc = accurracy;
    }
    
    public void setKnockback(float knockback) {
    	this.knockback = knockback;
    }
    
    public void setRPM(int rpm) {
    	this.rpm = rpm;
    }
    
    public void setZoom(int zoom, boolean scope) {
    	this.zoom = zoom;
    	this.scope = scope;
    }
    
    public void setReloadTime(long reloadTime) {
    	this.reloadt = reloadTime;
    }
    
    public void setKillReward(int reward) {
    	this.killReward = reward;
    }
    
    public void setPrice(int price) {
    	this.price = price;
    }
    
    public void load() {
    	GunData.addGun(this);
    }
    
    //
    
    public void setDamage(double damage) {
    	this.damage = damage;
    }
    
    public void setHeadshotDamage(double damage) {
    	this.headshotDamage = damage;
    }
    
    public void setLegDamage(double damage) {
    	this.legDamage = damage;
    }
    
    public void setArmourDamage(double damage) {
    	this.armourDamage = damage;
    }
    
    public void setArmourHeadshotDamage(double damage) {
    	this.armourHeadshotDamage = damage;
    }
    
    //GET
    public String getName() {
    	if(this instanceof SkinnedGun) {
			SkinnedGun sGun = (SkinnedGun) this;
			
			return this.name + " | " + sGun.getSkin();
			
		} else {
			return this.name;
		}
    }
    
    public Material getMaterial() {
    	return this.mat;
    }
    
    public String getGunTypeName() {
    	return this.typeName;
    }
    
    public GunType getGunType() {
    	return this.type;
    }
    
    public int getMagazineSize() {
    	return this.magSize;
    }
    
    public int getSpawnAmmo() {
    	return this.spawnAmmo;
    }
    
    public int getBullets() {
    	return this.bullets;
    }
    
    public float getAccurracy() {
    	return this.acc;
    }
    
    public float getKnockback() {
    	return this.knockback;
    }
    
    public int getRPM() {
    	return this.rpm;
    }
    
    public int getZoom() {
    	return this.zoom;
    }
    
    public boolean getScope() {
    	return this.scope;
    }
    
    public long getReloadTime() {
    	return this.reloadt;
    }
    
    public String getSound() {
    	return this.sound;
    }
    
    public int getKillReward() {
    	return killReward;
    }
    
    public int getPrice() {
    	return price;
    }
    
    public int getId() {
    	return id;
    }
    
    public int getSelectionSlot() {
    	return selectInventorySlot;
    }
    
    public String getTeam() {
    	return team;
    }
    
    //
    
    public double getDamage() {
    	return this.damage;
    }
    
    public double getHeadshotDamage() {
    	return this.headshotDamage;
    }
    
    public double getLegDamage() {
    	return this.legDamage;
    }
    
    public double getArmourDamage() {
    	return this.armourDamage;
    }
    
    public double getArmourHeadshotDamage() {
    	return this.armourHeadshotDamage;
    }
    
    //
    
	@SuppressWarnings("deprecation")
	public void shoot(final Player player, final Gun gun, boolean delay) {
		if(!player.isDead() && Gun.canShoot(player, gun, false) && !player.getEyeLocation().add(player.getLocation().getDirection()).getBlock().getType().isSolid() && !player.getLocation().clone().add(0, 0.5, 0).getBlock().getType().isSolid()) {
			float spread = gun.getAccurracy() / 50;
			Location loc = player.getLocation().clone();
			
			float knockback = gun.getKnockback();
			double speed = 0.1 * (player.getLocation().distance(GunListener.playerOldLoc.get(player.getName())) * 100);
			
			String owner = "";
			for(String s : ItemUtils.getDisplayName(player.getItemInHand()).split(" ")) {
				if(s.contains("'s")) {
					owner = s + " ";
				}
			}
			
			if(GunListener.playerOldLoc.containsKey(player.getName())) {
				speed = loc.distance(GunListener.playerOldLoc.get(player.getName()));
				
				if(speed < 0.1) {
					speed = 0.1;
				}
				if(speed > 2) {
					speed = 2;
				}
			}
			
			if(!player.isOnGround()) {
				speed = speed * 2;
			}
			
			spread = (float) (spread * (speed * 10));
			
			if(!GunListener.autoing.containsKey(player.getName())) {
				spread /= 2.5f;
			}
			
			playSound(player, gun.getSound(), 30);
			
			ItemStack newGun = player.getItemInHand();
			ItemMeta meta = newGun.getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + owner + gun.getName() + " " + (Ammo.getAmmoLeft(player, gun, player.getItemInHand()) - 1) + "/" + Ammo.getTotalAmmoLeft(player, gun));
			newGun.setItemMeta(meta);
			
			if(Ammo.getAmmoLeft(player, gun, player.getItemInHand()) - 1 > 127) {
				newGun.setAmount(1);
			} else {
				newGun.setAmount(Ammo.getAmmoLeft(player, gun, player.getItemInHand()));
			}
			
			if(delay) {
				GunData.delay.put(player, (1 / ((gun.getRPM() / 2) * (float) 1 / 60)) * 20);
			}
			
			if(GunData.zoomed.containsKey(player)) {
				spread = spread - GunData.zoomed.get(player).getZoom();
				knockback = knockback - (GunData.zoomed.get(player).getZoom() / 2);
				if(spread < 0) {
					spread = 0;
				}
			}
			
			loc.setYaw((float) (loc.getYaw() + ((Math.random() * 2) - 1) * knockback));
			loc.setPitch((float) (loc.getPitch() + (Math.random() - 1) * knockback));
			
			for(int x = gun.bullets; x > 0; x--) {
			    //final Arrow bullet = player.getWorld().spawnArrow(player.getEyeLocation().add(0, -0.1, 0).add(player.getLocation().getDirection().normalize().multiply(1)), player.getLocation().getDirection(), 4, spread);
				//final Snowball bullet = (Snowball) player.launchProjectile(Snowball.class);
				final Snowball bullet = player.launchProjectile(Snowball.class);
				bullet.teleport(bullet.getLocation().clone().add(0, 0.25f, 0));
			    spread(bullet, spread);
				bullet.setShooter(player);
			    GunData.bullets.put(bullet, gun);
			    GunData.bulletVel.put(bullet, bullet.getVelocity().multiply(10));
			    
			    ParticleEffect effect = new ParticleEffect(ParticleType.SUSPENDED_DEPTH, 0, 1, 0);
				for(int y = 1; y <= 10; y++) {
					effect.sendToLocation(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.5)).add(bullet.getVelocity().multiply(y * 4)));
				}
			    
			    int delete = 100;
			    
			    if(this.getName().toLowerCase().contains("shotgun")) {
			    	delete = 2;
			    }
			    
			    Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
			    	public void run() {
			    		if(GunData.bullets.containsKey(bullet) && !bullet.isDead()) {
			    			GunData.bullets.remove(bullet);
			    			GunData.bulletVel.remove(bullet);
			    			bullet.remove();
			    		}
			    	}
			    }, delete);
			}
			
			/*if(gun.getKnockback() != 0) {
				player.teleport(loc);
				
				//player.setVelocity(vec.add(new Vector(-loc.getDirection().getX() * (knockback / 20), 0.05, -loc.getDirection().getZ() * (knockback / 20))));
				
				Bukkit.broadcastMessage(GunListener.playerOldLoc.get(player).toVector().subtract(player.getLocation().toVector()) + "");
				
				player.setVelocity(GunListener.playerOldLoc.get(player).toVector().subtract(player.getLocation().toVector()));
				player.setSprinting(isSprinting);
				player.setSneaking(isSneaking);
			}*/
			
		} else {
			GunData.looping.remove(player);
		}
		
		StatManager.addStat(player, "cs_shots", 1);
    }
    
	public void reload(final Player player, final Gun gun) {
	    if(canReload(player)) {
	    	final int gunAmmo = Ammo.getAmmoLeft(player, gun, player.getItemInHand());
	    	final int totalGunAmmo = Ammo.getTotalAmmoLeft(player, gun) + gunAmmo;
	    	final int gunSlot = getSlot(player, player.getItemInHand());
	    	
    		GunData.reloading.put(player, gunSlot);
    		GunData.reloadGun.put(player, gun);
    		GunData.reloadGunAmmo.put(player, gunAmmo);
    		GunData.reloadTotalGunAmmo.put(player, totalGunAmmo);

    		String owner = "";
			for(String s : ItemUtils.getDisplayName(player.getItemInHand()).split(" ")) {
				if(s.contains("'s")) {
					owner = s + " ";
				}
			}
			final String finalOwner = owner;
			
			ItemMeta meta = player.getItemInHand().getItemMeta();
			meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + owner + gun.getName() + " 0/" + totalGunAmmo);
			
			player.getItemInHand().setItemMeta(meta);
			player.getItemInHand().setAmount(1);
			
			runReloadBar(player, gun, (gun.getReloadTime() * 20), (gun.getReloadTime() * 20), gunSlot);
			
			playSound(player, gun.getSound() + ".reload", 20);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					if(player.isOnline() && GunData.reloading.containsKey(player)) {
						GunData.reloading.remove(player);
						GunData.reloadGun.remove(player);
						GunData.reloadGunAmmo.remove(player);
						GunData.reloadTotalGunAmmo.remove(player);
						
						ItemMeta meta = player.getItemInHand().getItemMeta();
						
						if(meta != null) {
							if(totalGunAmmo > gun.getMagazineSize()) {
								meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + finalOwner + gun.getName() + " " + gun.getMagazineSize() + "/" + (totalGunAmmo - gun.getMagazineSize()));
								
								if(Ammo.getAmmoLeft(player, gun, player.getItemInHand()) > 127) {
									player.getItemInHand().setAmount(1);
								} else {
									player.getItemInHand().setAmount(gun.getMagazineSize());
									if(gun.getMagazineSize() > 127) {
										player.getItemInHand().setAmount(1);
									} else {
										player.getItemInHand().setAmount(gun.getMagazineSize());
									}
								}
								
							} else {
								meta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + finalOwner + gun.getName() + " " + totalGunAmmo + "/0");
								
								if(Ammo.getAmmoLeft(player, gun, player.getItemInHand()) > 127) {
									player.getItemInHand().setAmount(1);
								} else {
									if(totalGunAmmo > 0) {
										player.getItemInHand().setAmount(totalGunAmmo);
									} else {
										player.getItemInHand().setAmount(1);
									}
								}
							}
							player.getItemInHand().setItemMeta(meta);
						}
					}
				}
			}, gun.getReloadTime() * 20);
    	}
    }
	
    public ItemStack toItemStack(boolean displayAmmo, String owner) {
    	ItemStack gunItem = new ItemStack(this.mat);
    	List<String> lore = new ArrayList<String>();
    	
    	//lore = Arrays.asList("Magazine size: %ms", "Damage: %d");
    	lore = Arrays.asList();
    	
    	if(owner != null) {
    		owner += "'s ";
    	} else {
    		owner = "";
    	}
    	
    	List<String> finalLore = new ArrayList<String>();
    	ItemMeta gunItemMeta = gunItem.getItemMeta();
    	
    	if(displayAmmo) {  
    		gunItemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + owner + getName() + " " + this.getMagazineSize() + "/" + this.getSpawnAmmo());
    		
    	} else {
    		gunItemMeta.setDisplayName(ChatColor.WHITE + "" + ChatColor.BOLD + owner + getName());
    	}
    	
    	if(displayAmmo) {
	    	if(this.getMagazineSize() > 127) {
				gunItem.setAmount(1);
			} else {
				gunItem.setAmount(this.getMagazineSize());
			}
    	} else {
    		gunItem.setAmount(1);
    	}
    	
    	for(String all : lore) {
    		boolean add = true;
    		String blackAll = ChatColor.GRAY + all;
    		String finalAll = blackAll.replace("%type", this.typeName + "").replace("%ms", this.magSize + "").replace("%acc", Gun.getBar((int) this.getAccurracy(), 10, true, ChatColor.GREEN, ChatColor.GRAY)).replace("%k", Gun.getBar((int) this.getKnockback(), 6, false, ChatColor.RED, ChatColor.GRAY)).replace("%d", (int) (this.getDamage()) + "").replace("%rpm", this.rpm + "").replace("%rt", Gun.getBar((int) this.getReloadTime(), 5, true, ChatColor.GREEN, ChatColor.GRAY));
    		
    		if(add == true) {
    			finalLore.add(finalAll);
    		}
    	}
    	gunItemMeta.setLore(finalLore);
    	gunItem.setItemMeta(gunItemMeta);
    	
    	return gunItem;
    }
    
    public void copyData(Gun from) {
		setType(from.getGunType());
		setMagazineSize(from.getMagazineSize());
		setSpawnAmmo(from.getSpawnAmmo());
		setBullets(from.getBullets());
		setAccurracy(from.getAccurracy());
		setKnockback(from.getKnockback());
		setRPM(from.getRPM());
		setZoom(getZoom(), getScope());
		setReloadTime(from.getReloadTime());
		setPrice(from.getPrice());
		setKillReward(from.getKillReward());
		setSound(from.getSound());
		//
		setDamage(from.getDamage());
		setHeadshotDamage(from.getHeadshotDamage());
		setLegDamage(from.getLegDamage());
		setArmourDamage(from.getArmourDamage());
		setArmourHeadshotDamage(from.getArmourHeadshotDamage());
    }
    
    //Static methods
    
    public static boolean isGun(ItemStack item) {
    	if(item != null) {
	    	if(item.getItemMeta() != null) {
	    		if(item.getItemMeta().getDisplayName() != null) {
	    			String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
	    			
	    			List<String> remove = new ArrayList<String>();
	    			for(String s : name.split(" ")) {
	    				if(isInteger(s.replace("/", ""))) {
	    					remove.add(" " + s);
	    				}
	    				if(s.contains("'s")) {
	    					remove.add(s + " ");
	    				}
	    			}
	    			for(String s : remove) {
	    				name = name.replace(s, "");
	    			}
	    			
    				return Gun.getGun(name) != null;
	    		}
	    	}
    	}
		return false;
    }
    
    public static Gun getGun(String name) {
		if(name != null) {
			name = ChatColor.stripColor(name);
			
			List<String> remove = new ArrayList<String>();
			for(String s : name.split(" ")) {
				if(isInteger(s.replace("/", ""))) {
					remove.add(" " + s);
				}
				if(s.contains("'s")) {
					remove.add(s + " ");
				}
			}
			for(String s : remove) {
				name = name.replace(s, "");
			}
			
			for(Gun all : GunData.guns) {
				if(all.getName().equals(name)) {
					return all;
				}
			}
		}
		return null;
	}
    
    public static Gun getGun(int id) {
		for(Gun all : GunData.guns) {
			if(all.getId() == id) {
				return all;
			}
		}
		return null;
	}
    
    public static boolean isGun(String name) {
    	if(name != null) {
    		List<String> remove = new ArrayList<String>();
			for(String s : name.split(" ")) {
				if(isInteger(s.replace("/", ""))) {
					remove.add(" " + s);
				}
				if(s.contains("'s")) {
					remove.add(s + " ");
				}
			}
			for(String s : remove) {
				name = name.replace(s, "");
			}
			
			return Gun.getGun(name) != null;
		}
		return false;
	}
    
    @SuppressWarnings("deprecation")
	public static boolean canShoot(Player player, Gun gun, boolean checkDelay) {
    	boolean shoot = false;
    	
		if(!(TimerHandler.getTimer("buytime") != null && TimerHandler.getTimer("buytime").getLeft() >= 5 && CSGOGame.hasStarted())) {
			if(!GunData.reloading.containsKey(player) && Ammo.getAmmoLeft(player, gun, player.getItemInHand()) > 0) {
				if((!GunData.delay.containsKey(player) || !checkDelay)) {
					shoot = true;
				}
				
			} else {
				player.playSound(player.getLocation(), "guns.clipempty", 1, 1);
			}
		}
		
		return shoot;
	}
    
    public static boolean canReload(Player player) {
    	boolean reload = false;
    	
    	if(isGun(player.getItemInHand())) {
    		Gun gun = getGun(player.getItemInHand().getItemMeta().getDisplayName());
    		
			if(!GunData.reloading.containsKey(player) && Ammo.getAmmoLeft(player, gun, player.getItemInHand()) != gun.getMagazineSize() && Ammo.getTotalAmmoLeft(player, gun) > 0) {
				reload = true;
			}
    	}
    	
    	return reload;
	}
    
    public static String getBar(int data, int size, boolean flip, ChatColor color1, ChatColor color2) {
    	String bar = "";
        
    	if(flip == false) {
    		for(int x = 0; x < size; x++) {
    			if(x < data) {
    				bar = bar + color1 + ChatColor.BOLD + ":";
    			} else {
    				bar = bar + color2 + ChatColor.BOLD + ":";
    			}
    		}
    		
    	} else {
    		for(int x = 0; x < size; x++) {
    			if(x >= size - data) {
    				bar = bar + color2 + ChatColor.BOLD + ":";
    			} else {
    				bar = bar + color1 + ChatColor.BOLD + ":";
    			}
    		}
    	}
    	
        return bar;
	}
    
    @SuppressWarnings("deprecation")
	public static void playSound(final Player player, final String sound, int range) {
    	for(final Player all : Bukkit.getOnlinePlayers()) {
    		if(player.getWorld().equals(all.getWorld())) {
    			if(player.getLocation().distance(all.getLocation()) <= range) {
    				all.playSound(all.getLocation(), sound, (float) (-Math.pow(player.getLocation().distance(all.getLocation()) * 0.01, 0.5) + 1), 1);
    			}
    		}
    	}
    }
    
    public static int getSlot(Player player, ItemStack stack) {
    	int item = -1;
    	
    	for(int x = 0; x < player.getInventory().getSize(); x++) {
    		if(player.getInventory().getItem(x) != null) {
	    		if(player.getInventory().getItem(x).equals(stack)) {
	    			item = x;
	    		}
    		}
    	}
    	
    	return item;
    }
    
    public static int getTotalAmmo(Gun gun, int ammoAmount, int neededAmmo) {
    	if(ammoAmount > neededAmmo) {
			return gun.getMagazineSize();
			
		} else {
			return neededAmmo + ammoAmount;
		}
    }
    
    public static void runReloadBar(final Player player, final Gun gun, final long time, final long progress, final int slot) {
    	Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
    		public void run() {
    			if(player.isOnline() && GunData.reloading.containsKey(player)) {
    				if(isGun(player.getInventory().getItem(slot)) && Gun.getGun(player.getInventory().getItem(slot).getItemMeta().getDisplayName()) == gun) {
    					if(progress <= 0) {
    						ChatUtils.sendActionBar(player, "");
    						player.updateInventory();
    						
    					} else {
    						ChatUtils.sendActionBar(player, ChatColor.BOLD + gun.getName() + " Reloading... " + ChatUtils.getProgressbar(15, 1 - ((float) progress / (float) time), ChatColor.GREEN, ChatColor.RED));
    						player.updateInventory();
    						
    						runReloadBar(player, gun, time, progress - 1, slot);
    					}
    				}
    			}
    		}
    	}, 1L);
    }
    
    public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
    
    public static void spread(Projectile p, float spread) {
    	Vector v = p.getVelocity().clone();
    	
    	v.setX(v.getX() + ((new Random().nextFloat() * spread) - (new Random().nextFloat() * spread)));
    	v.setY(v.getY() + ((new Random().nextFloat() * spread) - (new Random().nextFloat() * spread)));
    	v.setZ(v.getZ() + ((new Random().nextFloat() * spread) - (new Random().nextFloat() * spread)));
    	
    	p.setVelocity(v);
    }
}
