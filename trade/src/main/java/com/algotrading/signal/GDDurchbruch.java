package com.algotrading.signal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.depot.Order;
import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;
import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

public class GDDurchbruch implements SignalAlgorithmus {
	static final Logger log = LogManager.getLogger(GDDurchbruch.class);

	/**
	 * erzeugt ein Signal, wenn der Tageskurs den GD schneidet 
	 * Stärke sagt aus, wie weit der Kurs vom GD abweicht. 
	 * Parameter "schwelle" - Dezimalwert der Überschreitung 
	 * Parameter "indikator" - die zugehörige GD-IndikatorBeschreibung
	 * Parameter "zeitraum" - der zu berechnende Zeitraum
	 * @param kursreihe
	 */
	public int ermittleSignal(Aktie aktie, SignalBeschreibung sB) {
		if (aktie == null) log.error("Inputparameter Aktie ist null");
		if (sB == null) log.error("Inputparameter Signalbeschreibung ist null");
		int anzahl = 0;
		IndikatorBeschreibung indikator = (IndikatorBeschreibung) sB.getParameter("indikator");
		// wie weit muss der Tageskurs den Gleitenden Durchschnitt durchbrechen
		float schwelle = (Float) sB.getParameter("schwelle");
		schwelle = schwelle + 1;
		if (indikator == null) log.error("Signal enthaelt keinen Indikator");
		Zeitraum zeitraum = (Zeitraum) sB.getParameter("zeitraum");
		
		for (Kurs kurs : aktie.getKurse(zeitraum)) {
			Kurs vortageskurs = aktie.getVortageskurs(kurs);
			if (vortageskurs != null) {
				// bisher darunter, jetzt darüber
				// dabei werden die Signale erstellt und mit dem Tageskurs verbunden 
				if (GDDurchbruch.pruefeGleitenderDurchschnittSteigung(sB, kurs, vortageskurs, indikator, schwelle)) anzahl++;
				
				// bisher darüber, jetzt darunter
				if (GDDurchbruch.pruefeGleitenderDurchschnittSinkflug(sB, kurs, vortageskurs, indikator, schwelle)) anzahl++;
			}
		}
		return anzahl; 
	}

	/**
	 * bisher darunter, jetzt darüber
	 * erzeugt Signale und hängt sie an den Kurs an
	 */
	private static boolean pruefeGleitenderDurchschnittSteigung (SignalBeschreibung sB, Kurs tageskurs, Kurs vortageskurs, IndikatorBeschreibung iB, float schwelle ) {
		if (tageskurs == null || vortageskurs == null || iB == null) log.error("Inputvariable ist null"); 
		boolean result = false; 
		Float gd = tageskurs.getIndikatorWert(iB);
		Float gdvt = vortageskurs.getIndikatorWert(iB);
		log.trace("GD-Signal Steigung: " + Util.formatDate(tageskurs.datum) + " - " + vortageskurs.getKurs() + " GDVt: " + gdvt + 
				" Kurs: " + tageskurs.getKurs() + " GD: " + gd);
		Signal signal = null; 
		// wenn am Vortag der Kurs unter dem GD war, und heute der Kurs über dem GD ist 
		if ((vortageskurs.getKurs() < (gdvt * schwelle)) && 
				tageskurs.getKurs() > (gd * schwelle)) {
			signal = Signal.create(sB, tageskurs, Order.KAUF, 0);
			result = true; 
			signal.staerke = (tageskurs.getKurs() - gd) / gd;
			log.debug("GD-Steigung erkannt: " + Util.formatDate(tageskurs.datum) + " VTKurs " + vortageskurs.getKurs() + " GDVt: " + gdvt + 
					" Kurs: " + tageskurs.getKurs() + " GD: " + gd);
		} 
		return result; 
	}

	/**
	 * bisher darüber, jetzt darunter
	 */
	private static boolean pruefeGleitenderDurchschnittSinkflug (SignalBeschreibung sB, Kurs tageskurs, Kurs vortageskurs, IndikatorBeschreibung indikator, float schwelle ) {
		if (tageskurs == null || vortageskurs == null || indikator == null) log.error("Inputvariable ist null"); 
		boolean result = false; 
		Float gd = tageskurs.getIndikatorWert(indikator);
		Float gdvt = vortageskurs.getIndikatorWert(indikator);
		Signal signal = null; 
		
		if ((vortageskurs.getKurs() > (gdvt * schwelle)) && 
				tageskurs.getKurs() < (gd * schwelle)) {
			signal = Signal.create(sB, tageskurs, Order.VERKAUF, 0);
			result = true;
			signal.staerke = (gd - tageskurs.getKurs()) / gd;
			log.debug("GD-Sinkflug: " + Util.formatDate(tageskurs.datum) + " VTKurs " + vortageskurs.getKurs() + " GDVt: " + gdvt + 
					" Kurs: " + tageskurs.getKurs() + " GD: " + gd);
		} 
		return result; 
	}

}
