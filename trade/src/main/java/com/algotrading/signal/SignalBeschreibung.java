package com.algotrading.signal;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.Parameter;

/**
 * Beschreibt die Eigenschaften, die ein Signal erfüllen muss
 * Diese werden als Parameter generisch gehalten. 
 * Dient als Berechnungs-Vorschrift 
 * Bezieht sich auf eine Aktie - darf nur aus einer Aktie erzeugt werden. 
 * @author oskar
 *
 */
public class SignalBeschreibung extends Parameter {
	static final Logger log = LogManager.getLogger(SignalBeschreibung.class);

	private short signalTyp; 
	// die Aktie, an der die SignalBeschreibung hängt. 
	private Aktie aktie; 
	
	Aktie getAktie() {
		return aktie;
	}

	// eine Liste von Bewertungen mit zugehörigem Zeithorizont
	private HashMap<Integer, SignalBewertung> signalBewertung = new HashMap<Integer, SignalBewertung>();  

	public SignalBeschreibung(Aktie aktie, short signalTyp) {
		this.signalTyp = signalTyp; 
		this.aktie = aktie;
	}
	
	/**
	 * Eine neue Bewertung wird durchgeführt mit einem bestimmten Zeithorizont
	 */
	public SignalBewertung addBewertung (int tage) {
		SignalBewertung sBW = new SignalBewertung(tage, this);
		this.signalBewertung.put(tage, sBW);
		return sBW;
	}

	public short getSignalTyp() {
		return signalTyp;
	}
	
	public String toString () {
		return Short.toString(this.signalTyp);
	}
	
}
