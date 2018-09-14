package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;

/**
 * Alle Indikatoren müssen rechnen können 
 * @author oskar
 *
 */
abstract interface IndikatorAlgorithmus {
	
	/**
	 * iteriert über alle Kurse dieser Aktie und berechnet die Indikatorenwerte, die dann am Kurs hängen
	 * Die Parameter hängen an der Indikator-Beschreibung
	 * @param aktie
	 * @param indikatorBeschreibung
	 */
	abstract void rechne (Aktie aktie, IndikatorBeschreibung indikatorBeschreibung);

}
