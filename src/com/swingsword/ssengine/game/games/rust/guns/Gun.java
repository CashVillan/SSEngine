package com.swingsword.ssengine.game.games.rust.guns;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.Main;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import com.swingsword.ssengine.MasterPlugin;
import com.swingsword.ssengine.game.games.rust.utils.ItemUtils;
import com.swingsword.ssengine.game.games.rust.utils.ParticleEffect;
import com.swingsword.ssengine.game.games.rust.utils.ParticleEffect.ParticleType;

public class Gun {

	private String name;
	private Material mat;
	private String typeName;
	private String type;
	private double damage;
	private double headshotDamage;
	private int magSize;
	private String ammoName;
	private String ammoType;
	private int bullets;
	private float acc;
	private float knockback;
	private int rpm;
	private int zoom;
	private boolean scope;
	private long reloadt;
	private String sound;
	private boolean bolt;
    
    public Gun(String name, Material material, String typeName, String type, int magazineSize, String ammoName, String ammoType, int bullets, int damage, int headshotDamage, float accurracy, float knockback, int rpm, int zoom, boolean scope, long reloadTime, String sound, boolean bolt) {
        this.name = name;
        this.mat = material;
        this.type = type;
        if(magazineSize <= 127) {
        	this.magSize = magazineSize;
        } else {
        	this.magSize = 127;
        }
        this.ammoName = ammoName;
        this.damage = damage;
        this.headshotDamage = headshotDamage;
        this.ammoType = ammoType;
        this.acc = accurracy;
        this.knockback = knockback;
        this.rpm = rpm;
        this.zoom = zoom;
        this.scope = scope;
        this.reloadt = reloadTime;
        this.bolt = bolt;
        
        GunData.addGun(this);
	}
    
    public Gun(String name, Material material) {
        this.name = name;
        this.mat = material;
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
    
    public void setType(String type) {
    	this.type = type;
    }
    
    public void setSound(String sound) {
    	this.sound = sound;
    }
    
    public void setDamage(double damage) {
    	this.damage = damage;
    }
    
    public void setHeadshotDamage(double damage) {
    	this.headshotDamage = damage;
    }
    
    public void setMagazineSize(int size) {
    	if(size <= 127) {
    		this.magSize = size;
    	} else {
    		this.magSize = 127;
    	}
    }
    
    public void setAmmoName(String type) {
    	this.ammoName = type;
    }
    
    public void setAmmoType(String type) {
    	this.ammoType = type;
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
    
    public void load() {
    	GunData.addGun(this);
    }
    
    //GET
    public String getName() {
    	return this.name;
    }
    
    public Material getMaterial() {
    	return this.mat;
    }
    
    public String getGunTypeName() {
    	return this.typeName;
    }
    
    public String getGunType() {
    	return this.type;
    }
    
    public double getDamage() {
    	return this.damage;
    }
    
    public double getHeadshotDamage() {
    	return this.headshotDamage;
    }
    
    public int getMagazineSize() {
    	return this.magSize;
    }
    
    public String getAmmoName() {
    	return this.ammoName;
    }
    
    public String getAmmoType() {
    	return this.ammoType;
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
    
    public boolean getBolt() {
    	return this.bolt;
    }
    
    public void setBolt(boolean bolt) {
    	this.bolt = bolt;
    }
    
	public void shoot(final Player player, final Gun gun) {
		if(!player.isDead() && !player.getEyeLocation().add(player.getLocation().getDirection()).getBlock().getType().isSolid() && !player.getLocation().clone().add(0, 0.5, 0).getBlock().getType().isSolid()) {
			float spread = gun.getAccurracy() / 50;
			Location loc = player.getLocation().clone();
			Vector vec = player.getVelocity().clone();
			float knockback = gun.getKnockback();
			
			if(ItemUtils.getLore(player.getItemInHand()) != null) {
				if(ItemUtils.loreContains(ItemUtils.getLore(player.getItemInHand()), "Silencer")) {
					playSound(player, gun.getSound() + ".sfire", 0);
					
				} else {
					playSound(player, gun.getSound() + ".fire", 50);
				}
			} else {
				playSound(player, gun.getSound() + ".fire", 50);
			}
			
			ItemStack newGun = player.getItemInHand();
			ItemMeta meta = newGun.getItemMeta();
			meta.setDisplayName(gun.getName() + " " + (Ammo.getAmmoLeft(player, gun) - 1));
			newGun.setItemMeta(meta);
			
			GunData.delay.put(player, (1 / (gun.getRPM() * (float) 1 / 60)) * 20);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
				public void run() {
					if(gun.getAmmoType() == AmmoType.CLIP()) {
						Random random = new Random();
						playSound(player, "custom.shell" + random.nextInt(6), 15);
					}
				}
			}, 10L);
			
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
			    final Arrow bullet = player.getWorld().spawnArrow(player.getEyeLocation().add(0, -0.1, 0).add(player.getLocation().getDirection().normalize().multiply(1)), player.getLocation().getDirection(), 4, spread);
			    //final Snowball bullet = ((Snowball) player.launchProjectile(Snowball.class));
				bullet.setShooter(player);
				
			    Location loc1 = bullet.getLocation();
			    loc1.setY(loc1.getY() + 0.5);
			    
			    bullet.teleport(loc1);
			    
			    spread(bullet, spread);
			    GunData.bullets.put(bullet, gun);
			    GunData.bulletVel.put(bullet, bullet.getVelocity().multiply(3));
			    
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
			    
			    ParticleEffect effect = new ParticleEffect(ParticleType.SUSPENDED_DEPTH, 0, 1, 0);
				for(int y = 1; y <= 10; y++) {
					effect.sendToLocation(player.getEyeLocation().add(player.getEyeLocation().getDirection().multiply(0.5)).add(bullet.getVelocity().multiply(y * 4)));
				}
			}
			
			if(gun.getKnockback() != 0) {
				player.teleport(loc);
				player.setVelocity(vec.add(new Vector(-loc.getDirection().getX() * (knockback / 20), 0.05, -loc.getDirection().getZ() * (knockback / 20))));
			}
		} else {
			GunData.looping.remove(player);
		}
    }
    
	public void reload(final Player player, final Gun gun) {
	    if(canReload(player)) {
	    	final int ammoSlot = Ammo.getAmmoSlot(player, gun);
	    	final int neededAmmo = gun.getMagazineSize() - Ammo.getAmmoLeft(player, gun);
	    	final int gunAmmo = Ammo.getAmmoLeft(player, gun);
	    	final int gunSlot = getSlot(player, player.getItemInHand());
	    	//final int totalAmmo = getTotalAmmo(gun, player.getInventory().getItem(ammoSlot).getAmount(), gunAmmo);
	    	
	    	if(ammoSlot != -1) {
	    		GunData.reloading.put(player, player.getInventory().getItem(ammoSlot).clone());
				
				ItemMeta meta = player.getItemInHand().getItemMeta();
				meta.setDisplayName(gun.getName() + " 0");
				player.getItemInHand().setItemMeta(meta);
				
				runReloadBar(player, gun, (gun.getReloadTime() * 20), (gun.getReloadTime() * 20), gunSlot);
				
				playSound(player, gun.getSound() + ".reload", 20);
				
				Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
					public void run() {
						if(player.isOnline()) {
							if(Gun.isGun(player.getItemInHand()) && Ammo.isAmmo(player.getInventory().getItem(ammoSlot))) {
								if(Gun.getGun(player.getItemInHand().getItemMeta().getDisplayName()).equals(gun)) {
									GunData.reloading.remove(player);
									
									int remove = 0;
									if(player.getInventory().getItem(ammoSlot).getAmount() - neededAmmo < 0) {
										remove = player.getInventory().getItem(ammoSlot).getAmount() - neededAmmo;
									}

									if(player.getInventory().getItem(ammoSlot).getAmount() - neededAmmo > 0) {
										player.getInventory().getItem(ammoSlot).setAmount(player.getInventory().getItem(ammoSlot).getAmount() - neededAmmo);
										
									} else {
										player.getInventory().setItem(ammoSlot, null);
									}
									
									ItemMeta meta = player.getItemInHand().getItemMeta();
									meta.setDisplayName(gun.getName() + " " + (gun.getMagazineSize() + remove));
									player.getItemInHand().setItemMeta(meta);
									
								} else {
									ItemMeta meta = player.getInventory().getItem(gunSlot).getItemMeta();
									meta.setDisplayName(gun.getName() + " " + gunAmmo);
									player.getInventory().getItem(gunSlot).setItemMeta(meta);
										
									GunData.reloading.remove(player);
									player.updateInventory();
									
									player.sendMessage(ChatColor.RED + "You switched items while reloading.");
								}
							} else {
								if(player.getInventory().getItem(gunSlot) != null && player.getInventory().getItem(gunSlot).getItemMeta() != null) {
									ItemMeta meta = player.getInventory().getItem(gunSlot).getItemMeta();
									meta.setDisplayName(gun.getName() + " " + gunAmmo);
									player.getInventory().getItem(gunSlot).setItemMeta(meta);
									
									GunData.reloading.remove(player);
									player.updateInventory();
								}
							}
						}
					}
				}, gun.getReloadTime() * 20);
	    	}
    	}
    }
    
    /*ItemStack oldGun = newGun;
	ItemMeta meta = newGun.getItemMeta();
	meta.setDisplayName(gun.getName() + " | " + gunAmmo);
	newGun.setItemMeta(meta);*/
	
    public ItemStack toItemStack() {
    	ItemStack gunItem = new ItemStack(this.mat);
    	List<String> lore = new ArrayList<String>();
    	
    		lore = Arrays.asList("Magazine size: %ms", "Ammo Type: %ammo", "Damage: %d");
    	
    	List<String> finalLore = new ArrayList<String>();
    	ItemMeta gunItemMeta = gunItem.getItemMeta();
    	
    	gunItemMeta.setDisplayName(this.name + " 0");
    	for(String all : lore) {
    		boolean add = true;
    		String blackAll = ChatColor.GRAY + all;
    		String finalAll = blackAll.replace("%type", this.typeName + "").replace("%ms", this.magSize + "").replace("%acc", Gun.getBar((int) this.getAccurracy(), 10, true, ChatColor.GREEN, ChatColor.GRAY)).replace("%ammo", this.ammoName + "").replace("%k", Gun.getBar((int) this.getKnockback(), 6, false, ChatColor.RED, ChatColor.GRAY)).replace("%d", (int) (this.getDamage() * 5) + "").replace("%rpm", this.rpm + "").replace("%rt", Gun.getBar((int) this.getReloadTime(), 5, true, ChatColor.GREEN, ChatColor.GRAY));
    		
    		if(add == true) {
    			finalLore.add(finalAll);
    		}
    	}
    	gunItemMeta.setLore(finalLore);
    	gunItem.setItemMeta(gunItemMeta);
    	
    	return gunItem;
    }
    
    public static boolean isGun(ItemStack item) {
    	boolean isGun = false;
    	
    	if(item != null) {
	    	if(item.getItemMeta() != null) {
	    		if(item.getItemMeta().getDisplayName() != null) {
	    			String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
	    			
	    			for(Gun all : GunData.guns) {
	    				if(name.split(" ").length > 0 && isInteger(name.split(" ")[name.split(" ").length - 1])) {
	    					String newName = "";
	    					for(String s : name.split(" ")) {
	    						if(!s.equals(name.split(" ")[name.split(" ").length - 1])) {
	    							if(newName.equals("")) {
	    								newName = s;
	    										
	    							} else {
	    								newName = newName + " " + s;
	    							}
	    						}
	    					}
	    					
	    					if(all.getName().equals(newName)) {
	    						isGun = true;
	    					}
	    					
	    				} else {
	    					if(all.getName().equals(name)) {
	    						isGun = true;
	    					}
	    				}
	    			}
	    		}
	    	}
    	}
    	
    	return isGun;
    }
    
    public static Gun getGun(String name) {
		Gun gun = null;
		
		if(name != null) {
			for(Gun all : GunData.guns) {
				if(isInteger(name.split(" ")[name.split(" ").length - 1])) {
					String newName = "";
					for(String s : name.split(" ")) {
						if(!s.equals(name.split(" ")[name.split(" ").length - 1])) {
							if(newName.equals("")) {
								newName = s;
										
							} else {
								newName = newName + " " + s;
							}
						}
					}
					
					if(all.getName().equals(newName)) {
						gun = all;
					}
					
				} else {
					if(all.getName().equals(name)) {
						gun = all;
					}
				}
			}
		}
		return gun;
	}
    
    public boolean isGun(String name) {
		boolean gun = false;

		if(name != null) {
			for(Gun all : GunData.guns) {
				if(isInteger(name.split(" ")[name.split(" ").length - 1])) {
					String newName = "";
					for(String s : name.split(" ")) {
						if(!s.equals(name.split(" ")[name.split(" ").length - 1])) {
							if(newName.equals("")) {
								newName = s;
										
							} else {
								newName = newName + " " + s;
							}
						}
					}
					
					if(all.getName().equals(newName)) {
						gun = true;
					}
					
				} else {
					if(all.getName().equals(name)) {
						gun = true;
					}
				}
			}
		}
		return gun;
	}
    
    public static boolean canShoot(Player player, Gun gun) {
    	boolean shoot = false;
    	
		if(!GunData.reloading.containsKey(player) && !GunData.delay.containsKey(player) && Ammo.getAmmoLeft(player, gun) > 0) {
			shoot = true;
		}
		return shoot;
	}
    
    public static boolean canReload(Player player) {
    	boolean reload = false;
    	
    	if(isGun(player.getItemInHand())) {
    		Gun gun = getGun(player.getItemInHand().getItemMeta().getDisplayName());
    		
			if(!GunData.reloading.containsKey(player) && Ammo.hasAmmoFor(player, gun) && Ammo.getAmmoLeft(player, gun) != gun.getMagazineSize()) {
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
    
    public void runReloadBar(final Player player, final Gun gun, final long time, final long progress, final int slot) {
    	Bukkit.getScheduler().scheduleSyncDelayedTask(MasterPlugin.getMasterPlugin(), new Runnable() {
    		public void run() {
    			if(player.isOnline()) {
    				if(isGun(player.getInventory().getItem(slot)) && Gun.getGun(player.getInventory().getItem(slot).getItemMeta().getDisplayName()) == gun) {
    					if(progress <= 0) {
    						
    						player.getInventory().getItem(slot).setDurability((short) 0);
    						player.updateInventory();
    						
    					} else {
    						player.getInventory().getItem(slot).setDurability((short) (player.getInventory().getItem(slot).getType().getMaxDurability() * (float) ((float) progress / (float) time)));
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
    
    public void spread(Projectile p, float spread) {
    	Vector v = p.getVelocity().clone();
    	
    	v.setX(v.getX() + ((new Random().nextFloat() * spread) - (new Random().nextFloat() * spread)));
    	v.setY(v.getY() + ((new Random().nextFloat() * spread) - (new Random().nextFloat() * spread)));
    	v.setZ(v.getZ() + ((new Random().nextFloat() * spread) - (new Random().nextFloat() * spread)));
    	
    	p.setVelocity(v);
    }
}
