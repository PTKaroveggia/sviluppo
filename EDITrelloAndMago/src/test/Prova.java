package test;


import com.trello.Comunicator;
import com.trello.TrelloUtils;

public class Prova extends TrelloUtils {

	public static void main(String[] args) {
		new Comunicator().run();
		// new Comunicator().runDB();
	}

}
