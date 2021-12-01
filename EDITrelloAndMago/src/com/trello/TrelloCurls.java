package com.trello;

public abstract class TrelloCurls {

	private static String httpRoot = "https://api.trello.com/1/";

	public static String BOARDS = httpRoot + "members/me/boards?fields=name,url";
	public static String BOARD_CARDS = httpRoot + "boards/{id}/cards";
	public static String BOARD_LISTS = httpRoot + "boards/{id}/lists";
	public static String CARD_ADD_LABEL = httpRoot + "cards/{id}/idLabels";
	public static String LABELS_OF_BOARD = httpRoot + "boards/{id}/labels";

}
