package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
/**
 * Multipliziert 2 bestehende Indikatoren 
 * @author oskar
 */
public class IndikatorMultiplikation extends IndikatorAlgorithmus {
	
	@Override
	public void rechne(Aktie aktie) {
		IndikatorAlgorithmus iA1 = (IndikatorAlgorithmus) getParameter("indikator1");
		IndikatorAlgorithmus iA2 = (IndikatorAlgorithmus) getParameter("indikator2");
		float i1 = 0; 
		float i2 = 0; 
		float ergebnis  = 0;
		for (Kurs kurs : aktie.getBoersenkurse()) {
			i1 = kurs.getIndikatorWert(iA1);
			i2 = kurs.getIndikatorWert(iA2);
			ergebnis = i1 * i2; 
			kurs.addIndikator(this, ergebnis);
			i1 = 0;
			i2 = 0;
		}
	}

	@Override
	public String getKurzname() {
		return "Multiplikation"; 
	}

}
