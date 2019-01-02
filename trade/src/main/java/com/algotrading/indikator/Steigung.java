package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;

/**
 * Die Steigung entspricht der Kapitalentwicklung in % zum eigesetzten Kapital 
 * unabhängig von der Dauer
 * eine positive Dauer geht bei der Auswahl des eingesetzten Kapitals rückwärts und berechnet die Steigung in der abgelaufenen Zeit
 * eine negative Dauer geht vorwärts für die kommende Zeit 
 * @author oskar
 *
 */
public class Steigung implements IndikatorAlgorithmus {

	@Override
	public void rechne(Aktie aktie, IndikatorBeschreibung indikator) {
		// TODO Auto-generated method stub

		float[] kurse = aktie.getKursArray();
		// holt die gewünschte Dauer
		int dauer = ((Integer) indikator.getParameter("dauer")).intValue();

		if (kurse.length <= dauer) Indikatoren.log.error(aktie.name + " zu wenig Kurse: " + kurse.length + " vorhanden: " + dauer + " benoetigt."); // wenn weniger Kurse vorhanden sind
		
		float kapitalBeginn = 0; 
		float kapitalEnde = 0; 
		float steigung; 
		// zurück rechnen
		if (dauer > 0) {
			// ignoriert die ersten x Kurse, läuft bis zum Schluss
			for (int i = dauer ; i < kurse.length; i++) {
				// Beginn wird von heute (i) zurück gesetzt um x
				kapitalBeginn = kurse[i - dauer];
				// Ende ist i
				kapitalEnde = kurse[i];
				// Rendite wird berechnet und eingetragen 
				rechneSteigung(aktie, indikator, dauer, kapitalBeginn, kapitalEnde, i);
			}
		}
		else {
			dauer = Math.abs(dauer);
			for (int i = 1 ; i < kurse.length - dauer; i++) {
				// Beginn ist heute
				kapitalBeginn = kurse[i];
				// Ende ist in Zukunft
				kapitalEnde = kurse[i + dauer];
				rechneSteigung(aktie, indikator, dauer, kapitalBeginn, kapitalEnde, i);
			}
		}
		
	}

	private void rechneSteigung(Aktie aktie, IndikatorBeschreibung indikator, int dauer, float kapitalBeginn,
			float kapitalEnde, int i) {
		float steigung;
		steigung = (kapitalEnde / kapitalBeginn) - 1;
		aktie.getBoersenkurse().get(i).addIndikator(indikator, steigung); 
		Indikatoren.log.trace("Steigung: " + dauer + " - " + steigung);
	}

}