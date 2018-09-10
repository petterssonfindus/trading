package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

public class BergTal {

	/**
	 * rechnet Berg, wenn nach vorne und nach hinten die Kurse fallen 
	 * geht beliebig weit nach vorne und hinten, solange die Kurs kontinuierlich sinken.
	 * Differenz zu jedem Tag wird addiert
	 * @param aktie
	 * @return
	 */
	static void rechne (Aktie aktie, int dauer) {
		
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		float[] kursArray = aktie.getKursArray();
		Kurs kurs; 
		float kursdiffVorAlt = 0;
		float kursdiffZurueckAlt = 0;
		float kursdiffVor = 0;
		float kursdiffZurueck = 0;
		float summe = 0;
		int position; 
		boolean istBerg = false; 
		
		for (int i = dauer; i < kurse.size() ; i++) {
			kurs = kurse.get(i);
			position = 1;
			float ausgangskurs = kursArray[i];
			do {
				summe += kursdiffVor + kursdiffZurueck; // Kursdifferenzen werden zur Summe addiert 
				kursdiffVorAlt = kursdiffVor; // alte Kursdifferenz merken
				kursdiffZurueckAlt = kursdiffZurueck; // alte Kursdifferenz merken
				// neue Kursdifferenzen berechnen 
				kursdiffVor = ausgangskurs - kursArray[i + position]; // Kursdifferenz berechnen 
				kursdiffZurueck = ausgangskurs - kursArray[i - position]; // Kursdifferenz berechnen 
				position ++ ;  // ein Tag weiter
	
				// wenn die Kursdifferenz nach vorne und hinten von Tag zu Tag steigt 
				// UND die Position grääer bleibt als die erlaubte Dauer 
				istBerg = kursdiffVor > kursdiffVorAlt && kursdiffZurueck > kursdiffZurueckAlt && position > i - dauer;
				if (istBerg) {
					// wenn ein Berg identifiziert wurde, wird die Summe eingetragen 
					// mit jedem erfolgreichen Durchlauf wird die Summe grääer
					kurs.berg = summe; 
				}
			}
			while (istBerg);
		}
	}

}
