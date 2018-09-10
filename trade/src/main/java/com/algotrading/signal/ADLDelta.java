package com.algotrading.signal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.util.Zeitraum;
import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
/**
 * Wenn sich der ADL-Wert zum Vortag um Faktor x ändert, wird ein Signal erzeugt
 * @author oskar
 *
 */
public class ADLDelta implements SignalAlgorithmus {
	static final Logger log = LogManager.getLogger(ADLDelta.class);

	public int ermittleSignal(Aktie aktie, SignalBeschreibung signalbeschreibung) {
		if (aktie == null) log.error("Inputparameter Aktie ist null");
		if (signalbeschreibung == null) log.error("Inputparameter Signalbeschreibung ist null");
		// hole die Parameter, die bei der Konfiguration eingesetzt wurden. 
		IndikatorBeschreibung indikator = (IndikatorBeschreibung) signalbeschreibung.getParameter("indikator");
		float schwelle = (Float) signalbeschreibung.getParameter("schwelle");
		if (indikator == null) log.error("Signal enthaelt keinen Indikator");
		Zeitraum zeitraum = (Zeitraum) signalbeschreibung.getParameter("zeitraum");
		int anzahl = 0;
		for (Kurs kurs : aktie.getKurse(zeitraum)) {
			
			Kurs vortageskurs = aktie.getVortageskurs(kurs);
			if (vortageskurs != null) {
				float delta = Math.abs(kurs.getIndikatorWert(indikator) / vortageskurs.getIndikatorWert(indikator));
				if (delta > schwelle ) anzahl++;
				
			}
		}
		return anzahl; 
	}

}
