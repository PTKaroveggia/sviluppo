package com.trello;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.db.JdbcMsSql;
import com.db.bean.MagoOrdine;
import com.sendemail.SendMail;
import com.trello.beans.Board;
import com.trello.beans.Card;
import com.trello.beans.Cards;
import com.trello.beans.Label;
import com.trello.beans.Labels;
import com.trello.beans.List;

public class Comunicator {

	public void run() {

		/*
		 * Reperire l'id della bacheca perchè deve essere utilizzato per le request
		 * successive
		 * 
		 * Ciclare su tutte le cards NON ARCHIVIATE della bacheca Ciclare su tutte le
		 * schede per singola bacheca e leggere le etichette
		 * 
		 * 
		 * capire dove mettere l'ID di Mago
		 * 
		 * leggere la data di scadenza
		 * 
		 * Confrontare i dati con MAGO e sincornizzare Mago + Trello
		 * 
		 * 
		 * Mago --> Label ORDINE CON ACCONTO (accesa/spenta in funzione di Mago) Trello
		 * --> Data scadenza aggiornata di volta in volta in Mago
		 * 
		 */
		
		
		/**
		 * 
		 * Inviare email solo su effettive variazioni (data vs sobo e label verso produzione) 
		 * 		codice	Descrizione
		 *		40	Ordine con acconto
		 * 		41	Ordine NO acconto
		 * 		50	Ordine NO acconto - OK prod
		 *
		 *  Eseguire questo applicativo con frequenza giornaliera alle ore 22:.00. 
		 *  NON DEVE MANDARE EMAIL SE VARIAZIONI NON PRESENTI
		 *  
		 *  Identificare la label per il codice 50 e gestirla di conseguenza
		 * 
		 * SM 01/10/2021
		 * 
		 */
		
		

//		Board board = getBoard("Slots Installazioni");
		Board board = getBoard("Slots installazioni di prova");
		System.out.println(board.getName());
		System.out.println(board.getId());
		// elaboraListe(board);
		try {
			elaboraCards(board);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

//	public void runDB() {
//
//		// JdbcMsSql.getDbConnection();
//
//		try {
//			ResultSet rs = JdbcMsSql.query();
//
//			while (rs.next()) {
//				String ordine = rs.getString("Ordine");
//				System.out.println(">> " + ordine);
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//	}

	/**
	 * Dato il nome di una Bacheca, ne estraggo il bean da trello
	 * 
	 * @param boardName
	 * @return
	 */
	private Board getBoard(String boardName) {

		JSONArray arr = TrelloUtils.getArrayResponse(TrelloCurls.BOARDS, null);

//		if (TrelloUtils.debug == true)
//			System.out.println(response.getBody());
//
//		JSONArray arr = new JSONArray(response.getBody());

		Board board = null;

		for (int i = 0; i < arr.length(); i++) {

			JSONObject innerObj = arr.getJSONObject(i);

			if ((new Board(innerObj)).getName().equals(boardName)) {
				board = new Board(innerObj);
			}

		}
		return board;
	}

	private void elaboraListe(Board board) {

		HashMap<String, List> liste = new HashMap<String, List>();

		HashMap<String, String> keys = new HashMap<>();
		keys.put("{id}", board.getId());
		JSONArray arrBoards = TrelloUtils.getArrayResponse(TrelloCurls.BOARD_LISTS, keys);

		for (int i = 0; i < arrBoards.length(); i++) {
			JSONObject innerObj = arrBoards.getJSONObject(i);
			List listTemp = new List(innerObj);
			liste.put(listTemp.getName(), listTemp);
			System.out.println(listTemp.getName());
			System.out.println("--------------------");
		}
	}

	private void elaboraCards(Board board) throws SQLException {

		Cards progetti = new Cards();
		Labels etichette = new Labels();

		HashMap<String, String> keys = new HashMap<>();
		keys.put("{id}", board.getId());
		JSONArray arrCard = TrelloUtils.getArrayResponse(TrelloCurls.BOARD_CARDS, keys);

		for (int i = 0; i < arrCard.length(); i++) {
			JSONObject innerObj = arrCard.getJSONObject(i);

			Card cardTemp = new Card(innerObj);
			cardTemp.readLabels(etichette);
			progetti.getCards().put(cardTemp.getId(), cardTemp);

			//System.out.println(etichette.toString());

			System.out.println("*************************************");
			System.out.println(innerObj.toString());
//			System.out.println("***");
			System.out.println(cardTemp.getIdList());
			System.out.println(cardTemp.getId());
			System.out.println(cardTemp.getName());
			System.out.println(cardTemp.getMagoNrOrdine());
//			System.out.println(cardTemp.getDue().toString());
			System.out.println("----------------------------------------------");
//			System.out.println("***");
//			System.out.println(cardTemp.getUrl());
//			System.out.println("***");
//			System.out.println(cardTemp.getLabels().toString());

//			for (int j = 0; j < cardTemp.getLabels().length(); j++) {
//
//				Label labelTemp = new Label(cardTemp.getLabels().getJSONObject(j));
//
//				System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
//				System.out.println(labelTemp.getName());
//				System.out.println(labelTemp.getColor());
//				System.out.println(labelTemp.getId());
//				System.out.println(labelTemp.getIdBoard());
//
//			}

			System.out.println("--------------------");
		}

		// Stampo l'elenco delle etichette che ho nella bacheca
//		for (Map.Entry<String, Label> entry : etichette.getList().entrySet()) {
//			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
//		}

		// TODO: se non trovo le etichette dovrò crearne di nuove
		// Cerco le due etichette necessarie alla gestione dell'acconto
		Label accontoOK = new Label(null);
		Label accontoKO = new Label(null);
		accontoOK = etichette.getList().get("ORDINE-CON ACCONTO");
		accontoKO = etichette.getList().get("ORDINE-NO ACCONTO");
		StringBuffer sb = new StringBuffer();
		
		// TODO per Alberto: isolare i string buffer per contestualizzare i testi delle email:
		// sobo@pharmathek.com mail con elenco progetti data variata, e nr totale progetti analizzati. Modificare l'oggeto della mail se presenti o meno progetti con data variata
		// produzione@pharmathek.com 
		
		
		// ciclo su tutte le card e ele controllo una alla volta
		for (Map.Entry<String, Card> entry : progetti.getCards().entrySet()) {
			System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			VerificaProgetto(entry.getValue(), accontoOK, accontoKO, sb);
		}
		
		if (sb.length() == 0) {
			sb.append("NON SONO STATE RILEVATE VARIAZIONI"); 
		} 
		
		
		new SendMail().sendEmail("s.mezzani@pharmathek.com", "Confronto ordini TRELLO - MAGO", sb.toString()) ;

		
		/*
		 * 
		 * // Aggioungi label TrelloUtils.addLabel("61498d910000c8335d9ceb8b",
		 * "614e1e55a4d6b515eaefaff0"); // Rimuovi label
		 * TrelloUtils.deleteLabel("61498d910000c8335d9ceb8b",
		 * "614e1e55a4d6b515eaefaff0");
		 * 
		 */

//		HashMap<String, String> keys2 = new HashMap<>();
//		keys2.put("{idBoard}", "61498a38a80135736c4522cf");
//		keys2.put("{name}", "etichetta");
//		keys2.put("{color}", "lime");
//		HttpResponse response = TrelloUtils.post("https://api.trello.com/1/labels?name={name}&color={color}&idBoard={idBoard}", keys2);
//		System.out.println(response.getBody());
// 		{"id":"614e1e55a4d6b515eaefaff0","idBoard":"61498a38a80135736c4522cf","name":"etichetta","color":"lime","limits":{}}

//		HashMap<String, String> keys3 = new HashMap<>();
//		keys3.put("{id}", "61498d910000c8335d9ceb8b");
//		keys3.put("{idLabel}", "614e1e55a4d6b515eaefaff0");
//		HttpResponse response2 = TrelloUtils.delete("https://api.trello.com/1/cards/{id}/idLabels/{idLabel}", keys3);
//		System.out.println(response2.getBody());

//			HashMap<String, String> keys2 = new HashMap<>();
//			keys2.put("{id}", "61498d910000c8335d9ceb8b");
//			keys2.put("{name}", "PROVA2");
//			keys2.put("{idList}", "61498d85a9e06c39b7750807");

//		HttpResponse response = TrelloUtils.post("https://api.trello.com/1/cards?idList={idList}", keys2);
//		System.out.println(response.getBody());

//		Creare una Board		
//		HttpResponse response = TrelloUtils.post("https://api.trello.com/1/boards/?name={name}", keys2);

		// Aggiungi label

//		HashMap<String, String> keys2 = new HashMap<>();
//		keys2.put("{id}", "61498d910000c8335d9ceb8b");
//		keys2.put("{idLabels}", "614d8b0a29d2e33be255585a");
//		HttpResponse response = TrelloUtils.post(TrelloCurls.CARD_ADD_LABEL, keys2);
//		System.out.println(response.getBody());
	}

	
	// TODO: SM 27/09/21 Manca interpretazione del campo priorità e relativa scittura di labels e data  
	
	// Verifico i dati del progetto, leggendo i dati dal db e confrontandoli con i
	// dati presenti su Trello
	private void VerificaProgetto(Card progetto, Label accontoOK, Label accontoKO, StringBuffer sb) throws SQLException {
		String newline = System.getProperty("line.separator");
		
		/*
		 * Verifiche da effettuare: Data di scadenza di trello --> data consegna di
		 * mago. Se variata aggiornare la data presente su Mago e notificare via email
		 */

		// LEGGO I DATI DELL'ORDINE DAL DB DI MAGO
		MagoOrdine ordine = JdbcMsSql.readOrdine(progetto.getMagoNrOrdine());

		// VERIFICO LE INFORMAZIONI CON I DATI PRESENTI SU TRELLO

		System.out.println(ordine.getNrOrdine());

		sb.append(newline + "Ordine "+ progetto.getMagoNrOrdine() +newline); 
		sb.append("progetto su Trello : "+ progetto.getName() +newline); 
		sb.append("ordine su Mago     : "+ progetto.getMagoNrOrdine() +newline); 
		Boolean lineAded = false; 

		
		
		if (progetto.getMagoNrOrdine().equals("")) {
			sb.append(">> Ordine MAGO non identificato. Verificare il nome del progetto su TRELLO (il numero ordine deve essere tra parentesi quadre. ex: [nrOrdine])." + newline);
			lineAded = true; 
		} else if (!ordine.isRecordExist()) {
			sb.append(">> Ordine non trovato su MAGO. Verificare su Trello il che il numero dell'ordine sia corretto)." + newline);
			lineAded = true; 
		} else {
			
			if (!dateUguali(progetto.getDue(), ordine.getData())) {
				System.out.println("Date diverse");
				System.out.println("Data Trello:" + progetto.getDue());
				System.out.println("Data Mago  :" + ordine.getData());
	
				sb.append(">> La data di consegna risulta variata:  Data in Trello: "+ printDateToStr(progetto.getDue()) + " Data in Mago: " +  printDateToStr(ordine.getData())+newline); 
				lineAded = true; 
				
			} else {
				System.out.println("Date UGUALI");
			}
			
		}
		
		if (!lineAded) {
			sb.append("-"+newline); 
		}
		
		

		System.out.println(ordine.getPriorita());
		System.out.println(ordine.getData());
		System.out.println("Fine lettura dati prgetto");
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

	}

	private boolean dateUguali(Date due, Date data) {
		// Verifico le date escludendo dal confronto l'orario
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		if ((due == null) || (data == null)) {
			return false;
		} else {
			return df.format(due).equals(df.format(data));
		}
	}
	
	private String printDateToStr(Date data) {
		String dataStr = ""; 
		if(data != null) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			dataStr = df.format(data);
		} else {
			dataStr = "--/--/----";
		}
		return dataStr; 
	}

}
