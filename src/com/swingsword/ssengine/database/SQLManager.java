package com.swingsword.ssengine.database;

import java.util.Collection;
import java.util.HashMap;

public class SQLManager {
	
	static HashMap<String, SQL> databases = new HashMap<String, SQL>();
	
	public SQLManager() {
		new SQL("global");
		new SQL("games");
	}

	public static SQL getSQL(String name) {
		return databases.get(name);
	}
	
	public static Collection<SQL> getSQLs() {
		return databases.values();
	}
}
