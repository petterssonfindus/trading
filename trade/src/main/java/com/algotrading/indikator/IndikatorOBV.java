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
		int x = (Integer) getParameter("dauer");
		
		int summe = 0;
		int umsatzHeute = 0;
		int umsatzVortag = 0;
		Kurs kurs = null; 
		
		// addiert die Umsätze der vergangenen x Tage. 
		// dabei wird nicht geschrieben, da die Berechnung noch unvollständig ist. 
		if (kurse.size() <= x) log.error(aktie.name + " zu wenig Kurse: " + kurse.size() + " vorhanden: " + x + " benoetigt."); // wenn weniger Kurse vorhanden sind
		// k beginnt mit x, bis zum Ende 
		for (int k = x ; k < kurse.size() ; k++) {
			// fär jeden Kurs x-Tage zuräck 
			// der erste Kurs braucht einen Vortageskurs 
			umsatzVortag = kurse.get(0).volume;
			for (int i = 1 ; i < x ; i++) {
				kurs = kurse.get(k - x + i);
				if (kurs == null) log.error("Kurs nicht vorhanden im Indikator OnBalanceVolume");
				umsatzHeute = kurs.volume;
				if (umsatzHeute > umsatzVortag) { // der Kurs ist gestiegen
					// das Volumen wird hinzu addiert 
					summe += umsatzHeute ;
				}
				else { // der Kurs ist gefallen 
					summe -= umsatzHeute ;
				}
				umsatzVortag = umsatzHeute;
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
