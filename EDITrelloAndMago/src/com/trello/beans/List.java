package com.trello.beans;

import org.json.JSONObject;

import com.utils.Dizionario;

public class List {

	private JSONObject jsonObj;

	private String idBoard = "";
	private int pos = 0;
	private String name = "";
	private Boolean closed = false;
	private String id = "";

	private int mese = 0;
	private int anno = 0;

	public List(JSONObject innerObj) {

		if (innerObj != null) {
			jsonObj = innerObj;

			idBoard = jsonObj.getString("idBoard");
			pos = jsonObj.getInt("pos");
			name = jsonObj.getString("name");
			closed = jsonObj.getBoolean("closed");
			id = jsonObj.getString("id");

			readName();
		}
	}

	private void readName() {

		// Prendo gli ultimi 4 caratteri e ne estraggo l'anno

		int len = name.length();
		if (len > 4) {
			String strAnno = name.substring(len - 4, len);
			String strMese = name.replace(strAnno, "").trim();

			System.out.println(strAnno);
			System.out.println(strMese);

			try {
				anno = Integer.parseInt(strAnno);
			} catch (NumberFormatException e1) {
			}

			try {
				mese = Dizionario.getMeseStr().get(strMese);
			} catch (Exception e) {
			}

		}

	}

	public JSONObject getJsonObj() {
		return jsonObj;
	}

	public String getIdBoard() {
		return idBoard;
	}

	public int getPos() {
		return pos;
	}

	public String getName() {
		return name;
	}

	public Boolean getClosed() {
		return closed;
	}

	public String getId() {
		return id;
	}

	public int getAnno() {
		return anno;
	}

	public int getMese() {
		return mese;
	}

}
