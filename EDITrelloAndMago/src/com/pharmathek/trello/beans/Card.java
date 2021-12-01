package com.pharmathek.trello.beans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pharmathek.trello.TrelloUtils;

public class Card {

	private JSONObject jsonObj;

	private String id = "";
	private String url = "";
	private String name = "";
	private String desc = "";
	private String dueStr = "";
	private Date due = null; 
	
	
	private Boolean dueComplete = false;
	private String idList = "";
	private JSONArray idMembers = null;
	private JSONArray idLabels = null;
	private JSONArray labels = null;
	private String magoNrOrdine= "";
	private Labels etichetteLocal = null;

	public Card(JSONObject innerObj) {
		labels = new JSONArray();
		idMembers = new JSONArray();
		idLabels = new JSONArray();
		etichetteLocal = new Labels(); 
		

		if (innerObj != null) {
			jsonObj = innerObj;

			name = jsonObj.getString("name");
			desc = jsonObj.getString("desc");
			idList = jsonObj.getString("idList");
			idMembers = jsonObj.getJSONArray("idMembers");
			idLabels = jsonObj.getJSONArray("idLabels");
			id = jsonObj.getString("id");
			url = jsonObj.getString("url");
			//idList = jsonObj.getString("idList");
			labels = jsonObj.getJSONArray("labels");
			dueComplete = jsonObj.getBoolean("dueComplete");
			//idList = jsonObj.getString("idList");
			idMembers = jsonObj.getJSONArray("idMembers");
			
			System.out.println(etichetteLocal.getList().toString());
			TrelloUtils.readLabels(etichetteLocal, labels);
			System.out.println(etichetteLocal.getList().toString());
 
			
			// Estraggo la data di scadenza 
			try {
				dueStr = jsonObj.getString("due");
				dueStr = dueStr.replaceAll("T", " ").substring(0,23); 
				try {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					due = sdf.parse(dueStr);
					
					// Aggiungo 2 ore perchè il dato arriva con fusio orario -2 
					Calendar c = Calendar.getInstance();
			        c.setTime(due);
			        c.add(Calendar.HOUR, 2);
			        due = c.getTime();
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} catch (Exception e1) {
			}

			// Leggo il numero dell'ordine 
			calcNumeroOrdine();
			
		}
	}

	

	public String getDesc() {
		return desc;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public JSONArray getLabels() {
		return labels;
	}

	public JSONObject getJsonObj() {
		return jsonObj;
	}

	public String getId() {
		return id;
	}

	public Boolean getDueComplete() {
		return dueComplete;
	}

	public String getIdList() {
		return idList;
	}

	public JSONArray getIdMembers() {
		return idMembers;
	}

	public JSONArray getIdLabels() {
		return idLabels;
	}
	
	
	public String getMagoNrOrdine() {
		return magoNrOrdine;
	}

	
	private void calcNumeroOrdine() {
		
		if ((name != null) & (!name.equals(""))) {
			
			int inizio = 0 ; 
			int fine = 0 ;
			
			inizio = name.indexOf("[");
			fine = name.indexOf("]");
			
			if ((inizio >= 0 ) & (fine >= 0)) {
				magoNrOrdine = name.substring(inizio+1, fine);
			}
			
			magoNrOrdine = magoNrOrdine.toUpperCase().replaceAll("ORDINE ", "");
			magoNrOrdine = magoNrOrdine.toUpperCase().replaceAll("ORD. ", "");

			magoNrOrdine = magoNrOrdine.toUpperCase().replaceAll("ORDINE", "");
			magoNrOrdine = magoNrOrdine.toUpperCase().replaceAll("ORD.", "");
			
			magoNrOrdine = magoNrOrdine.trim();
			
		}
		
	}
	
	
	public Date getDue() {
		return due;
	}

	public Labels getEtichette() {
		return etichetteLocal;
	}
	
	
	
}
