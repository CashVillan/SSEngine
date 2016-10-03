package com.swingsword.ssengine.bar;

public class UtilTextTop 
{
	/*//Base Commands
	public static void display(String text, Player... players)
	{
		displayProgress(text, 1, players);
	}
	
	public static void displayProgress(String text, double progress, Player... players)
	{
		for (Player player : players)
			displayTextBar(player, progress, text);
	}
	
	//Logic
	public static final int EntityDragonId = 777777;
	public static final int EntityWitherId = 777778;
	
	//Display
	public static void displayTextBar(final Player player, double healthPercent, String text)
	{
		deleteOld(player);
		
		healthPercent = Math.min(1, healthPercent);
		boolean halfHealth = true;
	
		//Display Wither (as well as Dragon)
		if (true)
		{
			Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(24));
			
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(getWitherPacket(text, healthPercent, halfHealth, loc));
		}
		
		//Remove
		Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], new Runnable()
		{
			public void run()
			{
				deleteOld(player);
			}
		}, 20);
	}

	private static void deleteOld(Player player)
	{
		if (true)
		{
			PacketPlayOutEntityDestroy destroyWitherPacket = new PacketPlayOutEntityDestroy(EntityWitherId);
			((CraftPlayer)player).getHandle().playerConnection.sendPacket(destroyWitherPacket); 
		}
	}
	
	private static Field getField(Object packet, String declaredField)
	{
		try 
		{
			Field field = packet.getClass().getDeclaredField(declaredField);
			field.setAccessible(true);
			return field;
		} 
		
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static PacketPlayOutSpawnEntityLiving getWitherPacket(String text, double healthPercent, boolean halfHealth, Location loc)
	{
		PacketPlayOutSpawnEntityLiving mobPacket = new PacketPlayOutSpawnEntityLiving();

		try {
			getField(mobPacket, "a").set(mobPacket, (int) EntityWitherId);
			getField(mobPacket, "b").set(mobPacket, (int) EntityType.WITHER.getTypeId());
			getField(mobPacket, "c").set(mobPacket, (int) Math.floor(loc.getBlockX() * 32.0D));
			getField(mobPacket, "d").set(mobPacket, (int) MathHelper.floor(loc.getBlockY() * 32.0D));
			getField(mobPacket, "e").set(mobPacket, (int) Math.floor(loc.getBlockZ() * 32.0D));
			getField(mobPacket, "f").set(mobPacket, (int) 0);
			getField(mobPacket, "g").set(mobPacket, (int) 0);
			getField(mobPacket, "h").set(mobPacket, (int) 0);
			getField(mobPacket, "i").set(mobPacket, (byte) 0);
			getField(mobPacket, "j").set(mobPacket, (byte) 0);
			getField(mobPacket, "k").set(mobPacket, (byte) 0);
		
			//Health
			double health = healthPercent * 299.9 + 0.1;
			//if (halfHealth)
			//	health = healthPercent * 149 + 151;
			 
			//Watcher
			DataWatcher watcher = getWatcher(text, health, loc.getWorld());
			getField(mobPacket, "l").set(mobPacket, watcher);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return mobPacket;
	}
	
	public static DataWatcher getWatcher(String text, double health, org.bukkit.World world) {
		DataWatcher watcher = new DataWatcher(new DummyEntity(((CraftWorld) world).getHandle()));

		watcher.register(this, (Byte) (byte) 0);
		watcher.a(0, (Byte) (byte) 0); // Flags, 0x20 = invisible
		watcher.a(6, (Float) (float) health);
		watcher.a(2, (String) text); // Entity name
		watcher.a(10, (String) text); // Entity name
		watcher.a(3, (Byte) (byte) 0); // Show name, 1 = show, 0 = don't show
		watcher.a(11, (Byte) (byte) 0); // Show name, 1 = show, 0 = don't show
		watcher.a(16, (Integer) (int) health); // Health
		watcher.a(20, (Integer) (int) 881); // Inv

		byte i1 = watcher.getByte(0);
		watcher.watch(0, Byte.valueOf((byte) (i1 | 1 << 5)));

		return watcher;
	}*/
}
