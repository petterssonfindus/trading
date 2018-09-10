package com.algotrading.indikator;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.algotrading.aktie.*;
import com.algotrading.aktie.Kurs;

public class Volatilitaet {

	/**
	 * Volatilität mit Hilfe der apache.math.statistic-Komponente
	 * @param aktie
	 * @param x - die gewänscht Zeitraum
	 */
	public static void rechne (Aktie aktie, IndikatorBeschreibung indikator) {
		int x = (Integer) indikator.getParameter("dauer");
		// wenn weniger Kurse vorhanden sind, als die Zeitraum 
		if (aktie.getBoersenkurse().size() <= x) return;
		
		Kurs tageskurs; 
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// beim Einfägen weiterer Werte fliegt automatisch der erst raus
		stats.setWindowSize(x);
		// die Werte auffällen ohne Berechnung
		for (int i = 0 ; i < x ; i++) {
			stats.addValue(aktie.getBoersenkurse().get(i).getKurs());
		}
		for (int i = x ; i < aktie.getBoersenkurse().size() ; i++) {
			tageskurs = aktie.getBoersenkurse().get(i);
			stats.addValue(tageskurs.getKurs());
			double vola = stats.getStandardDeviation();
			tageskurs.addIndikator(indikator, (float) vola); 
		}
	}

}
