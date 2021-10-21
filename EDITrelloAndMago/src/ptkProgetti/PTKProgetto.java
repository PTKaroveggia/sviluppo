package ptkProgetti;

import org.json.JSONObject;

public class PTKProgetto {

	private String MagoNrOrdine = "";
	private Boolean MagoPagamentoOk= false;
	private JSONObject TrelloScheda = null;
	private String TrelloSchedaId = "";
	private String TrelloSchedaIdLista = "";
	
	
	
	public String getMagoNrOrdine() {
		return MagoNrOrdine;
	}
	public void setMagoNrOrdine(String magoNrOrdine) {
		MagoNrOrdine = magoNrOrdine;
	}
	public Boolean getMagoPagamentoOk() {
		return MagoPagamentoOk;
	}
	public void setMagoPagamentoOk(Boolean magoPagamentoOk) {
		MagoPagamentoOk = magoPagamentoOk;
	}
	public JSONObject getTrelloScheda() {
		return TrelloScheda;
	}
	public void setTrelloScheda(JSONObject trelloScheda) {
		TrelloScheda = trelloScheda;
	}
	public String getTrelloSchedaId() {
		return TrelloSchedaId;
	}
	public void setTrelloSchedaId(String trelloSchedaId) {
		TrelloSchedaId = trelloSchedaId;
	}
	public String getTrelloSchedaIdLista() {
		return TrelloSchedaIdLista;
	}
	public void setTrelloSchedaIdLista(String trelloSchedaIdLista) {
		TrelloSchedaIdLista = trelloSchedaIdLista;
	}
	
	
	
	
}
