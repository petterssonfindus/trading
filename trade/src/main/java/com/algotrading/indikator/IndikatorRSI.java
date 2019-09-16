package com.algotrading.indikator;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

public class IndikatorRSI extends IndikatorAlgorithmus {

	public IndikatorRSI() {
	};

	/**
	 * Relative-Stärke-Index
	 * Das Verhältnis der durschnittlich positiven Tage zu den durchschnittlich negativen Tagen 
	 * Wie stark sind die guten Tage im Vergleich zu den schlechten Tagen. 
	 * Ein Wert > 0,5 sagt, dass die positiven Tage stark sind, die negativen Tage schwach. 
	 * Ein Wert > 1 sagt, dass die positiven Tage doppelt so große Gewinne wie die negativen Verluste brachten 
	 * An jedem Tag kommt eine neue Differnz hinzu, eine alte fällt weg. 
	 * @param aktie
	 * @param "dauer"
	 */
	@Override
	public void rechne(Aktie aktie) {
		int tage = (Integer) getParameter("dauer");
		List<Kurs> kurse = aktie.getKursListe();
		float sumUp = 0;  // Summe der positiven Wertveränderung 
		float sumDown = 0; // Summe der negativen Wertveränderung 
		float sumUpA = 0; // Summe Up Average
		float sumDownA = 0; // Summe Down Average
		float rsi;
		float kurs = 0;
		float kursVortag = kurse.get(0).getKurs();
		float differenz = 0;
		float differenzAlt = 0;
		float[] differenzen = new float[kurse.size()];

		// die ersten Tage dienen zur Vorbereitung - keine Indikator-Berechnung 
		for (int i = 0; i < tage; i++) {
			kurs = kurse.get(i).getKurs();
			differenz = kurs - kursVortag;
			differenzen[i] = differenz;

			// Summen addieren
			if (differenz > 0)
				sumUp += differenz;
			else
				sumDown += differenz;

			kursVortag = kurs;
		}

		for (int i = tage; i < kurse.size(); i++) {
			Kurs kursO = kurse.get(i);
			kursVortag = kurs;
			kurs = kursO.getKurs();
			// Kursschwankung als Differenz zum Vortag
			differenz = kurs - kursVortag;
			// die neue Differenz in ein Array einstellen an der Stelle des Kurses. 
			differenzen[i] = differenz;
			// die alte Differenz aus dem Array auslesen 
			differenzAlt = differenzen[i - tage];

			// Summen anpassen: neue Differenz hinzuzählen
			if (differenz > 0)
				sumUp += differenz;
			else
				sumDown += differenz;
			// Alte Differenz abziehen
			if (differenzAlt > 0)
				sumUp -= differenzAlt;
			else
				sumDown -= differenzAlt;

			// Durchschnitte berechnen
			// alle +Differenzen / Tage 
			sumUpA = sumUp / tage;
			// alle -Differenzen / Tage 
			sumDownA = Math.abs(sumDown / tage);
			// RSI berechnen
			rsi = sumUpA / (sumUpA + sumDownA);
			// RSI an den Kurs anhängen
			kursO.addIndikator(this, rsi);

		}
	}

	@Override
	public String getKurzname() {
		return "RSI";
	}

}
