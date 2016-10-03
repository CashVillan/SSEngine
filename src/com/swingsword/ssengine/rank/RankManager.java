package com.swingsword.ssengine.rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.swingsword.ssengine.utils.ConfigUtils;
import com.swingsword.ssengine.utils.StringUtils;

public class RankManager {

	public static ArrayList<Rank> ranks = new ArrayList<Rank>();

	public RankManager() {
		for (String server : ConfigUtils.getConfig("cache").getStringList("ranks")) {
			List<String> configdata = StringUtils.stringToList(server);
			
			if(configdata != null && configdata.size() >= 4) {
				String name = configdata.get(0);
				String display = configdata.get(1);
				String altRank = configdata.get(2);
				String joinMsg = configdata.get(3);
				boolean staff = StringUtils.getIntBoolean(configdata.get(4));
				List<String> perms = Arrays.asList((configdata.get(5).split(";")));
				new Rank(name, display, altRank, joinMsg, staff, perms);
			}
		}
	}

	public static Rank getRank(String rank) {
		for (Rank all : ranks) {
			if (all.name.equals(rank)) {
				return all;
			}
		}
		return null;
	}
}