package com.swingsword.ssengine.player;

import com.swingsword.ssengine.utils.StringUtils;

public class PlayerSettings {
	public Boolean[] settings = { true, true, true, false, false, false };
	
	public String toString() {
		return StringUtils.getBooleanInt(this.settings[0]) + StringUtils.getBooleanInt(this.settings[1]) + StringUtils.getBooleanInt(this.settings[2]) + StringUtils.getBooleanInt(this.settings[3]) + StringUtils.getBooleanInt(this.settings[4]) + StringUtils.getBooleanInt(this.settings[5]);
	}
	
	public static PlayerSettings fromString(String settingsString) {
		PlayerSettings settings = new PlayerSettings();
		
		if(settings != null) {
			if(settingsString != null) {
				char[] settingsList = settingsString.toCharArray();
				
				for(int x = 0; x < 5; x++) {
					settings.settings[x] = StringUtils.getIntBoolean(settingsList[0] + "");
				}
			}
		}
		
		return settings;
	}
}
