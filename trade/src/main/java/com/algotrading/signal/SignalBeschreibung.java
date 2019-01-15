package com.algotrading.signal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.util.Parameter;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

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

	private List<Signal> signale = new ArrayList<Signal>();
	
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
	 * Signal-Erzeugung an der Signal-Beschreibung
	 * Hängt das neue Signal auch am Kurs an 
	 */
	public Signal createSignal (Kurs tageskurs, byte kaufVerkauf, float staerke) {
		Signal signal = new Signal(this, tageskurs, kaufVerkauf, staerke);
		this.signale.add(signal);  // das Signal hängt an der SignalBeschreibung 
		tageskurs.addSignal(signal); // das Signal hängt am Kurs 
		log.debug("neues Signal: " + signal.toString());
		return signal;
	}
	
	/**
	 * Der Zeitraum, vom Beginn des ersten Signals bis zum letzten Signal
	 */
	public Zeitraum getZeitraumSignale () {
		Signal signal1 = this.signale.get(0);
		Signal signaln = this.signale.get(this.signale.size() - 1);
		return new Zeitraum (signal1.getKurs().getDatum(), signaln.getKurs().getDatum());
	}
	
	/**
	 * Eine neue Bewertung wird durchgeführt 
	 */
	public SignalBewertung createBewertung () {
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
	
	public List<Signal> getSignale() {
		return signale;
	}
	
	public String toString () {
		return "S:" + Short.toString(this.signalTyp) + this.toStringParameter();
	}

}
