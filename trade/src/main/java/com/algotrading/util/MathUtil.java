package com.algotrading.util;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

public class MathUtil {
	
	/**
	 * Transformiert einen bestehenden Indikator auf den Wert der Standardabweichung 
	 * Auf Wunsch wird die Abweichung des Kurses von der Standardabweichung berechnet
	 * Durchläuft alle Kurse einer Aktie und transformiert den gewünschten Indikator 
	 */
	public static void transformiereNachAbweichung (Aktie aktie, IndikatorAlgorithmus iA, int dauer, boolean relativeAbweichung) {
		Kurs kurs; 
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// beim Einfügen weiterer Werte fliegt automatisch der erst raus
		stats.setWindowSize(dauer);
		// die Werte auffüllen ohne Berechnung
		for (int i = 0 ; i < dauer ; i++) {
			kurs = aktie.getBoersenkurse().get(i);
			if (kurs.getIndikatorWert(iA) != null) {
				stats.addValue(kurs.getIndikatorWert(iA));
				// der Indikator wird entfernt, da er transformiert wird und noch kein sinnvolles Ergebnis hat. 
				kurs.removeIndikator(iA);
			}
		}
		for (int i = dauer ; i < aktie.getBoersenkurse().size() ; i++) {
			// den Kurs holen 
			kurs = aktie.getBoersenkurse().get(i);
			// den bisherigen Indikator-Wert der Stats-Liste hinzu fügen. 
			Float indikatorWert = kurs.getIndikatorWert(iA);
			if (indikatorWert == null) {
				continue; // es kann geschehen, dass der Indikator null ist. 
			}
			stats.addValue(indikatorWert);
			// Ermittlung der StandardAbweichung 
			double stabw = stats.getStandardDeviation();
			if (relativeAbweichung) {
				// die Abweichung vom Durchschnitt berechnen 
				double differenz = kurs.getKurs() - stats.getMean(); 
				// wie viele Standabweichungen ist der Kurs vom Durchschnitt entfernt
				double abweichung = differenz / stabw; 
				
				kurs.replaceIndikatorWert(iA, (float) abweichung);
			}
			else kurs.replaceIndikatorWert(iA, (float) stabw);
		}
		
	}
	
}
