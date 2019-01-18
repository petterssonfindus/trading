package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.util.Util;
/**
 * Multipliziert 2 bestehende Indikatoren 
 * Berücksichtigt einen zusätzlichen festen Faktor 
 * Jeder Indikator kann als Nenner verwendet werden durch reziproken Wert 
 * parameter: 	reziprok1 (optional) - Integer 0 oder null = Zähler  1 = Nenner
 * 				reziprok2 (optional) - Integer 0 oder null = Zähler  1 = Nenner
 * 				faktor
 * @author oskar
 */
public class IndikatorMultiplikation extends IndikatorAlgorithmus {
	
	@Override
	public void rechne(Aktie aktie) {
		IndikatorAlgorithmus iA1 = (IndikatorAlgorithmus) getParameter("indikator1");
		IndikatorAlgorithmus iA2 = (IndikatorAlgorithmus) getParameter("indikator2");
		
		float faktor = 1;
		Float faktorF = ((Float) getParameter("faktor"));
		if (faktorF != null) faktor = faktorF; 
		
		int reziprok1 = 0; 
		Integer reziprok1I = ((Integer) getParameter("reziprok1"));
		if (reziprok1I != null) reziprok1 = reziprok1I; 
		
		int reziprok2 = 0; 
		Integer reziprok2I = ((Integer) getParameter("reziprok2"));
		if (reziprok2I != null) reziprok2 = reziprok2I; 

		float ergebnis  = 0;
		for (Kurs kurs : aktie.getBoersenkurse()) {
			// hole den Indikator-Wert1 an dem Kurs 
			Float iA1V = kurs.getIndikatorWert(iA1);
			if (iA1V == null) continue; 
			float value1 = iA1V.floatValue();
			// wenn reziprok eingeschalten, dann umdrehen. 
			if (reziprok1 == 1) value1 = 1 / value1; 
			// hole den Indikator-Wert2 an dem Kurs 
			Float iA2V = kurs.getIndikatorWert(iA2);
			if (iA2V == null) continue; 
			float value2 = iA2V.floatValue();
			// wenn reziprok eingeschalten, dann umdrehen. 
			if (reziprok2 == 1) value2 = 1 / value2; 

			ergebnis = value1 * value2 * faktor; 
			kurs.addIndikator(this, ergebnis);
		}
	}

	@Override
	public String getKurzname() {
		return "Multiplikation"; 
	}

}
