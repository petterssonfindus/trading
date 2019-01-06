package com.algotrading.aktie;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.signal.Signal;
import com.algotrading.signal.SignalBeschreibung;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Util;
/**
 * ein Kurs gilt an einem Tag. 
 * Er enthält Kursdaten, Indikatoren, und Signale
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
	// Liste der Indikatoren, die am Kurs hängen. 
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
			log.info("leeres Signal bei Kurs: " + this.toString());
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
	/**
	 * Zugriff auf das Signal einer bestimmten SignalBeschreibung
	 * @param signalBeschreibung
	 * @return ein Signal oder null, wenn nicht vorhanden
	 */
	public Signal getSignal (SignalBeschreibung signalBeschreibung) {
		Signal result = null; 
		for (Signal signal : this.signale) {
			if (signal.getSignalBeschreibung().equals(signalBeschreibung)) {
				result = signal; 
			}
		}
		return result; 
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
	 * null - wenn Indikator an diesem Kurs nicht vorhanden
	 * @param indikator die Indikator-Beschreibung 
	 * @return der float-Wert zu diesem Kurs
	 */
	public Float getIndikatorWert (IndikatorBeschreibung indikator) {
		if (this.indikatoren.containsKey(indikator)) {
			return this.indikatoren.get(indikator);
		}
		else {
//			log.error("Kurs enthält den gewünschten Indikator nicht");
			return null;
		}
	}
	
	/**
	 * gibt den Kurs eines Tages zurück - i.d.R. der Close-Kurs
	 * @return
	 */
	public float getKurs () {
		return close; 
	}
	/**
	 * Ermittelt den Kurs in die Zukunft oder Vergangenheit um n-Tage
	 * wenn nicht vorhanden, dann -null-
	 * @param vorzurueck Anzahl Tage: positiv = Zukunft - negativ = Vergangenheit
	 */
	public Kurs getKursTage (int n) {
		ArrayList<Kurs> kurse = this.getAktie().getBoersenkurse();
		// an welcher Stelle ist der aktuelle Kurs ?
		int x = kurse.indexOf(this);
		return kurse.get(x + n);
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
	 */
	public void setDatum (GregorianCalendar datum) {
		this.datum = datum; 
	}
	
	/**
	 * erzeugt einen String ohne Line-Separator
	 * Datum ; Close-Kurs ;  n-Indikatoren ; n-Signale
	 */
	public String toString() {
		return DateUtil.formatDate(datum, "-", true) + Util.separatorCSV + 
				Util.toString(close) + toStringIndikatoren() + toStringSignale();
	}
	/**
	 * Liefert einen String mit allen Indikatoren im Format Typ - Wert
	 */
	private String toStringIndikatoren() {
		String result = ""; 
		for (Float wert : this.indikatoren.values()) {
			result = result.concat(Util.separatorCSV + Util.toString(wert));
		}
		return result; 
	}
	/**
	 * Liefert einen String mit allen Signalen im Format 
	 * Typ - Kauf/Verkauf - Wert
	 */
	private String toStringSignale() {
		String result = ""; 
		for (Signal signal : this.signale) {
			result = result.concat(Util.separatorCSV + signal.toStringShort() );
		}
		return result; 
	}
	
	
}
