package com.algotrading.signal;

import java.util.ArrayList;

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
 *            indikator - der Indikator der bewertet wird.
 * Stärke des Signals: Prozentuale Abweichung des Indikators vom Durchschnitt
 * @author Oskar
 *
 */
public class MinMax implements SignalAlgorithmus {
	static final Logger log = LogManager.getLogger(MinMax.class);

	@Override
	public int ermittleSignal(Aktie aktie, SignalBeschreibung sB) {
		if (aktie == null) log.error("Inputparameter Aktie ist null");
		if (sB == null) log.error("Inputparameter Signalbeschreibung ist null");
		int anzahl = 0;
		// *** indikator ***
		IndikatorBeschreibung indikator = (IndikatorBeschreibung) sB.getParameter("indikator");
		if (indikator == null) log.error("Signal enthaelt keinen Indikator");
		// *** dauer ***
		int dauer = (Integer) sB.getParameter("dauer");
		if (dauer == 0) log.error("IndikatorMinMax enthält keine Dauer" );
		// *** schwelle ***
		Object o = sB.getParameter("schwelle");
		if (o == null) log.error("IndikatorMinMax enthält keine Schwelle" );
		float schwelle = (Float) o ;
		if (schwelle == 0) log.error("IndikatorMinMax enthält keine Schwelle" );
		schwelle += 1;
		// *** durchbruch ***
		float durchbruch = (Integer) sB.getParameter("durchbruch");
		boolean durchbruchOben = false;
		boolean durchbruchUnten = false;
		
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		Kurs kurs; 
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// beim Einfügen weiterer Werte fliegt automatisch der erst raus
		stats.setWindowSize(dauer);
		// die Werte auffüllen ohne Berechnung
		for (int i = 0 ; i < dauer ; i++) {
			kurs = kurse.get(i);
			Float Value = getValue(indikator, kurs);
			// wenn kein Indikator vorhanden ist, wird nichts unternommen
			if (Value == null) continue; 
			stats.addValue(Value);
		}
		for (int i = dauer ; i < aktie.getBoersenkurse().size() ; i++) {
			kurs = kurse.get(i);
			Float Value = getValue(indikator, kurs);
			if (Value == null) continue; 
			float value = (float) Value; 
			
			stats.addValue(value);
			// Standardabweichung 
			// Erwartungswert +- Standardabweichung = 68% der Werte. 
			double staAbw = stats.getStandardDeviation();
			// der Durchschnitt 
			double durchschnitt = stats.getMean();
			double grenze = schwelle * staAbw;
			double wertOben = durchschnitt + grenze; 
			double wertUnten = durchschnitt - grenze; 
			// Prüfung ob der Value oberhalb der Ober-Grenze liegt
			if (value > wertOben) {
				// Maximalwert liegt vor 
				if (durchbruch == 0) { // die Signal-Bestimmung nimmt jeden Maximalwert 
					Signal signal = Signal.create(sB, kurs, Order.KAUF, 0);
					signal.staerke = value - (float) durchschnitt; 
					anzahl ++; 
				}
				// Signale nur bei Durchbruch und bisher unter Maximalwert
				else if (durchbruchOben == false) {
					Signal signal = Signal.create(sB, kurs, Order.KAUF, 0);
					signal.staerke = value - (float) durchschnitt; 
					anzahl ++; 
					durchbruchOben = true; 
				}
			}
			else {
				durchbruchOben = false; 
			}
			if (value < wertUnten) {  // Prüfung, ob der Wert unterhalb der Unter-Grenze liegt 
				// Minimalwert liegt vor 
				if (durchbruch == 0) {
					Signal signal = Signal.create(sB, kurs, Order.VERKAUF, 0);
					signal.staerke = (float) durchschnitt - value; 
					anzahl ++; 
				}
				// Signale nur bei Durchbruch und bisher unter Maximalwert
				else if (durchbruchUnten == false) {
					Signal signal = Signal.create(sB, kurs, Order.KAUF, 0);
					signal.staerke = (float) (durchschnitt - value) / (float) durchschnitt; 
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
	
	/**
	 * Ermittelt den zu beobachtenden Wert 
	 * Derzeit der Indikator, könnte auch ein Kurs sein 
	 */
	private Float getValue(IndikatorBeschreibung indikator, Kurs kurs) {
		
		return kurs.getIndikatorWert(indikator);
		
	}

}
