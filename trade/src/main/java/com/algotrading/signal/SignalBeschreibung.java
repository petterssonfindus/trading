package com.algotrading.signal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.Parameter;
import com.algotrading.util.Util;

/**
 * Beschreibt die Eigenschaften, die ein Signal erfüllen muss
 * Diese werden als Parameter generisch gehalten. 
 * Dient als Berechnungs-Vorschrift 
 * Bezieht sich auf eine Aktie - darf nur aus einer Aktie erzeugt werden. 
 * Enthält auch die Bewertung der Prognose-Qualität
 * @author oskar
 *
 */
public class SignalBeschreibung extends Parameter {
	static final Logger log = LogManager.getLogger(SignalBeschreibung.class);

	private short signalTyp; 
	// die Aktie, an der die SignalBeschreibung hängt. 
	private Aktie aktie; 

	// eine Liste aller Bewertungen mit unterschiedlichen Tagen, Zeiträumen ...
	private List<SignalBewertung> signalBewertung = new ArrayList<SignalBewertung>();  
	
	/**
	 * Das Erzeugen der Signalbeschreibung sollte nur über Aktie.createSignalbeschreibung() vorgenommen werden.
	 */
	public SignalBeschreibung(Aktie aktie, short signalTyp) {
		this.signalTyp = signalTyp; 
		this.aktie = aktie;
	}
	
	Aktie getAktie() {
		return aktie;
	}
	
	/**
	 * Eine neue Bewertung wird durchgeführt 
	 */
	public SignalBewertung addBewertung () {
		SignalBewertung sBW = new SignalBewertung(this);
		this.signalBewertung.add(sBW);
		return sBW;
	}
	
	public List<SignalBewertung> getBewertungen () {
		return this.signalBewertung;
	}

	public short getSignalTyp() {
		return signalTyp;
	}
	
	public String toString () {
		return "S:" + Short.toString(this.signalTyp) + this.toStringParameter();
	}
	
}
