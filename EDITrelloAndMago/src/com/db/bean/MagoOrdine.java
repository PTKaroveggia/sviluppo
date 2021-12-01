package com.db.bean;

import java.util.Date;

public class MagoOrdine {

	private int saleOrdId = 0; 
	private String nrOrdine = "";
	private Date data = null;
	private int priorita = 0;
	private boolean recordExist = false;
	
	

	public MagoOrdine() {
	}

	public String getNrOrdine() {
		return nrOrdine;
	}

	public void setNrOrdine(String nrOrdine) {
		this.nrOrdine = nrOrdine;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getPriorita() {
		return priorita;
	}

	public void setPriorita(int priorita) {
		this.priorita = priorita;
	}
	
	public void setRecordExist(boolean recordExist) {
		this.recordExist = recordExist;
	}
	
	public boolean isRecordExist() {
		return recordExist;
	}
	
	public void setSaleOrdId(int saleOrdId) {
		this.saleOrdId = saleOrdId;
	}
	
	public int getSaleOrdId() {
		return saleOrdId;
	}

}
