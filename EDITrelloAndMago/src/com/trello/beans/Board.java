package com.trello.beans;

import org.json.JSONObject;

public class Board {

	private String name;
	private String id;
	private String url;
	private JSONObject jsonObj;

	public Board(JSONObject innerObj) {
		if (innerObj != null) {
			jsonObj = innerObj;
			name = jsonObj.getString("name");
			id = jsonObj.getString("id");
			url = jsonObj.getString("url");
		}
	}

	public Board() {
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public String getUrl() {
		return url;
	}

}
