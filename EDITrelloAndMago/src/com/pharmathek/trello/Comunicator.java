package com.pharmathek.trello;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.pharmathek.db.JdbcMsSql;
import com.pharmathek.db.bean.MagoOrdine;
import com.pharmathek.sendemail.SendMail;
import com.pharmathek.trello.beans.Board;
import com.pharmathek.trello.beans.Card;
import com.pharmathek.trello.beans.Cards;
import com.pharmathek.trello.beans.Label;
import com.pharmathek.trello.beans.Labels;

public class Comunicator {

	static String newline = System.getProperty("line.separator");

	public void run() {

		/*
		 * Reperire l'id della bacheca perch� deve essere utilizzato per le request
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
		 * Inviare email solo su effettive variazioni (data vs sobo e label verso
		 * produzione) codice Descrizione 40 Ordine con acconto 41 Ordine NO acconto 50
		 * Ordine NO acconto - OK prod
		 *
		 * Eseguire questo applicativo con frequenza giornaliera alle ore 22:.00. NON
		 * DEVE MANDARE EMAIL SE VARIAZIONI NON PRESENTI
		 * 
		 * Identificare la label per il codice 50 e gestirla di conseguenza
		 * 
		 * SM 01/10/2021
		 * 
		 */

		Board board = getBoard("Slots Installazioni");
//		Board board = getBoard("Slots installazioni di prova");
		System.out.println(board.getName());
		System.out.println(board.getId());
		// elaboraListe(board);
		try {
			elaboraCards(board);
		} catch (Exception e) {
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

	// Lettura etichette presenti su tutta la bacheca

	private void leggiElencoEtichette(Board board, Labels anagraficoEtichette) {
		// Labels etichette = new Labels();

		HashMap<String, String> keys = new HashMap<>();
		keys.put("{id}", board.getId());
		JSONArray arrBoards = TrelloUtils.getArrayResponse(TrelloCurls.LABELS_OF_BOARD, keys);

		TrelloUtils.readLabels(anagraficoEtichette, arrBoards);

	}

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

//	private void elaboraListe(Board board) {
//
//		HashMap<String, List> liste = new HashMap<String, List>();
//
//		HashMap<String, String> keys = new HashMap<>();
//		keys.put("{id}", board.getId());
//		JSONArray arrBoards = TrelloUtils.getArrayResponse(TrelloCurls.BOARD_LISTS, keys);
//
//		for (int i = 0; i < arrBoards.length(); i++) {
//			JSONObject innerObj = arrBoards.getJSONObject(i);
//			List listTemp = new List(innerObj);
//			liste.put(listTemp.getName(), listTemp);
//			System.out.println(listTemp.getName());
//			System.out.println("--------------------");
//		}
//	}

	private void elaboraCards(Board board) throws Exception {

		// Leggo l'anagrafico etichette
		Labels anagraficoEtichette = new Labels();
		leggiElencoEtichette(board, anagraficoEtichette);

		Label accontoOK = new Label(null);
		Label accontoKO = new Label(null);
		Label accontoKO_OKDG = new Label(null);
		accontoOK = anagraficoEtichette.getList().get("ORDINE-CON ACCONTO");
		accontoKO = anagraficoEtichette.getList().get("ORDINE-NO ACCONTO");
		accontoKO_OKDG = anagraficoEtichette.getList().get("ORDINE NO ACCONTO - OK PROD");

		// TODO: SM 19.11.21 per AR: se le etichette non esistono devono essere create
		// nella Board
		if (accontoOK == null) {
			// Creo nuova atichettta in Trello
		}

		// Leggo le card contenute nella Board
		Cards progetti = new Cards();

		HashMap<String, String> keys = new HashMap<>();
		keys.put("{id}", board.getId());
		JSONArray arrCard = TrelloUtils.getArrayResponse(TrelloCurls.BOARD_CARDS, keys);

		for (int i = 0; i < arrCard.length(); i++) {
			Card cardTemp = new Card(arrCard.getJSONObject(i));
			progetti.getCards().put(cardTemp.getId(), cardTemp);
		}

		if (accontoOK == null)
			throw new Exception("object accontoOK is null");
		if (accontoKO == null)
			throw new Exception("object accontoKO is null");
		if (accontoKO_OKDG == null)
			throw new Exception("object accontoKO_OKDG is null");

		// Effettuo i controlli di merito richiesti
		controlliENotifiche(accontoOK, accontoKO, accontoKO_OKDG, progetti);

	}

	private void controlliENotifiche(Label accontoOK, Label accontoKO, Label accontoKO_OKDG, Cards progetti)
			throws SQLException {

		String emailDestProd, emailDestSOBO;

		emailDestProd = "produzione@pharmathek.com";
		emailDestSOBO = "sobo@pharmathek.com";

		if (TrelloUtils.debug) {
			emailDestProd = "s.mezzani@pharmathek.com";
			emailDestSOBO = "s.mezzani@pharmathek.com";

		}

		// Creo i SB per reportistica via email
		StringBuffer sbTrelloProgettiNonID = new StringBuffer();
		StringBuffer sbOrdineNonTrovato = new StringBuffer();
		StringBuffer sbMagoDataVariata = new StringBuffer();
		StringBuffer sbTrelloPrioritaVariata = new StringBuffer();

		// ciclo su tutte le card e ele controllo una alla volta
		for (Map.Entry<String, Card> entry : progetti.getCards().entrySet()) {

			VerificaProgetto(entry.getValue(), accontoOK, accontoKO, accontoKO_OKDG, sbTrelloProgettiNonID,
					sbOrdineNonTrovato, sbMagoDataVariata, sbTrelloPrioritaVariata);

		}

		// Preparazione invio email

		if (sbTrelloProgettiNonID.length() != 0) {
			new SendMail().sendEmail(emailDestProd, "CtrOrder TRELLO-MAGO Progetti Trello senza ID",
					sbTrelloProgettiNonID.toString());
		}

		if (sbOrdineNonTrovato.length() != 0) {
			new SendMail().sendEmail(emailDestProd, "CtrOrder TRELLO-MAGO Progetti Trello non trovati in MAGO",
					sbOrdineNonTrovato.toString());
		}

		if (sbMagoDataVariata.length() != 0) {
			new SendMail().sendEmail(emailDestSOBO, "CtrOrder TRELLO-MAGO Progetti Date di consegna variate",
					sbMagoDataVariata.toString());
		}

		if (sbTrelloPrioritaVariata.length() != 0) {
			new SendMail().sendEmail(emailDestProd, "CtrOrder TRELLO-MAGO Progetti Priorita' variate",
					sbTrelloPrioritaVariata.toString());
			// new SendMail().sendEmail(emailDestSOBO, "CtrOrder TRELLO-MAGO Progetti
			// Priorita' variate", sbTrelloPrioritaVariata.toString());
		}
	}

	// Verifico i dati del progetto, leggendo i dati dal db e confrontandoli con i
	// dati presenti su Trello
	private void VerificaProgetto(Card progetto, Label accontoOK, Label accontoKO, Label accontoKO_OKDG,
			StringBuffer sbTrelloProgettiNonID, StringBuffer sbOrdineNonTrovato, StringBuffer sbMagoDataVariata,
			StringBuffer sbTrelloPrioritaVariata) throws SQLException {

		/*
		 * Verifiche da effettuare: Data di scadenza di trello --> data consegna di
		 * mago. Se variata aggiornare la data presente su Mago e notificare via email
		 */

		// LEGGO I DATI DELL'ORDINE DAL DB DI MAGO
		MagoOrdine ordine = JdbcMsSql.readOrdine(progetto.getMagoNrOrdine());

		if (ordine.getNrOrdine().contains("*") || ordine.getNrOrdine().toUpperCase().contains("SLOT") ) {
				System.out.println("Caso ignorato: " + ordine.getNrOrdine());
		} else {
			

			// VERIFICO LE INFORMAZIONI CON I DATI PRESENTI SU TRELLO

			System.out.println(ordine.getNrOrdine());

			StringBuffer sbTemp = new StringBuffer();

			sbTemp.append(newline + "Ordine " + progetto.getMagoNrOrdine() + newline);
			sbTemp.append("Progetto Trello : " + progetto.getName() + newline);
			sbTemp.append("Ordine Mago     : " + progetto.getMagoNrOrdine() + newline);

			if (progetto.getMagoNrOrdine().equals("")) {

				if (sbTrelloProgettiNonID.length() == 0) {
					sbTrelloProgettiNonID.append(newline
							+ "Elenco progetti Trello non identificabili su MAGO.".toUpperCase() + newline + newline
							+ "Verificare il nome del progetto su TRELLO: il numero dell'ordine di MAGO deve essere tra parentesi quadre. ex: [nrOrdine]."
									.toUpperCase()
							+ newline + newline);
				}

				sbTrelloProgettiNonID.append("Progetto Trello : " + progetto.getName() + newline);

			} else if (!ordine.isRecordExist()) {

				if (sbOrdineNonTrovato.length() == 0) {
					sbOrdineNonTrovato.append(newline + "Elenco progetti Trello non trovati su MAGO.".toUpperCase()
							+ newline + newline
							+ "Verificare su Trello che il numero dell'ordine sia corretto; ".toUpperCase()
							+ "L'ordine presente tra parentesi quadre non e' stato trovato in MAGO.".toUpperCase()
							+ newline + newline);
				}

				sbOrdineNonTrovato.append("Progetto Trello : " + progetto.getName() + newline);
				sbOrdineNonTrovato.append("Ordine Mago     : " + progetto.getMagoNrOrdine() + newline + newline);

			} else {

				if (!dateUguali(progetto.getDue(), ordine.getData())) {
					System.out.println("Date diverse");
					System.out.println("Data Trello:" + progetto.getDue());
					System.out.println("Data Mago  :" + ordine.getData());

					if (sbMagoDataVariata.length() == 0) {
						sbMagoDataVariata.append(
								newline + "Elenco progetti dove la data di consegna risulta variata.".toUpperCase()
										+ newline + newline);
					}

					sbMagoDataVariata.append("Ordine MAGO    : " + ordine.getNrOrdine() + newline);
					sbMagoDataVariata.append("Progetto TRELLO: " + progetto.getName() + newline);
					sbMagoDataVariata.append("Data MAGO      : " + printDateToStr(ordine.getData()) + newline);
					sbMagoDataVariata
							.append("Data Trello    : " + printDateToStr(progetto.getDue()) + newline + newline);

					// TODO:x SM Modificare la data in MAGO
					JdbcMsSql.updateDataOrdine(ordine, progetto.getDue());

				} else {
					System.out.println("Date UGUALI");
				}

				controllaPriorita(progetto, 40, accontoOK, sbTrelloPrioritaVariata, ordine);
				controllaPriorita(progetto, 41, accontoKO, sbTrelloPrioritaVariata, ordine);
				controllaPriorita(progetto, 50, accontoKO_OKDG, sbTrelloPrioritaVariata, ordine);

			}
		}

	}

	/**
	 * Procedura per la verifica delle priorità impostate su Mago
	 * 
	 * @param progetto
	 * @param priorita
	 * @param etichetta
	 * @param sbTrelloPrioritaVariata
	 * @param ordine
	 */
	private void controllaPriorita(Card progetto, int priorita, Label etichetta, StringBuffer sbTrelloPrioritaVariata,
			MagoOrdine ordine) {


		if (ordine.getPriorita() == priorita) {
			if (progetto.getEtichette().getList().get(etichetta.getName()) == null) {
				controllaPrioritaInserisciIntestazione(sbTrelloPrioritaVariata);
				System.out.println("Etichetta " + etichetta.getName() + " aggiunta");
				sbTrelloPrioritaVariata.append("Progetto TRELLO : " + progetto.getName() + newline);
				sbTrelloPrioritaVariata.append("Ordine MAGO     : " + ordine.getNrOrdine() + newline);
				sbTrelloPrioritaVariata.append("Cod. priorita'  :" + priorita);
				sbTrelloPrioritaVariata.append("Etichetta       :" + etichetta.getName() + " aggiunta");
				TrelloUtils.addLabel(progetto.getId(), etichetta.getId());
			}
		} else {

			if (progetto.getEtichette().getList().get(etichetta.getName()) != null) {
				controllaPrioritaInserisciIntestazione(sbTrelloPrioritaVariata);
				System.out.println("Etichetta " + etichetta.getName() + " rimossa");
				sbTrelloPrioritaVariata.append("Progetto TRELLO : " + progetto.getName() + newline);
				sbTrelloPrioritaVariata.append("Ordine MAGO     : " + ordine.getNrOrdine() + newline);
				sbTrelloPrioritaVariata.append("Cod. priorita'  :" + priorita);
				sbTrelloPrioritaVariata.append("Etichetta       :" + etichetta.getName() + " rimossa");
				TrelloUtils.deleteLabel(progetto.getId(), etichetta.getId());
			}
		}
	}

	private void controllaPrioritaInserisciIntestazione(StringBuffer sbTrelloPrioritaVariata) {
		if (sbTrelloPrioritaVariata.length() == 0) {
			sbTrelloPrioritaVariata.append(newline
					+ "Elenco progetti Trello con priorita' variata su MAGO.".toUpperCase() + newline + newline);
		}
	}

	private boolean dateUguali(Date due, Date data) {
		// Verifico le date escludendo dal confronto l'orario
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
		if ((due == null) && (data == null)) {
			return true;
		} else if ((due == null) || (data == null)) {
			return false;
		} else {
			return df.format(due).equals(df.format(data));
		}
	}

	private String printDateToStr(Date data) {
		String dataStr = "";
		if (data != null) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			dataStr = df.format(data);
		} else {
			dataStr = "--/--/----";
		}
		return dataStr;
	}

}
