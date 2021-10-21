package com.trello.beans;

import java.util.HashMap;

public class Labels {

	private static HashMap<String, Label> labels = null;

	public HashMap<String, Label> getList() {

		if (labels == null) {
			labels = new HashMap<String, Label>();
		}

		return labels;
	}

}
