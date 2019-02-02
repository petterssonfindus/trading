package com.algotrading.indikator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.util.MathUtil;

/**
 * ein Indikator als Kurs des selben Tages 
 * Parameter typ = open, close, high, low, volume
 * 					1 = open 
 * 					2 = close
 * 					3 = high
 * 					4 = low
 * 					5 = volume
 * Parameter aktie = Name der Aktie, die referenziert wird (optional) 
 * Parameter stabw = Anzahl Tage für die Standardabweichung. Damit wird der Indikator ersetzt. 
 * Parameter relativ = wenn vorhanden, dann: wie viele Standabweichungen ist der Kurs vom Durchschnitt entfernt
 * @author oskar
 *
 */
public class IndikatorAbweichung extends IndikatorAlgorithmus {
	static final Logger log = LogManager.getLogger(IndikatorAbweichung.class);
	
	@Override
	public void rechne(Aktie aktie) {
		Aktie aktie2; 
		Kurs kurs2 = null; 
		Kurs kurs2a = null;
		Object oa = getParameter("aktie");
		if (oa != null) {
			aktie2 = Aktien.getInstance().getAktie(oa.toString());
		}
		else {
			aktie2 = aktie; 
		}
		
		int typ = 0;
		Object o = getParameter("typ");
		if (o == null) typ = 1; 
		else typ = (Integer) o ;
		

		// alle Kurse werden ergänzt um den Indikator
		
		for (Kurs kurs : aktie.getBoersenkurse()) {	// der Kurs der Ziel-Aktie 
			if (oa != null) { // wenn der Quell-Kurs aus einer anderen Aktie stammt 
				kurs2a = kurs2; // den Vortageskurs setzen 
				kurs2 = aktie2.getKurs(kurs.getDatum()); // anhand des gleichen Datums den Quell-Kurs holen 
				if (kurs2 == null) {	// es gibt keinen Quell-Kurs 
					if (kurs2a != null) {  // aber es gibt einen Vortageskurs
						kurs2 = kurs2a; // benutze den Vortagskurs 
					}
					else {	// weder Quell-Kurs noch Vortags-Kurs 
						continue;  // nächster Kurs
					}
				}
			}
			else kurs2 = kurs;
			if (kurs2 == null) {
				log.error("Zugriff auf Quell-Kurs liefert null");
			}
			
			// den Quell-Kurs holen und als Indikator eintragen. 
			kurs.addIndikator(this, getValue(kurs2, typ));
		}
		
		// zum Schluss wird die Faktorisierung und Logarithmierung durchgeführt
		MathUtil.transformiereIndikator(aktie, this);
		
	}
	
	private float getValue (Kurs kurs, int typ) {
		if (kurs == null) {
			log.error("Zugriff auf Quell-Kurs liefert null");
		}
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

	@Override
	public String getKurzname() {
		return "KW";
	}

}
