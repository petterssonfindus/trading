package com.algotrading.util;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

public class MathUtil {
	
	/**
	 * Transformiert einen bestehenden Indikator auf den Wert der Standardabweichung 
	 * @param stabw - int Anzahl Tage - Pflichtangabe, sonst geschieht nichts 
	 * @param relativ - Abweichung des Indikatorwertes von der Standardabweichung 
	 * @param faktor -> x/10+1 als Einflussfaktor umformen, der um Wert 1 oszilliert. 
	 * @param log - Logarithmieren 
	 * 
	 * Durchläuft alle Kurse einer Aktie und transformiert den gewünschten Indikator 
	 */
	public static void transformiereIndikator (Aktie aktie, IndikatorAlgorithmus iA) {
		Kurs kurs = null; 
		DescriptiveStatistics stats = new DescriptiveStatistics();
		boolean relativP = false;
		boolean faktorP = false;
		boolean logP = false;
		
		int stabwDauer = 0;
		Object stabwO = iA.getParameter("stabw");
		if (stabwO != null) {
			stabwDauer = (int) stabwO; 
		}
		else return; // wenn stabw nicht gesetzt ist, geschieht nichts. 

		Object relativO = iA.getParameter("relativ");
		if (relativO != null) {
			relativP = true; 
		}
		
		Object faktorO = iA.getParameter("faktor");
		if (faktorO != null) {
			faktorP = true; 
			relativP = true; 
		}
		
		Object logO = iA.getParameter("log");
		if (logO != null) {
			logP = true; 
			faktorP = true; 
			relativP = true; 
		}
		
		double result = 0;  // das Ergebnis der Berechnung, das letztlich la sIndikatorwert eingetragen wird. 
		// beim Einfügen weiterer Werte fliegt automatisch der erst raus
		stats.setWindowSize(stabwDauer);
		// die Werte auffüllen ohne Berechnung
		int anzahl = 0;
		int i = 0;  // Zähler durch die Kurse
		int anzahlKurse = aktie.getKursListe().size();
		
		// füllt den Kursspeicher, bis es voll ist
		while (anzahl < stabwDauer) {
			kurs = aktie.getKursListe().get(i ++);
			if (kurs.getIndikatorWert(iA) != null) {
				anzahl ++;
				stats.addValue(kurs.getIndikatorWert(iA));
				// der Indikator wird entfernt, da er transformiert wird und noch kein sinnvolles Ergebnis hat. 
				kurs.removeIndikator(iA);
			}
		}
		for ( ; i < anzahlKurse ; i++) {
			// den Kurs holen 
			kurs = aktie.getKursListe().get(i);
			// den bisherigen Indikator-Wert der Stats-Liste hinzu fügen. 
			Float indikatorWert = kurs.getIndikatorWert(iA);
			if (indikatorWert == null) {
				continue; // es kann geschehen, dass der Indikator null ist. 
			}
			stats.addValue(indikatorWert);
			// *** Schritt 1 *** Ermittlung der StandardAbweichung 
			result = stats.getStandardDeviation();
			
			if (relativP) {
				// die Abweichung vom Durchschnitt des Indikatorwertes berechnen 
				double differenz = indikatorWert - stats.getMean(); 
				// wie viele Standabweichungen ist der Indikatorwert vom Durchschnitt entfernt
				double abweichung = differenz / result; 
				// *** Schritt 2 *** Ermittlung der Differenz zur Standardabweichung 
				result = abweichung; 
				
				if (faktorP) {
					// *** Schritt 3 *** Ermittlung der Faktorisierung 
					double faktor = (abweichung/10) + 1;
					result = faktor; 
					
					if (logP) {
						double log = Math.log10(faktor);
						result = log; 
					}
				} // end Faktorisierung
			} // end Relativ 
			
			// falls eine Berechnung gemacht wurde, wird das Ergebnis im Kurs eingetragen. 
			kurs.replaceIndikatorWert(iA, (float) result);
		}  // end for
	}
	
}
