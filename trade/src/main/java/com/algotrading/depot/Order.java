package com.algotrading.depot;

import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.util.Util;
import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
/**
 * repräsentiert einen Wertpapierauftrag mit allen Ausfährungsinformationen
 * zusätzlich wird auf Gesamt-Depotbestand aggregiert zum aktuellen Zeitpunkt 
 * @author oskar
 *
 */
public class Order {
	private static final Logger log = LogManager.getLogger(Order.class);

	protected Depot depot;			// die Order weiss, zu welchem Depot sie gehärt
	protected String wertpapier; 	// gleiche Bezeichnung wie die Kursreihe
	protected float stueckzahl; 	// Anzahl Stäcke - mit beliebig vielen Nachkommastellen 
	protected byte kaufVerkauf; 	// 1 = Kauf, 2 = Verkauf
	protected float kurs; 			// der Kurse, zu dem ausgefährt wurde
	protected float abrechnungsbetrag; 	// der Abrechnungsbetrag mit 2 Nachkommastellen 
	protected GregorianCalendar datum;	// der Zeitpunkt der Ausfährung
	protected String datumString;	// der Zeitpunkt der Ausfährung
	protected Trade trade;			// jede Order gehärt zu einem Trade
	protected float depotgeld; 		// Geldbestand nach Ausfährung der Order 

	public static final byte KAUF = 1;
	public static final byte VERKAUF = 2;
	
	/**
	 * keine Gelddisposition
	 * schreibt Orderbuch
	 * berechnet den Kurs mit dem Schlusskurs des selben Tages 
	 * aktualisiert Depotbestand
	 * @return die fertige Order
	 */
	public static Order orderAusfuehren (byte kaufVerkauf, String wertpapier, 
			float stueckzahl, Depot depot) {
		if (wertpapier == null) log.error("Inputvariable Wertpapier ist null");
		if (depot == null) log.error("Inputvariable Depot ist null");
		if (stueckzahl == 0) log.error("Inputvariable Stueckzahl ist 0");
		// neue Order erzeugen
		Order order = new Order();
		// Referenz auf das zugehärige Depot setzen
		order.depot = depot; 
		// zugehärige Kursreihe ermitteln 
		Aktie kursreihe = Aktien.getInstance().getAktie(wertpapier);
		// das Datum der Order stammt aus dem aktuellen Datum des Depot
		order.datum = depot.heute;
		order.datumString = Util.formatDate(order.datum);
		order.kaufVerkauf = kaufVerkauf;
		order.stueckzahl = stueckzahl;
		order.wertpapier = wertpapier;
		// den Ausfährungskurs ermitteln
		order.kurs = kursreihe.getAktuellerKurs().getKurs();
		// den Abrechnungsbetrag ermitteln
		order.abrechnungsbetrag = Util.rundeBetrag(stueckzahl * order.kurs);
		// das Geld buchen 
		if (kaufVerkauf == Order.KAUF) {
			// der Geldbestand im Depot reduziert sich 
			depot.geld -= order.abrechnungsbetrag;
		}
		else {		// ein Verkauf
			// der Geldbestand im Depot erhäht sich
			depot.geld += order.abrechnungsbetrag;
		}
		order.depotgeld = depot.geld;
		depot.orderEintragen(order);
		return order;
	}
	
	public String kaufVerkaufToString () {
		if (this.kaufVerkauf == Order.KAUF) return "Kauf"; 
		else return "Verkauf"; 
	}
	
	public String toString () {
		String datum = Util.formatDate(this.datum);
		return this.depot.name + Util.separator + 
				this.wertpapier + Util.separator + 
				this.kaufVerkaufToString() + Util.separator + 
				datum + Util.separator + 
				Util.toString(this.stueckzahl) + Util.separator + 
				Util.toString(this.kurs) + Util.separator + 
				Util.toString(this.abrechnungsbetrag) + Util.separator +
				Util.toString(depotgeld) + Util.separator +
				Util.toString(this.trade.dauer) + Util.separator + 
				Util.toString(trade.erfolg);
		
	}
	
}
