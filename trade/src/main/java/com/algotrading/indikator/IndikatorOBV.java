package com.algotrading.indikator;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

public class IndikatorOBV extends IndikatorAlgorithmus {

	private static final Logger log = LogManager.getLogger(IndikatorOBV.class);

	/**
	 * Rechnet On-Balance-Volume - Indikator
	 * Steigt der Kurs, wird das Volumen hinzugerechnet 
	 * Fällt der Kurs, wird das Volumen abgezogen. 
	 * @param aktie
	 * @param dauer
	 */
	@Override
	public void rechne (Aktie aktie) {
		// holt die Kurse, an denen die Umsätze dran hängen.
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		// holt den Parameter aus dem Indikator 
		int dauer = (Integer) getParameter("dauer");
		
		int summe = 0;
		int umsatzHeute = 0;
		Kurs kursVortag = null;
		Kurs kurs = null; 
		
		// addiert die Umsätze der vergangenen x(dauer) Tage. 
		if (kurse.size() <= dauer) log.error(aktie.name + " zu wenig Kurse: " + kurse.size() + " vorhanden: " + dauer + " benoetigt."); // wenn weniger Kurse vorhanden sind
		// k zählt von x(dauer) + 1 Tag , bis zum Ende 
		for (int k = dauer + 1; k < kurse.size() ; k++) {
			// für jeden Kurs x-Tage zurück 
			// i zählt von 0 bis 9
			for (int i = 0 ; i < dauer ; i++) {
				kursVortag = kurse.get(k - dauer + i - 1);
				kurs = kurse.get(k - dauer + i);
				
				if (kurs == null || kursVortag == null) log.error("Kurs nicht vorhanden im Indikator OnBalanceVolume");
				umsatzHeute = kurs.volume;
				if (kurs.getKurs() > kursVortag.getKurs()) { // der Kurs ist gestiegen
					// das Volumen wird hinzu addiert 
					summe += umsatzHeute ;
				}
				else { // der Kurs ist gefallen oder gleich geblieben #TODO gleiche Kurse geschieht nichts
					summe -= umsatzHeute ;
				}
			}
			// das Ergebnis in den Kurs eintragen. 
			kurs.addIndikator(this, summe); 
			summe = 0;
		}
		
	}

	@Override
	public String getKurzname() {
		return "OBV";
	}

}
