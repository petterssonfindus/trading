package com.algotrading.aktie;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.data.DBManager;
import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.signal.Signal;
import com.algotrading.util.Util;
/**
 * ein Tageskurs enthält Kursdaten, Indikatoren, und Signale
 * Dummer Datenbehälter
 * Kursdaten stammen aus der Datenbank, Indikatoren und Signale werden berechnet. 
 * @author oskar
 *
 */
public class Kurs {
	private static final Logger log = LogManager.getLogger(Kurs.class);
	
	public GregorianCalendar datum; 
	public float close; 
	public float open;
	public float high;
	public float low;
	public float adjClose;
	public int volume; 
	public String wertpapier; 
	/**
	 * Ein Indikator ist eine Referenz auf die Parameter des Indikators mit zugehörigem Float-Wert 
	 * Der Float ist der Wert für diesen Kurs 
	 */
	private HashMap<IndikatorBeschreibung, Float> indikatoren = new HashMap<IndikatorBeschreibung, Float>();

	// Liste aller Signale - öffentlicher Zugriff nur über add() und get()
	private ArrayList<Signal> signale; 

	public Kurs() {
		this.signale = new ArrayList<Signal>();
	}
	/**
	 * hängt an einen Kurs ein Signal an
	 * @param signal
	 */
	public void addSignal (Signal signal) {
		if (signal == null) log.error("Inputvariable signal ist null");
		if (signal == null) {
			log.info("leeres Signal bei Tageskurs: " + this.toString());
		}
		else this.signale.add(signal);
	}
	/**
	 * Zugriff auf die Signale eines Kurses 
	 * @return eine Liste mit Signalen, oder null
	 */
	public ArrayList<Signal> getSignale () {
		return this.signale; 
	}
	
	public void clearSignale () {
		this.signale = new ArrayList<Signal>();
	}
	
	public String getClose () {
		return Float.toString(close);
	}
	
	/**
	 * ein Indikator wurde berechnet und wird dem Kurs hinzugefügt
	 * @param indikator
	 * @param wert
	 */
	public void addIndikator (IndikatorBeschreibung indikator, float wert) {
		this.indikatoren.put(indikator, wert);
	}
	
	/**
	 * ermittelt den Wert eines bestimmten Indikators dieses Kurses. 
	 * @param indikator die Indikator-Beschreibung 
	 * @return der float-Wert zu diesem Kurs
	 */
	public float getIndikatorWert (IndikatorBeschreibung indikator) {
		if (this.indikatoren.containsKey(indikator)) {
			return this.indikatoren.get(indikator);
		}
		else {
			log.error("Kurs enthält den gewünschten Indikator nicht");
			return 0;
		}
	}
	
	/**
	 * gibt den Kurs eines Tages zurück - i.d.R. der Close-Kurs
	 * @return
	 */
	public float getKurs () {
		return close; 
	}
	
	public void setKurs (float kurs) {
		this.close = kurs;
	}
	/**
	 * Bietet Zugriff auf die Aktie über eine Referenz (derzeit über den Namen) 
	 * @return die Aktie, zu dem der Kurs gehört 
	 */
	 public Aktie getAktie() {
		 return Aktien.getInstance().getAktie(this.wertpapier);
	 }
	
	/**
	 * das Datum mit einem GregCalendar setzen
	 * @param datum
	 */
	public void setDatum (GregorianCalendar datum) {
		this.datum = datum; 
	}
	
	public String toString() {
		return DBManager.formatSQLDate(datum) + Util.separator + 
				Util.toString(close) + Util.separator + 
				indikatorenToString();
	}
	
	private String indikatorenToString() {
		String result = ""; 
		for (Float wert : this.indikatoren.values()) {
			result.concat(Util.toString(wert) + Util.separator);
		}
		return result; 
	}
	
}
