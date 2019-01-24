package com.algotrading.signal;

import java.util.ArrayList;
import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.Parameter;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

/**
 * Ein Signal-Algorithmus muss diese Schnittstelle implementieren 
 * @author Oskar 
 *
 */
public abstract class SignalAlgorithmus extends Parameter {
	
	// eine Liste aller Signale, die von diesem Algorithmus erzeugt wurden
	private List<Signal> signale = new ArrayList<Signal>();
	
	// eine Liste aller Bewertungen mit unterschiedlichen Tagen, Zeiträumen ...
	private List<SignalBewertung> signalBewertung = new ArrayList<SignalBewertung>();

	/**
	 * Der Zeitraum, vom Beginn des ersten Signals bis zum letzten Signal
	 */
	public Zeitraum getZeitraumSignale () {
		Signal signal1 = this.signale.get(0);
		Signal signaln = this.signale.get(this.signale.size() - 1);
		return new Zeitraum (signal1.getKurs().getDatum(), signaln.getKurs().getDatum());
	}

	/**
	 * ermittelt Signale anhand einer Kursreihe
	 * @param aktie
	 * @return Anzahl erzeugter Signale
	 */
	public abstract int rechne(Aktie aktie);
	
	public abstract String getKurzname () ;
	
	/**
	 * Eine neue Bewertung wird durchgeführt 
	 */
	public SignalBewertung createBewertung () {
		SignalBewertung sBW = new SignalBewertung(this);
		this.signalBewertung.add(sBW);
		return sBW;
	}
	
	public void addSignal (Signal signal) {
		this.signale.add(signal);
	}
	
	public List<SignalBewertung> getBewertungen () {
		return this.signalBewertung;
	}
	
	public List<Signal> getSignale() {
		return this.signale;
	}
	
	/**
	 * enthält den Kurznamen und eine Liste der vorhandenen Parameter
	 */
	public String toString() {
		String result = ";I:" + getKurzname(); 
		for (String name : this.getAllParameter().keySet()) {
			result = result + (Util.separatorCSV + name + ":" + this.getParameter(name));
		}
		return result; 
	}

}
