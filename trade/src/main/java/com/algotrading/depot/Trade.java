package com.algotrading.depot;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.util.DateUtil;
import com.algotrading.util.Util;

	/**
	 * ein Trade besteht aus einem oder mehreren Käufen und Verkäufen 
	 * hat einen Beginn und ein Ende
	 * am Ende ist nichts mehr äbrig. 
	 * @author oskar
	 *
	 */
public class Trade {
	private static final Logger log = LogManager.getLogger(Trade.class);
	static final byte STATUS_EROEFFNET = 1; 
	static final byte STATUS_LAEUFT = 2; 
	static final byte STATUS_GESCHLOSSEN = 3; 
	String wertpapier; 
	ArrayList<Order> orders = new ArrayList<Order>();
	GregorianCalendar beginn;
	GregorianCalendar ende;
	int dauer = 0;
	// der jeweilige Bestand an Wertpapieren - am Anfang und Ende = 0
	float bestand = 0; 
	// Verkäufe abzgl. Käufe 
	float investiertesKapital = 0;
	// Gewinn/Verlust in Euro 
	float erfolg = 0;
	boolean erfolgreich = false; 
	byte status = Trade.STATUS_EROEFFNET; 

	/**
	 * der Trade darf nicht geschlossen sein
	 * der Bestand darf nicht negativ werden 
	 * Status wird angepasst 
	 * @param order
	 */
	byte addOrder (Order order) {
		if (order == null) log.error("Inputvariable Order ist null");
		// die Order erhält eine Referenz auf den zugehärigen Trade
		order.trade = this;
		if (this.status == Trade.STATUS_LAEUFT && order.wertpapier != this.wertpapier) log.error("Inputvariable Order abweichendes Wertpapier: " + order.wertpapier);
		if (this.status == Trade.STATUS_GESCHLOSSEN) log.error("Trade ist geschlossen");

		// die erste Order legt das Wertpapier fest und den Beginn 
		if (this.status == Trade.STATUS_EROEFFNET) {
			this.wertpapier = order.wertpapier;
			this.beginn = order.datum;
		}
		// Bestand wird angepasst
		if (order.kaufVerkauf == Order.KAUF) {
			this.bestand += order.stueckzahl;
			this.investiertesKapital += order.abrechnungsbetrag;
		}			
		else {	// Verkauf
			bestand -= order.stueckzahl;
			this.investiertesKapital -= order.abrechnungsbetrag;
		}
		// *******************
		// die Order eintragen 
		// *******************
		this.orders.add(order);
		// den Erfolg fortschreiben - Am Ende ist es der Gesamterfolg
		if (order.kaufVerkauf == Order.KAUF) this.erfolg = Util.rundeBetrag(this.erfolg - order.abrechnungsbetrag);
		else this.erfolg = Util.rundeBetrag(this.erfolg + order.abrechnungsbetrag);
		// Status anpassen 
		this.status = getStatus();
		// Dauer anpassen 
		this.ende = order.datum;
		this.dauer = DateUtil.anzahlKalenderTage(beginn, ende);
		
		// die letzte Order schlieät den Trade 
		if (this.status == Trade.STATUS_GESCHLOSSEN) {
			// die Dauer in Tagen 
			this.erfolgreich = this.erfolg > 0;
		}
		return this.status;
	}
	/**
	 * präft, ob der Trade im aktuellen Zustand offen oder geschlossen ist
	 * @return eräfnet, läuft, geschlossen
	 */
	public byte getStatus() {
		byte result; 
		if (orders.size() == 0) result = Trade.STATUS_EROEFFNET;
		else {	// mehrere Orders sind vorhanden 
			if (this.bestand < -0.01) log.error("Trade ist negativ Wertpapier :" + this.wertpapier);
			if (this.bestand > 0.01) {
				result = Trade.STATUS_LAEUFT;
			}
			else {
				result = Trade.STATUS_GESCHLOSSEN;  // Bestand ist 0
			}
		}
		return result; 
	}
	/**
	 * den aktuellen Status als lesbaren Text: 
	 * eräffnet - läuft - geschlossen
	 * @return
	 */
	public String getStatusAsString () {
		byte status = this.getStatus();
		String result = "";
		switch (status) {
			case 1: {
				result = "eroeffnet";
				break;
			}
			case 2: {
				result = "laeuft";
				break;
			}
			case 3: {
				result = "geschlossen";
				break;
			}
		}
		return result; 
	}
	
	public String toString() {
		String result = this.wertpapier + Util.separatorCSV + 
			this.getStatusAsString() + Util.separatorCSV + 
			this.orders.size() + Util.separatorCSV + 
			DateUtil.formatDate(beginn) + Util.separatorCSV + 
			DateUtil.formatDate(ende) + Util.separatorCSV + 
			Integer.toString(dauer) + Util.separatorCSV + 
			Util.toString(erfolg);
		return result; 
	}
}
