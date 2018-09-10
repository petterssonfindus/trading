package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;

public class GleitenderDurchschnitt {

	/**
	 * Summe aller Tageskurse der letzten x Tage / Anzahl 
	 * incluse aktueller Tageskurs 
	 * @param aktie
	 * @return Anzahl Tage, die erfolgreich berechnet wurden
	 */
	static int rechneGleitenderDurchschnitt (Aktie aktie, IndikatorBeschreibung indikator) {
		// holt die Kursreihe 
		float[] kurse = aktie.getKursArray();
		// holt den Parameter
		int x = ((Float) indikator.getParameter("dauer")).intValue();
		float summe = 0;
		int berechnet = 0;
		
		// addiert die Kurse der vergangenen x Tage. 
		// dabei wird nicht geschrieben, da die Berechnung noch unvollständig ist. 
		if (kurse.length <= x) Indikatoren.log.error(aktie.name + " zu wenig Kurse: " + kurse.length + " vorhanden: " + x + " benoetigt."); // wenn weniger Kurse vorhanden sind
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
			float ergebnis = summe / x;
			// das Ergebnis in den Kurs eintragen
			aktie.getBoersenkurse().get(i).addIndikator(indikator, ergebnis); 
			Indikatoren.log.trace("GD: " + x + " - " + ergebnis);
			berechnet ++;
		}
		return berechnet; 
	}

}
