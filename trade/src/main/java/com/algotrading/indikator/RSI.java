package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

public class RSI extends Indikator {

	// verhindert ein Instantiieren 
	private RSI() {};
	
	private static RSI instance; 
	
	public static RSI getInstance () {
		if (instance == null) instance = new RSI(); 
		return instance; 
	}
	
	/**
	 * Relative-Stärke-Index
	 * Das Verhältnis der durschnittlich positiven Tage zu den durchschnittlich negativen Tagen 
	 * Wie stark sind die guten Tage im Vergleich zu den schlechten Tagen. 
	 * Die Implementierung ist effizient. 
	 * An jedem Tag kommt eine neue Differnz hinzu, eine alte fällt weg. 
	 * @param aktie
	 * @param tage
	 */
	public void rechne (Aktie aktie, IndikatorBeschreibung indikatorBeschreibung ) {
		int tage = (Integer) indikatorBeschreibung.getParameter("tage"); 
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		float sumUp = 0;
		float sumDown = 0;
		float sumUpA = 0; // Summe Up Average
		float sumDownA = 0; // Summe Down Average
		float rsi; 
		float kurs = 0; 
		float kursVortag = kurse.get(0).getKurs(); 
		float differenz = 0;
		float differenzAlt = 0;
		float[] differenzen = new float[kurse.size()];
		
		// Vorbereitung der Kursdaten 
		for (int i = 0 ; i < tage ; i++) {
			Kurs kursO = kurse.get(i);
			kurs = kursO.getKurs();
			differenz = kurs - kursVortag;
			differenzen[i] = differenz; 
			
			// Summen addieren
			if (differenz > 0) sumUp += differenz;
			else sumDown += differenz;
	
			kursVortag = kurs; 
		}
		
		for (int i = tage ; i < kurse.size() ; i++) {
			Kurs kursO = kurse.get(i);
			kursVortag = kurs; 
			kurs = kursO.getKurs();
			differenz = kurs - kursVortag;
			differenzen[i] = differenz; 
			differenzAlt = differenzen[i - tage];
			
			// Summen anpassen: neue Differenz hinzuzählen
			if (differenz > 0) sumUp += differenz;
			else sumDown += differenz;
			// Alte Differenz abziehen
			if (differenzAlt > 0) sumUp -= differenzAlt;
			else sumDown -= differenzAlt; 
			
			// Durchschnitte berechnen
			sumUpA = sumUp / tage;
			sumDownA = Math.abs(sumDown / tage);
			// rsi berechnen
			rsi = sumUpA / (sumUpA + sumDownA);
			kursO.rsi = rsi; 
		
		}
	}

}
