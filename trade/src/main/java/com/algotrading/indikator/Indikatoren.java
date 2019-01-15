package com.algotrading.indikator;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;

/**
 * berechnet alle statistischen Indikatoren auf Basis einer Kursreihe
 * und ergänzt die Kursreihe mit den Daten. 
 * @author oskar
 *
 */
public class Indikatoren {
	static final Logger log = LogManager.getLogger(Indikatoren.class);

	/**
	 * steuert die Berechnung der gewünschten Indikator-Algorithmen
	 * Die Indikator-Algorithmen hängen initial an der Aktie. 
	 * Jeder berechnete Wert wird mit einer Referenz auf den Algorithmus am Kurs gespeichert
	 * @param aktie
	 */
	public static void rechneIndikatoren(Aktie aktie) {
		if (aktie == null) log.error("Inputvariable aktie ist null");
		List<IndikatorAlgorithmus> iAs = aktie.getIndikatorAlgorithmen();
		if (iAs == null) log.error("Inputvariable Indikatoren ist null");
		
		for (IndikatorAlgorithmus iA : iAs) {
			// holt sich die Implementierung des Indikators 
			iA.rechne(aktie);
		}
	}
}
