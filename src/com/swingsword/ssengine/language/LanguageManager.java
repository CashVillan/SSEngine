package com.swingsword.ssengine.language;

import java.util.ArrayList;

public class LanguageManager {
	
	public static ArrayList<Language> languages = new ArrayList<Language>();
	
	public LanguageManager() {
		new Language("English");
		new Language("Dutch");
		new Language("Portuguese");
		new Language("Spanish");
	}
	
	public static Language getLanguage(String language) {
		for (Language lang : languages) {
			if (lang.getId().equalsIgnoreCase(language)) {
				return lang;
			}
		}
		return null;
	}
}
