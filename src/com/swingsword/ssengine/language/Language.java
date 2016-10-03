package com.swingsword.ssengine.language;

public class Language {
	
	private String id;
	//private ArrayList<String> connected = new ArrayList<>();
	
	public Language(String id) {
		this.id = id;
		
		LanguageManager.languages.add(this);
	}

	public String getId() {
		return id;
	}
}
