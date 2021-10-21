package com.trello.beans;

import org.json.JSONObject;

public class Label {
	private JSONObject jsonObj;

	private String idBoard = "";
	private String color = "";
	private String name = "";
	private String id = "";

	public Label(JSONObject innerObj) {
		if (innerObj != null) {
			jsonObj = innerObj;
			idBoard = jsonObj.getString("idBoard");
			color = jsonObj.getString("color");
			name = jsonObj.getString("name");
			id = jsonObj.getString("id");

		}

	}

	public String getIdBoard() {
		return idBoard;
	}

	public String getColor() {
		return color;
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

}
