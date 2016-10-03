package com.swingsword.ssengine.utils;

import org.bukkit.ChatColor;

public class IntegerUtils {

	public static boolean isInteger(Object s) {
		if(s instanceof Integer) {
			return true;
		}
		
        try { 
            Integer.parseInt((String) s); 
        } catch(NumberFormatException e) { 
            return false; 
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }
    
    public static int fitsTimes(int total, int amount) {
        int times = 0;
        
        while(total >= amount) {
        	times = times + 1;
        	total = total - amount;
        }
        
        return times;
    }
    
    public static byte chatColorToWool(ChatColor color) {
    	switch(color) {
    	case BLACK:
    		return 15;
    	case DARK_BLUE:
    		return 11;
    	case DARK_GREEN:
    		return 13;
    	case DARK_AQUA:
    		return 9;
    	case DARK_RED:
    		return 14;
    	case DARK_PURPLE:
    		return 10;
    	case GOLD:
    		return 1;
    	case GRAY:
    		return 8;
    	case DARK_GRAY:
    		return 7;
    	case GREEN:
    		return 5;
    	case AQUA:
    		return 3;
    	case LIGHT_PURPLE:
    		return 2;
    	case YELLOW:
    		return 4;
    	case WHITE:
    		return 0;
		default:
			return 0;
    	}
    }
    
    public static ChatColor wooltoChatColor(byte id) {
    	switch(id) {
    	case 15:
    		return ChatColor.BLACK;
    	case 11:
    		return ChatColor.DARK_BLUE;
    	case 13:
    		return ChatColor.DARK_GREEN;
    	case 9:
    		return ChatColor.DARK_AQUA;
    	case 14:
    		return ChatColor.DARK_RED;
    	case 10:
    		return ChatColor.DARK_PURPLE;
    	case 1:
    		return ChatColor.GOLD;
    	case 8:
    		return ChatColor.GRAY;
    	case 7:
    		return ChatColor.DARK_GRAY;
    	case 5:
    		return ChatColor.GREEN;
    	case 3:
    		return ChatColor.AQUA;
    	case 2:
    		return ChatColor.LIGHT_PURPLE;
    	case 4:
    		return ChatColor.YELLOW;
    	case 0:
    		return ChatColor.WHITE;
		default:
			return ChatColor.WHITE;
    	}
    }
    
	public static Integer convertTime(String input) {
		int time = Integer.parseInt(input.substring(0, input.length() - 1));
		String unit = input.substring(input.length() - 1, input.length());
		int multiplier = 0;
		if(unit.equalsIgnoreCase("s")) {
			multiplier = 1;
		}
		if(unit.equalsIgnoreCase("m")) {
			multiplier = 60;
		}
		if (unit.equalsIgnoreCase("h")) {
			multiplier = 3600;
		}
		if (unit.equalsIgnoreCase("d")) {
			multiplier = 86400;
		}
		if (unit.equalsIgnoreCase("w")) {
			multiplier = 604800;
		}
		time *= multiplier;
		return time;
	}
}
