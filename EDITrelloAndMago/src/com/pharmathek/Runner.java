package com.pharmathek;

import com.pharmathek.trello.Comunicator;
import com.pharmathek.trello.TrelloUtils;

public class Runner extends TrelloUtils {

	public static void main(String[] args) {
		new Comunicator().run();
	}

}
