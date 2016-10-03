package com.swingsword.ssengine.game.games.rust.utils;

public class MathUtils {
	
	@SuppressWarnings("unused")
	private int round(double d) {
		double dAbs = Math.abs(d);
		int i = (int) dAbs;
		double result = dAbs - (double) i;
		if (result < 0.5) {
			return d < 0 ? -i : i;
		} else {
			return d < 0 ? -(i + 1) : i + 1;
		}
	}
	
	public static boolean isInteger(String s)
	{
		try
		{
			Integer.parseInt(s);
		} catch (Exception e)
		{
			return false;
		}
		return true;
	}

}
