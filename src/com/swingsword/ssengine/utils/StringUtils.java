package com.swingsword.ssengine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import com.swingsword.ssengine.player.PlayerSessionManager;

import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;
import net.minecraft.server.v1_10_R1.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_10_R1.PacketPlayOutTitle.EnumTitleAction;
import net.minecraft.server.v1_10_R1.PlayerConnection;

public class StringUtils {
	
	public static String formatSex(String input) {
		if (input.startsWith("f")) {
			return "female";
		}
		return "male";
	}
	
	public static String getFormat(Player p) {
		String level = ExpUtils.getLevel(PlayerSessionManager.getSession(p).getAccount().getExp()) + "";
		String ranks = PlayerSessionManager.getSession(p).getAccount().getRanksDisplay();
		return ChatColor.GRAY + level + " " + ranks;
	}

	public static String getBooleanInt(boolean bool) {
		if(bool == true) {
			return "1";
		} else {
			return "0";
		}
	}
	
	public static boolean getIntBoolean(String bool) {
		if(bool.equalsIgnoreCase("1")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static HashMap<String, String> getAliases() {
		HashMap<String, String> aliases = new HashMap<String, String>();
		
		aliases.put("<dot>", ".");
		aliases.put(",", ".");
		
		return aliases;
	}
	
	public static List<String> blockedKeys = Arrays.asList(
			".com",
			".net",
			".org",
			"play.",
			"hub.",
			"mc.",
			"http://"
		);
	
	public static boolean isCharacterOnly(String str) {
		return str.replace(" ", "").matches("[a-zA-Z]+");
	}
	
	public static void sendClickMessage(Player player, String message, String hoverMessage, String command) {
		IChatBaseComponent comp = ChatSerializer.a("{\"text\":\"" + "\",\"extra\":[{\"text\":\"" + message + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + hoverMessage + "\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + command + "\"}}]}");
		PacketPlayOutChat packet = new PacketPlayOutChat(comp);
		
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
	
	public static void sendTabTitle(Player player, String header, String footer) {
        if (header == null) header = "";
        header = ChatColor.translateAlternateColorCodes('&', header);

        if (footer == null) footer = "";
        footer = ChatColor.translateAlternateColorCodes('&', footer);

        header = header.replaceAll("<player>", player.getDisplayName());
        footer = footer.replaceAll("<player>", player.getDisplayName());

        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        IChatBaseComponent tabTitle = ChatSerializer.a("{\"text\": \"" + header + "\"}");
        IChatBaseComponent tabFoot = ChatSerializer.a("{\"text\": \"" + footer + "\"}");
        PacketPlayOutPlayerListHeaderFooter headerPacket = new PacketPlayOutPlayerListHeaderFooter(tabTitle);

        try {
            Field field = headerPacket.getClass().getDeclaredField("b");
            field.setAccessible(true);
            field.set(headerPacket, tabFoot);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.sendPacket(headerPacket);
        }
    }
	
	public static void sendTitle(Player player, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;

        PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        connection.sendPacket(packetPlayOutTimes);

        if (subtitle != null) {
            subtitle = subtitle.replaceAll("<player>", player.getDisplayName());
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            IChatBaseComponent titleSub = ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, titleSub);
            connection.sendPacket(packetPlayOutSubTitle);
        }

        if (title != null) {
            title = title.replaceAll("<player>", player.getDisplayName());
            title = ChatColor.translateAlternateColorCodes('&', title);
            IChatBaseComponent titleMain = ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleMain);
            connection.sendPacket(packetPlayOutTitle);
        }
    }
	
	public static void sendActionBar(Player player, String message) {
		CraftPlayer p = (CraftPlayer) player;
		IChatBaseComponent cbc = ChatSerializer.a("{\"text\": \"" + JSONObject.escape(message) + "\"}");
		PacketPlayOutChat ppoc = new PacketPlayOutChat(cbc, (byte) 2);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(ppoc);
	}
	
	public static ArrayList<String> stringToList(String str) {
		ArrayList<String> list = new ArrayList<String>();
		if (str != null) {
			for (String all : str.split(";/")) {
				if (!all.equals("")) {
					list.add(all);
				}
			}
		}
		return list;
	}
	
	public static String listToString(ArrayList<String> array) {
		String str = "";
		for (String all : array) {
			if (!all.equals("")) {
				if (str.equals("")) {
					str = all;
				} else {
					str = str + ";/" + all;
				}
			}
		}
		return str;
	}
	
	public static HashMap<String, String> stringToMap(String str) {
		HashMap<String, String> map = new HashMap<String, String>();
		
		if(str != null) {
			for(String all : str.split(";/")) {
				if(!all.equals("")) {
					map.put(all.split("/;")[0], all.split("/;")[1]);
				}
			}
		}
		
		return map;
	}
	
	public static String mapToString(HashMap<String, String> map) {
		String str = "";
		
		for(String all : map.keySet()) {
			if(!all.equals("")) {
				if(str.equals("")) {
					str = all + "/;" + map.get(all);
				} else {
					str = str + ";/" + all + "/;" + map.get(all);
				}
			}
		}
		
		return str;
	}
	
	public static String getTimeString(int minutes) {
		long hours = minutes / 60;   
		long minute = minutes % 60;
		
		return hours + "." + (minute / 60) + " hrs";
	}
	
	public static List<String> getLines(String url) {
		List<String> data = new ArrayList<String>();
		
		try {
			System.out.println("Downloading server file...");
			
			URL address = new URL(url);
			InputStreamReader pageInput = null;
			try {
				pageInput = new InputStreamReader(address.openStream());
			} catch (IOException e) { }
			
			BufferedReader source = null;
			
			if(pageInput != null) {
				source = new BufferedReader(pageInput);
			}
			String line;
			while((line = source.readLine()) != null && line.length() > 0) {
				data.add(line);
			}
			
			System.out.println("Downloaded server file.");
		} catch (Exception e) {
			System.out.println("Failed to download file.");
		}
		return data;
	}
	
	public static InputStream getStream(String url) {
		try {
			System.out.println("Downloading server file...");
			
			URL address = new URL(url);
			
			return address.openStream();
		} catch (Exception e) { }
		return null;
	}
}
