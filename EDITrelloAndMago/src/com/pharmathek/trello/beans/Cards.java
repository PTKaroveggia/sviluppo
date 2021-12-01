package com.pharmathek.trello.beans;

import java.util.HashMap;

public class Cards {
	private static HashMap<String, Card> cards = null;

	public HashMap<String, Card> getCards() {

		if (cards == null) {
			cards = new HashMap<String, Card>();
		}

		return cards;
	}

}
