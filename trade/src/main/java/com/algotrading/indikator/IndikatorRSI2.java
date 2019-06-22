package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

public class IndikatorRSI2 extends IndikatorAlgorithmus {


	public IndikatorRSI2() {};
	
	/**
	 * Relative-Stärke-Index
	 * Die Implementierung geht an jedem Tag durch alle rückliegenden Tage 
	 * Das Verhältnis der durschnittlich positiven Tage zu den durchschnittlich negativen Tagen 
	 * Wie stark sind die guten Tage im Vergleich zu den schlechten Tagen. 
	 * Ein Wert > 0,5 sagt, dass die positiven Tage stark sind, die negativen Tage schwach. 
	 * Ein Wert > 1 sagt, dass die positiven Tage doppelt so große Gewinne wie die negativen Verluste brachten 
	 * An jedem Tag kommt eine neue Differnz hinzu, eine alte fällt weg. 
	 * @param aktie
	 * @param "dauer"
	 */
	@Override
	public void rechne (Aktie aktie) {
		int tage = (Integer) getParameter("dauer"); 
		ArrayList<Kurs> kurse = aktie.getKursListe();
		float sumUp = 0;  // Summe der positiven Wertveränderung 
		float sumDown = 0; // Summe der negativen Wertveränderung 
		float sumUpA = 0; // Summe Up Average
		float sumDownA = 0; // Summe Down Average
		float rsi; 
		float kurs = 0; 
		float kursVortag = kurse.get(0).getKurs(); // der 1. Kurs ist Kurs(0)
		float differenz = 0;
		// Differenzen werden in ein Array gestellt
		float[] differenzen = new float[kurse.size()];
		
		// für alle vorhandenen Kurse
		for (int i = 0 ; i < kurse.size() ; i++) {
			Kurs kursO = kurse.get(i);
			// als Vortageskurs wird der bisherige Kurs eingesetzt. 
			kursVortag = kurs; 
			// der neue Kurs wird ermittelt. 
			kurs = kursO.getKurs();
			// Kursschwankung als Differenz zum Vortag
			differenz = kurs - kursVortag;
			// die neue Differenz in ein Array einstellen an der Stelle des Kurses. 
			differenzen[i] = differenz; 
			// zur Vorbereitung auf-addieren 
			if (i <= tage) continue;

			// für jeden Kurs werden alle Differenzen rückwirkend durch-gerechnet
			for (int t = 0; t < tage ; t++) {
				// die Differenzen rückwirkend auf-addieren 
				differenz = differenzen [i - t];
				if (differenz > 0) sumUp += differenz;
				else sumDown += differenz;
			}
			// Durchschnitte berechnen
			// alle +Differenzen / Tage 
			sumUpA = sumUp / tage;
			// alle -Differenzen / Tage 
			sumDownA = Math.abs(sumDown / tage);
			// RSI berechnen
			rsi = sumUpA / (sumUpA + sumDownA);
			// RSI an den Kurs anhängen
			kursO.addIndikator(this, rsi);
			sumUp = 0;
			sumDown = 0;
		
		}
	}

	@Override
	public String getKurzname() {
		return "RSI2";
	}

}
