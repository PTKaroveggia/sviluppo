package com.trello;

import java.util.HashMap;

import org.json.JSONArray;

import com.trello.beans.Label;
import com.trello.beans.Labels;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public abstract class TrelloUtils {

	public static boolean debug = true;

	public static String key = "b5854db6c1a68e80521ece217ed665fe";
	public static String token = "125a0489a86f030d0f162f1bb5e75a92fd4d97068d060d7c718ec79410de58e7";

	public static HttpResponse<String> getResponse(String curl, HashMap<String, String> keys) {

//		String localCurl;
//
//		if (curl.indexOf("?") > 0) {
//			localCurl = curl + "&";
//		} else {
//			localCurl = curl + "?";
//		}
//
//		if (keys == null) {
//			keys = new HashMap<String, String>();
//		}
//
//		localCurl = localCurl + "key={apiKey}&token={apiToken}";
//		keys.put("{apiKey}", key);
//		keys.put("{apiToken}", token);
//
//		// Print keys and values
//		for (String localKey : keys.keySet()) {
//			localCurl = localCurl.replace(localKey, keys.get(localKey));
//		}
//
////		localCurl = localCurl.replace("{apiKey}", key);
////		localCurl = localCurl.replace("{apiToken}", token);		
//
//		if (debug == true) {
//			System.out.println(localCurl);
//		}

		String localCurl = urlPrepare(curl, keys);
		HttpResponse<String> localResponse;

		localResponse = Unirest.get(localCurl).asString();

		if (debug == true) {
			System.out.println(localResponse.toString());
		}

		return localResponse;
	}

	public static HttpResponse<String> post(String curl, HashMap<String, String> keys) {

		String localCurl = urlPrepare(curl, keys);

//		localResponse = Unirest.post(localCurl).asString();

		HttpResponse<String> localResponse = Unirest.post(localCurl).field("value", "614e1e55a4d6b515eaefaff0")
				.asString();

		if (debug == true) {
			System.out.println(localResponse.toString());
		}

		return localResponse;

	}

	public static HttpResponse<String> post(String curl, HashMap<String, String> keys, String value) {

		String localCurl = urlPrepare(curl, keys);

		HttpResponse<String> localResponse = Unirest.post(localCurl).field("value", value).asString();

		if (debug == true) {
			System.out.println(localResponse.toString());
		}

		return localResponse;

	}

	public static void addLabel(String cardId, String labelId) {
		HashMap<String, String> keys2 = new HashMap<>();
		keys2.put("{id}", cardId);
		HttpResponse response = TrelloUtils.post("https://api.trello.com/1/cards/{id}/idLabels", keys2, labelId);
		System.out.println(response.getBody());
	}

	public static void deleteLabel(String cardId, String labelId) {
		HashMap<String, String> keys3 = new HashMap<>();
		keys3.put("{id}", cardId);
		keys3.put("{idLabel}", labelId);
		HttpResponse response2 = TrelloUtils.delete("https://api.trello.com/1/cards/{id}/idLabels/{idLabel}", keys3);
		System.out.println(response2.getBody());
	}

	public static HttpResponse<String> delete(String curl, HashMap<String, String> keys) {

		String localCurl = urlPrepare(curl, keys);

		HttpResponse<String> localResponse = Unirest.delete(localCurl).asString();

		if (debug == true) {
			System.out.println(localResponse.toString());
		}

		return localResponse;

	}

	private static String urlPrepare(String curl, HashMap<String, String> keys) {
		String localCurl;

		if (curl.indexOf("?") > 0) {
			localCurl = curl + "&";
		} else {
			localCurl = curl + "?";
		}

		if (keys == null) {
			keys = new HashMap<String, String>();
		}

		localCurl = localCurl + "key={apiKey}&token={apiToken}";
		keys.put("{apiKey}", key);
		keys.put("{apiToken}", token);

		// Print keys and values
		for (String localKey : keys.keySet()) {
			localCurl = localCurl.replace(localKey, keys.get(localKey));
		}

		if (debug == true) {
			System.out.println(localCurl);
		}
		return localCurl;
	}

	public static JSONArray getArrayResponse(String curl, HashMap<String, String> keys) {

		return new JSONArray(getResponse(curl, keys).getBody());
	}

	public static void readLabels(Labels etichette, JSONArray JSONlabels) {

		for (int j = 0; j < JSONlabels.length(); j++) {

			Label labelTemp = new Label(JSONlabels.getJSONObject(j));

			etichette.getList().put(labelTemp.getName(), labelTemp);

		}

	}

}
