package com.algotrading.aktie;

import java.util.ArrayList;

import com.algotrading.component.AktieVerwaltung;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.Indikatoren;
import com.algotrading.signal.SignalAlgorithmen;
import com.algotrading.signal.SignalAlgorithmus;

/**
 * Mehrere Aktien als Behälter
 * Eine Liste von Indikatoren für jede Aktie 
 *
 */
public class Aktien extends ArrayList<Aktie> {

	private static final long serialVersionUID = 1L;

	private AktieVerwaltung aV;

	private Indikatoren indikatoren = new Indikatoren();

	private SignalAlgorithmen signalAlgorithmen = new SignalAlgorithmen();

	public Aktien(AktieVerwaltung aV) {
		this.aV = aV;
	}

	public Aktie addAktie(String name) {
		Aktie aktie = aV.getVerzeichnis().getAktieOhneKurse(name);
		this.add(aktie);
		return aktie;
	}

	public void addIterable(Iterable<Aktie> it) {
		while (it.iterator().hasNext()) {
			this.add(it.iterator().next());
		}
	}

	public IndikatorAlgorithmus addIndikator(IndikatorAlgorithmus iA) {
		this.indikatoren.add(iA);
		return iA;
	}

	public SignalAlgorithmus addSignalAlgorithmus(SignalAlgorithmus sA) {
		this.signalAlgorithmen.add(sA);
		return sA;
	}

	/**
	 * Erst werden alle Indikatoren und Signale an allen Aktien gesetzt 
	 * Dann werden alle Indikatoren und Signale an allen Aktien berechnet 
	 */
	public void rechneIndikatorenUndSignale() {
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

	private void addIndikatorenToAktien() {
		for (IndikatorAlgorithmus iA : this.indikatoren) {
			for (Aktie aktie : this) {
				aktie.addIndikatorAlgorithmus(iA);
			}
		}
	}

	private void addSignalAlgorithmenToAktien() {
		for (SignalAlgorithmus sA : this.signalAlgorithmen) {
			for (Aktie aktie : this) {
				aktie.addSignalAlgorithmus(sA);
			}
		}
	}

	public Indikatoren getIndikatoren() {
		return indikatoren;
	}

	public SignalAlgorithmen getSignale() {
		return signalAlgorithmen;
	}

}
