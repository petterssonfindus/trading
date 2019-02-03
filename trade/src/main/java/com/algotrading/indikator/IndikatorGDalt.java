package com.algotrading.indikator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;

public class IndikatorGDalt extends IndikatorAlgorithmus {
	private static final Logger log = LogManager.getLogger(IndikatorGDalt.class);

	/**
	 * Summe aller Tageskurse der letzten x Tage / Anzahl 
	 * incluse aktueller Kurs 
	 * Parameter: dauer - die Zeitdauer, die berücksichtigt wird
	 * 			  berechnungsart (optional) - 0 = der Durchschnittswert (default) 
	 * 								1 = die absolute Differenz zum aktuellen Kurs = Mittelwert-Abweichung
	 * 								2 = die relative Differenz zum aktuellen Kurs = Mittelwert-Abweichung
	 */
	public void rechne (Aktie aktie) {
		// holt die Kursreihe 
		float[] kurse = aktie.getKursArray();
		// holt die gewünschte Dauer
		Object dauerO = getParameter("dauer");
		if (dauerO == null) log.error("GleitenderDurchschnitt ohne Parameter dauer");
		int x = ((Integer) dauerO).intValue();
		// holt die Berechnungs-Art als Wert oder Differenz zum Kurs
		int berechnungsArt = 0; 
		Object o = getParameter("berechnungsart");
		if (o != null) berechnungsArt = ((Integer) o).intValue();
		
		float summe = 0;
		
		// addiert die Kurse der vergangenen x Tage. 
		// dabei wird nicht geschrieben, da die Berechnung noch unvollständig ist. 
		if (kurse.length <= x) log.error(aktie.name + " zu wenig Kurse: " + kurse.length + " vorhanden: " + x + " benoetigt."); // wenn weniger Kurse vorhanden sind
		// addiert die ersten x Kurse. 
		for (int i = 0 ; i < x ; i++) {
			summe += kurse[i];
		}
		// ein neuer Kurs kommt hinzu, ein alter Kurs fällt weg 
		for (int i = x ; i < kurse.length; i++) {
			float kursneu = kurse[i];
			float kursalt = kurse[i - x];
			summe += kursneu;
			summe -= kursalt; 
			// den Durchschnitt berechnen
			float ergebnis = summe / x;
			
			// wenn Berechnungs-Art = 1, die Differenz berechnen. 
			if (berechnungsArt == 1) {
				ergebnis -= kursneu;
			}
			// wenn Berechnungs-Art = 2, die relative Veränderung berechnen. 
			else if (berechnungsArt == 2) {
				ergebnis = (kursneu - ergebnis) / kursneu; 
			}
			// das GD-Ergebnis in den Kurs eintragen
			aktie.getBoersenkurse().get(i).addIndikator(this, ergebnis); 
			log.trace("GD: " + x + " - " + ergebnis);
		}

	}

	@Override
	public String getKurzname() {
		return "GD";
	}

}
