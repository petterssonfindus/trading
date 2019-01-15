package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.Util;

/**
 * Die Performance wird als p.a.-Kapitalentwicklung berechnet 
 * eine positive Dauer geht rückwärts und berechnet die Performance in der abgelaufenen Zeit
 * eine negative Dauer geht vorwärts für die kommende Zeit 
 * @author oskar
 *
 */
public class IndikatorPerformance extends IndikatorAlgorithmus {

	@Override
	public void rechne(Aktie aktie) {
		// TODO Auto-generated method stub

		float[] kurse = aktie.getKursArray();
		// holt die gewünschte Dauer
		int x = ((Integer) getParameter("dauer")).intValue();

		if (kurse.length <= x) Indikatoren.log.error(aktie.name + " zu wenig Kurse: " + kurse.length + " vorhanden: " + x + " benoetigt."); // wenn weniger Kurse vorhanden sind
		
		float kapitalBeginn = 0; 
		float kapitalEnde = 0; 
		float rendite; 
		// zurück rechnen
		if (x > 0) {
			// ignoriert die ersten x Kurse, läuft bis zum Schluss
			for (int i = x ; i < kurse.length; i++) {
				// Beginn wird von heute (i) zurück gesetzt um x
				kapitalBeginn = kurse[i - x];
				// Ende ist i
				kapitalEnde = kurse[i];
				// Rendite wird berechnet und eingetragen 
				rechneRendite(aktie, x, kapitalBeginn, kapitalEnde, i);
			}
		}
		else {
			x = Math.abs(x);
			for (int i = 1 ; i < kurse.length - x; i++) {
				// Beginn ist heute
				kapitalBeginn = kurse[i];
				// Ende ist in Zukunft
				kapitalEnde = kurse[i + x];
				rechneRendite(aktie, x, kapitalBeginn, kapitalEnde, i);
			}
		}
		
	}

	private void rechneRendite(Aktie aktie, int x, float kapitalBeginn,
			float kapitalEnde, int i) {
		float rendite;
		rendite = Util.rechnePerformancePA(kapitalBeginn, kapitalEnde, x);
		aktie.getBoersenkurse().get(i).addIndikator(this, rendite); 
		Indikatoren.log.trace("GD: " + x + " - " + rendite);
	}

	@Override
	public String getKurzname() {
		return "Performance"; 
	}

}
