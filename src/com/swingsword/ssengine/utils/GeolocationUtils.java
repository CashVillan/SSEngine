package com.swingsword.ssengine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GeolocationUtils {

	public static HashMap<String, String> resolveIP(InetAddress realAddress) {
		HashMap<String, String> output = new HashMap<>();
		JsonObject wrapped = getJsonObjectFromIp(realAddress.getHostAddress());

		output.put("timezone", wrapped.get("timezone").getAsString());
		output.put("countryCode", wrapped.get("countryCode").getAsString());
		output.put("country", wrapped.get("country").getAsString());
		output.put("longitude", wrapped.get("lon").getAsString());

		return output;
	}
	
	public static JsonObject getJsonFromURL(String address) throws IOException {
		URL url = new URL(address);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		connection.setRequestMethod("GET");
		connection.connect();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		String value = "";
		String line;
		
		while ((line = in.readLine()) != null) {
			value = value + line;
		}
		in.close();
		JsonParser parser = new JsonParser();

		JsonObject json = (JsonObject) parser.parse(value);
		
		return json;
	}

	public static JsonObject getJsonObjectFromIp(String address) {
		try {
			JsonObject object = getJsonFromURL("http://ip-api.com/json/" + address);
			return object;
		} catch (IOException e) { }
		
		return null;
	}
	
	public static int getGMT(float longitude) {
		return (int) Math.round((longitude * 24 / 360) + (longitude > 0 ? -0.6 : 0.6));
	}
}
