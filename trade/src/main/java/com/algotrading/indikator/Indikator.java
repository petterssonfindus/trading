package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;

/**
 * Alle Indikatoren müssen rechnen können 
 * @author oskar
 *
 */
public abstract class Indikator {
	
	/**
	 * iteriert über alle Kurse dieser Aktie und berechnet die Indikatorenwerte, die dann am Kurs hängen
	 * Die Parameter hängen an der Indikator-Beschreibung
	 * @param aktie
	 * @param indikatorBeschreibung
	 */
	public abstract void rechne (Aktie aktie, IndikatorBeschreibung indikatorBeschreibung);

}
