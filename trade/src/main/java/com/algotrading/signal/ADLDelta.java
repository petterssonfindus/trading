package com.algotrading.signal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.util.Zeitraum;
import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.depot.Order;
/**
 * Wenn sich der ADL-Wert zum Vortag > Faktor x ändert, wird ein Signal erzeugt
 * Parameter: indikator = IndikatorAlgorithmus vom Typ ADL
 * Parameter: schwelle = Faktor der Änderung im Format 1.0x 
 * Parameter: zeitraum = Zeitraum 
 * @author oskar
 *
 */
public class ADLDelta extends SignalAlgorithmus {
	static final Logger log = LogManager.getLogger(ADLDelta.class);

	public int rechne(Aktie aktie) {
		if (aktie == null) log.error("Inputparameter Aktie ist null");
		
		// hole die Parameter, die bei der Konfiguration eingesetzt wurden. 
		IndikatorAlgorithmus indikator = (IndikatorAlgorithmus) getParameter("indikator");
		float schwelle = (Float) getParameter("schwelle");
		if (indikator == null) log.error("Signal enthaelt keinen Indikator");
		Zeitraum zeitraum = (Zeitraum) getParameter("zeitraum");
		
		int anzahl = 0;
		for (Kurs kurs : aktie.getKurse(zeitraum)) {
			
			Kurs vortageskurs = aktie.getVortageskurs(kurs);
			if (vortageskurs != null) {
				float delta = kurs.getIndikatorWert(indikator) / vortageskurs.getIndikatorWert(indikator);
				if (delta > schwelle ) {
					anzahl++;
					kurs.createSignal( this, Order.KAUF, delta);
				}
			}
		}
		return anzahl; 
	}

	@Override
	public String getKurzname() {
		return "ADLDelta";
	}

}
