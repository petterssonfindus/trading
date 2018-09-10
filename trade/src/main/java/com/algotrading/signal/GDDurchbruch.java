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

	// wie weit muss der Tageskurs den Gleitenden Durchschnitt durchbrechen
	private static final float SCHWELLEGDDURCHBRUCH = 0.00f;

	/**
	 * erzeugt ein Signal, wenn der Tageskurs den GD schneidet 
	 * Stärke ist maximal, wenn alle 3 GDs äber/unter dem Tageskurs sind 
	 * @param kursreihe
	 */
	public int ermittleSignal(Aktie aktie, SignalBeschreibung signalbeschreibung) {
		if (aktie == null) log.error("Inputparameter Aktie ist null");
		if (signalbeschreibung == null) log.error("Inputparameter Signalbeschreibung ist null");
		int anzahl = 0;
		IndikatorBeschreibung indikator = (IndikatorBeschreibung) signalbeschreibung.getParameter("indikator");
		if (indikator == null) log.error("Signal enthaelt keinen Indikator");
		Zeitraum zeitraum = (Zeitraum) signalbeschreibung.getParameter("zeitraum");
		
		for (Kurs kurs : aktie.getKurse(zeitraum)) {
			Kurs vortageskurs = aktie.getVortageskurs(kurs);
			if (vortageskurs != null) {
				// bisher darunter, jetzt daräber
				// dabei werden die Signale erstellt und mit dem Tageskurs verbunden 
				if (GDDurchbruch.pruefeGleitenderDurchschnittSteigung(kurs, vortageskurs, indikator)) anzahl++;
				
				// bisher daräber, jetzt darunter
				if (GDDurchbruch.pruefeGleitenderDurchschnittSinkflug(kurs, vortageskurs, indikator)) anzahl++;
			}
		}
		return anzahl; 
	}

	/**
	 * bisher darunter, jetzt daräber
	 * erzeugt Signale und hängt sie an den Kurs an
	 */
	private static boolean pruefeGleitenderDurchschnittSteigung (Kurs tageskurs, Kurs vortageskurs, IndikatorBeschreibung indikator ) {
		if (tageskurs == null || vortageskurs == null || indikator == null) log.error("Inputvariable ist null"); 
		boolean result = false; 
		Float gd = tageskurs.getIndikatorWert(indikator);
		Float gdvt = vortageskurs.getIndikatorWert(indikator);
		log.trace("GD-Signal Steigung: " + Util.formatDate(tageskurs.datum) + " - " + vortageskurs.getKurs() + " GDVt: " + gdvt + 
				" Kurs: " + tageskurs.getKurs() + " GD: " + gd);
		Signal signal = null; 
		// wenn am Vortag der Kurs unter dem GD war, und heute der Kurs äber dem GD ist 
		if ((vortageskurs.getKurs() < gdvt + GDDurchbruch.SCHWELLEGDDURCHBRUCH) && 
				tageskurs.getKurs() > (gd + GDDurchbruch.SCHWELLEGDDURCHBRUCH)) {
			signal = Signal.create(tageskurs, Order.KAUF, Signal.GDDurchbruch, 0);
			result = true; 
			signal.staerke = (tageskurs.getKurs() - gd) / gd;
			log.debug("GD-Steigung erkannt: " + Util.formatDate(tageskurs.datum) + " VTKurs " + vortageskurs.getKurs() + " GDVt: " + gdvt + 
					" Kurs: " + tageskurs.getKurs() + " GD: " + gd);
		} 
		return result; 
	}

	/**
	 * bisher daräber, jetzt darunter
	 */
	private static boolean pruefeGleitenderDurchschnittSinkflug (Kurs tageskurs, Kurs vortageskurs, IndikatorBeschreibung indikator ) {
		if (tageskurs == null || vortageskurs == null || indikator == null) log.error("Inputvariable ist null"); 
		boolean result = false; 
		Float gd = tageskurs.getIndikatorWert(indikator);
		Float gdvt = vortageskurs.getIndikatorWert(indikator);
		Signal signal = null; 
		
		if ((vortageskurs.getKurs() > gdvt - GDDurchbruch.SCHWELLEGDDURCHBRUCH) && 
				tageskurs.getKurs() < (gd - GDDurchbruch.SCHWELLEGDDURCHBRUCH)) {
			signal = Signal.create(tageskurs, Order.VERKAUF, Signal.GDDurchbruch, 0);
			result = true;
			signal.staerke = (gd - tageskurs.getKurs()) / gd;
			log.debug("GD-Sinkflug: " + Util.formatDate(tageskurs.datum) + " VTKurs " + vortageskurs.getKurs() + " GDVt: " + gdvt + 
					" Kurs: " + tageskurs.getKurs() + " GD: " + gd);
		} 
		return result; 
	}

}
