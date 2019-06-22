package com.algotrading.aktie;

import java.util.ArrayList;

import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.Indikatoren;
import com.algotrading.signal.SignalAlgorithmus;
import com.algotrading.signal.Signale;

/**
 * Mehrere Aktien als Behälter
 * Eine Liste von Indikatoren für jede Aktie 
 *
 */
public class Aktien extends ArrayList<Aktie> {
	
	private static final long serialVersionUID = 1L;
	
	private Indikatoren indikatoren = new Indikatoren(); 
	
	private Signale signalAlgorithmen = new Signale ();
	
	private Aktien() {}

	public static Aktien create () {
		return new Aktien();
	}
	
	public Aktie addAktie (String name) {
		Aktie aktie = AktieVerzeichnis.getInstance().getAktie(name);
		this.add(aktie);
		return aktie; 
	}
	
	public IndikatorAlgorithmus addIndikator (IndikatorAlgorithmus iA) {
		this.indikatoren.add(iA);
		return iA;
	}
	
	public SignalAlgorithmus addSignalAlgorithmus (SignalAlgorithmus sA) {
		this.signalAlgorithmen.add(sA);
		return sA;
	}
	
	/**
	 * Erst werden alle Indikatoren und Signale an allen Aktien gesetzt 
	 * Dann werden alle Indikatoren und Signale an allen Aktien berechnet 
	 */
	public void rechneIndikatorenUndSignale () {
		addIndikatorenToAktien();
		addSignalAlgorithmenToAktien();
		for (Aktie aktie : this) {
			for (IndikatorAlgorithmus iA : this.indikatoren) {
				iA.rechne(aktie);
			}
		}
		for (Aktie aktie : this) {
			for (SignalAlgorithmus sA : this.signalAlgorithmen) {
				sA.rechne(aktie);
			}
		}
	}
	
	private void addIndikatorenToAktien () {
		for (IndikatorAlgorithmus iA : this.indikatoren) {
			for (Aktie aktie : this) {
				aktie.addIndikatorAlgorithmus(iA);
			}
		}
	}

	private void addSignalAlgorithmenToAktien () {
		for (SignalAlgorithmus sA : this.signalAlgorithmen) {
			for (Aktie aktie : this) {
				aktie.addSignalAlgorithmus(sA);
			}
		}
	}

	public Indikatoren getIndikatoren() {
		return indikatoren;
	}

	public Signale getSignale() {
		return signalAlgorithmen;
	}

}
