package com.algotrading.signal;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.depot.Order;
import com.algotrading.indikator.IndikatorAlgorithmus;

/**
 * Für einen beliebigen Indikator geeignet Berechnet die Extremwerte in einem
 * vergangenen Zeitraum Sendet ein Signal, solange sich der Kurs aktuell im
 * Extrembereich befindet. Oder nur bei Durchbruch in die Extremzone Parameter:
 * dauer - für den beobachteten Zeitraum in dem die Extremwerte bestimmt werden
 * schwelle - für den Extrem-Wertebereich - in Standardabweichungen 1 = 68 %
 * oben und unten; 2 = 95 %; 3 = 99,73% durchbruch (optional) - 0 = tägliches
 * Signal in der Extremzone (Standard) 1 = nur bei Durchbruch indikator - der
 * Indikator der bewertet wird. Stärke des Signals: Prozentuale Abweichung des
 * Indikators vom Durchschnitt
 * 
 * @author Oskar
 *
 */
@Entity(name = "SignalstrategieMinMax")
@DiscriminatorValue("SignalstrategieMinMax")
public class SignalMinMax extends SignalAlgorithmus {
	static final Logger log = LogManager.getLogger(SignalMinMax.class);

	@Override
	public int rechne(Aktie aktie) {
		if (aktie == null)
			log.error("Inputparameter Aktie ist null");
		int anzahl = 0;
		// *** indikator ***
		IndikatorAlgorithmus indikator = (IndikatorAlgorithmus) getIndikatorAlgorithmen().get(0);
		if (indikator == null)
			log.error("Signal enthaelt keinen Indikator");
		// *** dauer ***
		int dauer = (Integer) getParameter("dauer");
		if (dauer == 0)
			log.error("IndikatorMinMax enthält keine Dauer");
		// *** schwelle ***
		Object o = getParameter("schwelle");
		if (o == null)
			log.error("IndikatorMinMax enthält keine Schwelle");
		float schwelle = (Float) o;
		if (schwelle == 0)
			log.error("IndikatorMinMax enthält keine Schwelle");
		// *** durchbruch - optional - Stand = 0
		float durchbruch = 0;
		Object od = getParameter("durchbruch");
		if (od != null)
			durchbruch = (Integer) od;

		boolean durchbruchOben = false;
		boolean durchbruchUnten = false;

		List<Kurs> kurse = aktie.getKursListe();
		Kurs kurs;
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// beim Einfügen weiterer Werte fliegt automatisch der erst raus
		stats.setWindowSize(dauer);
		// die Werte auffüllen ohne Berechnung
		for (int i = 0; i < dauer; i++) {
			kurs = kurse.get(i);
			Float Value = getValue(indikator, kurs);
			// wenn kein Indikator vorhanden ist, wird nichts unternommen
			if (Value == null) {
				System.out.println("Kurs ohne Indikator" + kurs.toString());
				continue;
			}
			stats.addValue(Value);
		}
		for (int i = dauer; i < aktie.getKursListe().size(); i++) {
			kurs = kurse.get(i);
			Float Value = getValue(indikator, kurs); // der Einzel-Wert
			if (Value == null) {
				System.out.println("kein Indikatorwert");
				continue;
			}
			float value = (float) Value;

			stats.addValue(value);
			// Standardabweichung
			// Erwartungswert +- Standardabweichung = 68% der Werte.
			double staAbw = stats.getStandardDeviation();
			// der Durchschnitt (Mittelwert)
			double durchschnitt = stats.getMean();
			double grenze = schwelle * staAbw;
			// die Schwelle nach oben
			double wertOben = durchschnitt + grenze;
			// die Schwelle nach unten
			double wertUnten = durchschnitt - grenze;
			// Prüfung ob der Value oberhalb der Ober-Grenze liegt
			if (value > wertOben) {
				// Maximalwert liegt vor
				if (durchbruch == 0) { // jeder Maximalwert ergibt ein Signal
					Signal signal = kurs.createSignal(this, Order.KAUF, 0);
					// positive Werte: value - durchschnitt
					rechneStaerke(value, durchschnitt, signal);
					anzahl++;
				}
				// Signale werden nur erzeugt bei Durchbruch und bisher unter Maximalwert
				else if (durchbruchOben == false) {
					Signal signal = kurs.createSignal(this, Order.KAUF, 0);
					rechneStaerke(value, durchschnitt, signal);
					anzahl++;
					durchbruchOben = true; // ein Durchbruch nach oben hat statt gefunden
				}
			} else { // der Wert liegt nicht oberhalb der Ober-Grenze
				durchbruchOben = false;
			}
			if (value < wertUnten) { // Prüfung, ob der Wert unterhalb der Unter-Grenze liegt
				// Minimalwert liegt vor
				if (durchbruch == 0) {
					Signal signal = kurs.createSignal(this, Order.VERKAUF, 0);
					// negative Werte
					rechneStaerke(value, durchschnitt, signal);
					anzahl++;
				}
				// Signale nur bei Durchbruch und bisher unter Maximalwert
				else if (durchbruchUnten == false) {
					Signal signal = kurs.createSignal(this, Order.KAUF, 0);
					rechneStaerke(value, durchschnitt, signal);
					anzahl++;
					durchbruchUnten = true; // Durchbruch nach unten hat statt gefunden
				}
			} else { // der Wert liegt nicht unterhalb der Untergrenze
				durchbruchUnten = false;
			}
		}
		return anzahl;
	}

	/**
	 * Rechnet die Staerke aus Differenz zwischen Wert und Durchschnitt im
	 * Verhältnis zum Durchschnitt positiv, wenn Wert über Durchschnitt negativ,
	 * wenn Wert unter Durchschnitt
	 * Um das Wieviel-fache übersteigt der Kurs den Durchschnitt. 
	 */
	private void rechneStaerke(float value, double durchschnitt, Signal signal) {
		signal.setStaerke((value - (float) durchschnitt) / (float) durchschnitt);
	}

	/**
	 * Ermittelt den zu beobachtenden Wert Derzeit der Indikator, könnte auch ein
	 * Kurs sein
	 */
	private Float getValue(IndikatorAlgorithmus indikator, Kurs kurs) {

		return kurs.getIndikatorWert(indikator);

	}

	@Override
	public String getKurzname() {
		return "MinMax";
	}

}
