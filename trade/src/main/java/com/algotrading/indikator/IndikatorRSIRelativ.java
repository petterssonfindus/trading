package com.algotrading.indikator;

import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

@Entity(name = "RSIRelativ")
@DiscriminatorValue("RSIRelativ")
public class IndikatorRSIRelativ extends IndikatorAlgorithmus {

	/**
	 * Relative-Stärke-Index - Berechnung erfolgt relativ zu den positiven -
	 * negativen Tagen. Wie groß sind die positiven Tage im Verhältnis zu den
	 * negativen Tagen. Summe Wertdifferenz positive Tage / Anzahl positive Tage Die
	 * Implementierung geht an jedem Tag durch alle rückliegenden Tage
	 * 
	 * @param "dauer"
	 */
	@Override
	public void rechne(Aktie aktie) {
		int tage = (Integer) getParameter("dauer");
		ArrayList<Kurs> kurse = aktie.getKursListe();
		float sumUp = 0; // Summe der positiven Wertveränderung
		float sumDown = 0; // Summe der negativen Wertveränderung
		float sumUpA = 0; // Summe Up Average
		float sumDownA = 0; // Summe Down Average
		int tagePlus = 0;
		int tageMinus = 0;
		float rsi;
		float kurs = 0;
		float kursVortag = kurse.get(0).getKurs(); // der 1. Kurs ist Kurs(0)
		float differenz = 0;
		// Differenzen werden in ein Array gestellt
		float[] differenzen = new float[kurse.size()];

		// für alle vorhandenen Kurse
		for (int i = 0; i < kurse.size(); i++) {
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
			if (i <= tage)
				continue;

			// für jeden Kurs werden alle Differenzen rückwirkend durch-gerechnet
			for (int t = 0; t < tage; t++) {
				// die Differenzen rückwirkend auf-addieren
				differenz = differenzen[i - t];

				if (differenz > 0) {
					sumUp += differenz;
					tagePlus++;
				}

				else {
					sumDown += differenz;
					tageMinus++;
				}
			}
			// Durchschnitte berechnen
			// alle +Differenzen / Tage
			sumUpA = sumUp / tagePlus;
			// alle -Differenzen / Tage
			sumDownA = Math.abs(sumDown / tageMinus);
			// RSI berechnen
			rsi = sumUpA / (sumUpA + sumDownA);
			// RSI an den Kurs anhängen
			kursO.addIndikator(this, rsi);
			sumUp = 0;
			sumDown = 0;
			tagePlus = 0;
			tageMinus = 0;
		}
	}

	@Override
	public String getKurzname() {
		return "RSIRelativ";
	}

}
