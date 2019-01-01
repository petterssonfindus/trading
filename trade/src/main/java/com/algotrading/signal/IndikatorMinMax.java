package com.algotrading.signal;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.depot.Order;
import com.algotrading.indikator.IndikatorBeschreibung;

/**
 * Für einen beliebigen Indikator geeignet
 * Berechnet die Extremwerte in einem vergangenen Zeitraum 
 * Sendet ein Signal, solange sich der Kurs aktuell im Extrembereich befindet. 
 * Oder nur bei Durchbruch in die Extremzone 
 * Parameter: dauer - für den beobachteten Zeitraum in dem die Extremwerte bestimmt werden
 *            schwelle - für den Extrem-Wertebereich - in Standardabweichungen 
 *            			1 = 68 % oben und unten; 2 = 95 %; 3 = 99,73%
 *            durchbruch - 0 = tägliches Signal in der Extremzone
 *            			   1 = nur bei Durchbruch 
 * @author Oskar
 *
 */
public class IndikatorMinMax implements SignalAlgorithmus {
	static final Logger log = LogManager.getLogger(IndikatorMinMax.class);

	@Override
	public int ermittleSignal(Aktie aktie, SignalBeschreibung sB) {
		if (aktie == null) log.error("Inputparameter Aktie ist null");
		if (sB == null) log.error("Inputparameter Signalbeschreibung ist null");
		int anzahl = 0;
		IndikatorBeschreibung indikator = (IndikatorBeschreibung) sB.getParameter("indikator");
		if (indikator == null) log.error("Signal enthaelt keinen Indikator");
		int dauer = (Integer) sB.getParameter("dauer");
		if (dauer == 0) log.error("IndikatorMinMax enthält keine Dauer" );
		float schwelle = (Float) sB.getParameter("schwelle");
		if (schwelle == 0) log.error("IndikatorMinMax enthält keine Schwelle" );
		float durchbruch = (Integer) sB.getParameter("durchbruch");
		boolean durchbruchOben = false;
		boolean durchbruchUnten = false;
		schwelle += 1;
				
		Kurs tageskurs; 
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// beim Einfügen weiterer Werte fliegt automatisch der erst raus
		stats.setWindowSize(dauer);
		// die Werte auffüllen ohne Berechnung
		for (int i = 0 ; i < dauer ; i++) {
			stats.addValue(aktie.getBoersenkurse().get(i).getKurs());
		}
		for (int i = dauer ; i < aktie.getBoersenkurse().size() ; i++) {
			tageskurs = aktie.getBoersenkurse().get(i);
			float kurs = tageskurs.getKurs();
			stats.addValue(tageskurs.getKurs());
			// Standardabweichung 
			// Erwartungswert +- Standardabweichung = 68% der Werte. 
			double staAbw = stats.getStandardDeviation();
			// der Durchschnitt 
			double durchschnitt = stats.getMean();
			double grenze = schwelle * staAbw;
			double wertOben = durchschnitt + grenze; 
			double wertUnten = durchschnitt - grenze; 
			// Prüfung ob der aktuelle Tageskurs oberhalb der Ober-Grenze liegt
			if (kurs > wertOben) {
				// Maximalwert liegt vor 
				if (durchbruch == 0) {
					Signal signal = Signal.create(sB, tageskurs, Order.KAUF, 0);
					signal.staerke = kurs - (float) durchschnitt; 
					anzahl ++; 
				}
				// Signale nur bei Durchbruch und bisher unter Maximalwert
				else if (durchbruchOben == false) {
					Signal signal = Signal.create(sB, tageskurs, Order.KAUF, 0);
					signal.staerke = kurs - (float) durchschnitt; 
					anzahl ++; 
					durchbruchOben = true; 
				}
			}
			else {
				durchbruchOben = false; 
			}
			if (kurs < wertUnten) {
				// Minimalwert liegt vor 
				if (durchbruch == 0) {
					Signal signal = Signal.create(sB, tageskurs, Order.VERKAUF, 0);
					signal.staerke = (float) durchschnitt - kurs; 
					anzahl ++; 
				}
				// Signale nur bei Durchbruch und bisher unter Maximalwert
				else if (durchbruchUnten == false) {
					Signal signal = Signal.create(sB, tageskurs, Order.KAUF, 0);
					signal.staerke = (float) durchschnitt - kurs; 
					anzahl ++; 
					durchbruchUnten = true; 
				}
			}
			else {
				durchbruchUnten = false; 
			}
		}
		return anzahl;
	}

}
