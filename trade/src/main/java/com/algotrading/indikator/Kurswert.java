package com.algotrading.indikator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

/**
 * ein Indikator als Kurs des selben Tages 
 * Parameter typ = open, close, high, low, volume
 * 					1 = open 
 * 					2 = close
 * 					3 = high
 * 					4 = low
 * 					5 = volume
 * @author oskar
 *
 */
public class Kurswert implements IndikatorAlgorithmus {
	static final Logger log = LogManager.getLogger(Kurswert.class);
	
	private static Kurswert instance; 
	
	public static Kurswert getInstance () {
		if (instance == null) instance = new Kurswert(); 
		return instance; 
	}

	@Override
	public void rechne(Aktie aktie, IndikatorBeschreibung iB) {
		Object o = iB.getParameter("typ");
		if (o == null) log.error("Kurswert enthält keinen Typ" );
		int typ = (Integer) o ;
		if (typ == 0) log.error("Kurswert enthält keinen Typ " );
		// alle Kurse werden ergänzt um den Indikator
		for (Kurs kurs : aktie.getBoersenkurse()) {
			kurs.addIndikator(iB, getValue(kurs, typ));
		}
		
	}
	
	private float getValue (Kurs kurs, int typ) {
		float result = 0; 
		switch (typ) {
			case 1: 
				result = kurs.open;
				break;
			case 2: 
				result = kurs.close;
				break;
			case 3: 
				result = kurs.high;
				break;
			case 4: 
				result = kurs.low;
				break;
			case 5: 
				result = kurs.volume;
				break;
		}
		return result;
	}

}
