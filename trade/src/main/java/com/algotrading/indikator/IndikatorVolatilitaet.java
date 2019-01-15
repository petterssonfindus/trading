package com.algotrading.indikator;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.algotrading.aktie.*;
import com.algotrading.aktie.Kurs;
/**
 * Volatilität 
 * Parameter: dauer in Tagen 
 * @author oskar
 */
public class IndikatorVolatilitaet extends IndikatorAlgorithmus {

	/**
	 * Volatilität mit Hilfe der apache.math.statistic-Komponente
	 */
	@Override
	public void rechne (Aktie aktie) {
		int x = (Integer) getParameter("dauer");
		// wenn weniger Kurse vorhanden sind, als die Zeitraum 
		if (aktie.getBoersenkurse().size() <= x) return;
		
		Kurs kurs; 
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// beim Einfügen weiterer Werte fliegt automatisch der erst raus
		stats.setWindowSize(x);
		// die Werte auffüllen ohne Berechnung
		for (int i = 0 ; i < x ; i++) {
			stats.addValue(aktie.getBoersenkurse().get(i).getKurs());
		}
		for (int i = x ; i < aktie.getBoersenkurse().size() ; i++) {
			kurs = aktie.getBoersenkurse().get(i);
			stats.addValue(kurs.getKurs());
			double vola = stats.getStandardDeviation();
			kurs.addIndikator(this, (float) vola); 
		}
	}

	@Override
	public String getKurzname() {
		return "Vola";
	}

}
