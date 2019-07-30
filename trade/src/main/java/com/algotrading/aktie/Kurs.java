package com.algotrading.aktie;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.signal.Signal;
import com.algotrading.signal.SignalAlgorithmus;
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
	public String datumString; 
	public float close; 
	public float open;
	public float high;
	public float low;
	public float adjClose;
	public int volume; 
	public String wertpapier; 
	// Map der Indikatoren, die am Kurs hängen. 
	private HashMap<IndikatorAlgorithmus, Float> indikatoren = new HashMap<IndikatorAlgorithmus, Float>();
	// Liste der Indikatoren in Reihenfolge des Einfügens. 
	private List<IndikatorAlgorithmus> indikatorenListe = new ArrayList<IndikatorAlgorithmus>();

	// Liste aller Signale - öffentlicher Zugriff nur über add() und get()
	private List<Signal> signale; 

	public Kurs() {
		this.signale = new ArrayList<Signal>();
	}
	/**
	 * hängt an einen Kurs ein Signal an
	 * @param signal
	 */
	public void addSignal (Signal signal) {
		if (signal == null) {
			log.info("leeres Signal bei Kurs: " + this.toString());
		}
		else this.signale.add(signal);
	}
	/**
	 * Zugriff auf die Signale eines Kurses 
	 * @return eine Liste mit Signalen, oder null
	 */
	public List<Signal> getSignale () {
		return this.signale; 
	}
	
	public Signal getSignal (SignalAlgorithmus sA) {
		for (Signal signal : this.signale) {
			if (signal.getSignalAlgorithmus().equals(sA)) return signal;
		}
		return null; 
	}
	
	public void clearSignale () {
		this.signale = new ArrayList<Signal>();
	}
	
	public String getClose () {
		return Float.toString(close);
	}
	
	/**
	 * ein Indikator wurde berechnet und wird dem Kurs hinzugefügt
	 * Die Map dient als Assoziativer Speicher
	 * Die List als Reihenfolge
	 */
	public void addIndikator (IndikatorAlgorithmus indikator, float wert) {
		this.indikatoren.put(indikator, wert);
		this.indikatorenListe.add(indikator);
	}
	
	/**
	 * Ersetzt einen Indikator-Wert durch einen anderen
	 */
	public void replaceIndikatorWert (IndikatorAlgorithmus iA, float wert) {
		if (this.indikatoren.containsKey(iA)) {
			this.indikatoren.replace(iA, wert);
		}
	}
	/**
	 * Entfernt einen Indikator
	 * z.B. wenn der Indikator transformiert wird mit Standardabw. und die ersten Kurse leer bleiben. 
	 */
	public void removeIndikator (IndikatorAlgorithmus iA) {
		if (this.indikatoren.containsKey(iA))
			this.indikatoren.remove(iA);
	}
	
	/**
	 * ermittelt den Wert eines bestimmten Indikators dieses Kurses. 
	 * null - wenn Indikator an diesem Kurs nicht vorhanden
	 * @param indikator die Indikator-Beschreibung 
	 * @return der float-Wert zu diesem Kurs
	 */
	public Float getIndikatorWert (IndikatorAlgorithmus indikator) {
		if (this.indikatoren.containsKey(indikator)) {
			return this.indikatoren.get(indikator);
		}
		else {
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
		ArrayList<Kurs> kurse = this.getAktie().getKursListe();
		// an welcher Stelle ist der aktuelle Kurs ?
		int x = kurse.indexOf(this);
		try {
			return kurse.get(x + n);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public void setKurswert (float kurs) {
		this.close = kurs;
	}
	/**
	 * Bietet Zugriff auf die Aktie über eine Referenz (derzeit über den Namen) 
	 * @return die Aktie, zu dem der Kurs gehört 
	 */
	 public Aktie getAktie() {
		 return AktieVerzeichnis.getInstance().getAktieOhneKurse(this.wertpapier);
	 }
	
	/**
	 * das Datum mit einem GregCalendar setzen
	 */
	public void setDatum (GregorianCalendar datum) {
		this.datum = datum;
		this.datumString = DateUtil.formatDate(datum);
	}
	
	public GregorianCalendar getDatum() {
		return datum;
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
	 * In der Reihenfolge des Einfügens der Indikatoren 
	 */
	private String toStringIndikatoren() {
		String result = ""; 
		for (IndikatorAlgorithmus iA : this.indikatorenListe) {
			result = result.concat(Util.separatorCSV + Util.toString(this.getIndikatorWert(iA)));
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
	
	/**
	 * Signal-Erzeugung am Kurs 
	 * @param signalBeschreibung TODO
	 * @param kaufVerkauf TODO
	 * @param staerke TODO
	 */
	public Signal createSignal (SignalAlgorithmus sA, byte kaufVerkauf, float staerke) {
		Signal signal = new Signal(sA, this, kaufVerkauf, staerke);
		sA.addSignal(signal);  // das Signal hängt am Signal-Algorithmus
		addSignal(signal); // das Signal hängt am Kurs 
		log.debug("neues Signal: " + signal.toString());
		return signal;
	}
	
	
}
