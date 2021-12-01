package com.pharmathek.utils;

import java.util.HashMap;

public class Dizionario {

	private static Boolean done = false;
	private static HashMap<Integer, String> mese; // = new HashMap<Integer, String>();
	private static HashMap<String, Integer> meseStr; // = new HashMap<Integer, String>();

	private static void preparaDizionari() {
		mese = new HashMap<Integer, String>();
		mese.put(1, "Gennaio");
		mese.put(2, "Febbraio");
		mese.put(3, "Marzo");
		mese.put(4, "Aprile");
		mese.put(5, "Maggio");
		mese.put(6, "Giugno");
		mese.put(7, "Luglio");
		mese.put(8, "Agosto");
		mese.put(9, "Settembre");
		mese.put(10, "Ottobre");
		mese.put(11, "Novembre");
		mese.put(12, "Dicembre");

		meseStr = new HashMap<String, Integer>();
		meseStr.put("Gennaio", 1);
		meseStr.put("Febbraio", 2);
		meseStr.put("Marzo", 3);
		meseStr.put("Aprile", 4);
		meseStr.put("Maggio", 5);
		meseStr.put("Giugno", 6);
		meseStr.put("Luglio", 7);
		meseStr.put("Agosto", 8);
		meseStr.put("Settembre", 9);
		meseStr.put("Ottobre", 10);
		meseStr.put("Novembre", 11);
		meseStr.put("Dicembre", 12);

		done = true;

	}

	public static HashMap<Integer, String> getMese() {
		if (done == false) {
			preparaDizionari();
		}
		return mese;
	}

	public static HashMap<String, Integer> getMeseStr() {
		if (done == false) {
			preparaDizionari();
		}
		return meseStr;
	}

}
