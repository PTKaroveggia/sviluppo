package com.pharmathek.trello.beans;

import java.util.HashMap;

public class Labels {

	private HashMap<String, Label> labels = null;

	public HashMap<String, Label> getList() {

		if (labels == null) {
			labels = new HashMap<String, Label>();
		}

		return labels;
	}

}
