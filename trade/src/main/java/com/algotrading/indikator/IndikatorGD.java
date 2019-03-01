package com.algotrading.indikator;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.util.MathUtil;

public class IndikatorGD extends IndikatorAlgorithmus {

	/**
	 * Gleitender Durchschnitt mit Hilfe der apache.math.statistic-Komponente
	 * @param dauer - Anzahl Tage die rückwirkend betrachtet werden
	 * @param mittelwert - 0 = geometrischer Mittelwert - gewichtet größere Zahlen geringer - default
	 * 					 - 1 = arithmetischer Mittelwert - 
	 * @param stabw, relativ, faktor, log aus MathUtil.Transformation
	 */
	@Override
	public void rechne (Aktie aktie) {
		Object dauerO = getParameter("dauer");
		int x = (Integer) dauerO;
		// wenn weniger Kurse vorhanden sind, als die Zeitraum 
		if (aktie.getBoersenkurse().size() <= x) return;
		
		Object mittelwertO = getParameter("mittelwert");
		int mittelwert = 0;	// Default-Wert = geometrisch
		if (mittelwertO != null) {
			mittelwert = 1;
		}
		
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
			// Ermittlung des Durchschnitts
			double gd = 0;
			if (mittelwert == 0) gd = stats.getGeometricMean();
			else gd = stats.getMean();
			kurs.addIndikator(this, (float) gd); 
		}
		MathUtil.transformiereIndikator(aktie, this);
	}

	@Override
	public String getKurzname() {
		return "GD";
	}

}
