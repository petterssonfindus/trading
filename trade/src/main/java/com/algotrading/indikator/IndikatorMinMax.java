package com.algotrading.indikator;

import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

/**
 * Misst, wie weit der aktuelle Wert vom Durchschnitt entfernt ist. Einheit:
 * StandardAbweichungen vom Durchschnitt entfernt. Parameter: dauer - für den
 * beobachteten Zeitraum in dem die Extremwerte bestimmt werden
 * 
 * @author Oskar
 *
 */
@Entity(name = "MinMax")
@DiscriminatorValue("MinMax")
public class IndikatorMinMax extends IndikatorAlgorithmus {
	static final Logger log = LogManager.getLogger(IndikatorMinMax.class);

	@Override
	public void rechne(Aktie aktie) {
		if (aktie == null)
			log.error("Inputparameter Aktie ist null");

		// *** indikator ***
		// *** dauer ***
		int dauer = (Integer) getParameter("dauer");
		if (dauer == 0)
			log.error("IndikatorMinMax enthält keine Dauer");

		ArrayList<Kurs> kurse = aktie.getKursListe();
		Kurs kurs;
		DescriptiveStatistics stats = new DescriptiveStatistics();
		// beim Einfügen weiterer Werte fliegt automatisch der erst raus
		stats.setWindowSize(dauer);
		// die Werte auffüllen ohne Berechnung
		for (int i = 0; i < dauer; i++) {
			kurs = kurse.get(i);
			Float Value = kurs.getKurs();
			stats.addValue(Value);
		}
		for (int i = dauer; i < aktie.getKursListe().size(); i++) {
			kurs = kurse.get(i);
			float value = kurs.getKurs();

			stats.addValue(value);
			// Standardabweichung
			// Erwartungswert +- Standardabweichung = 68% der Werte.
			double staAbw = stats.getStandardDeviation();
			// der Durchschnitt (Mittelwert)
			double durchschnitt = stats.getMean();
			// Differenz zwischen Kurs und Durchschnitt / Stabw.
			// wie viele Stabw. ist der aktuelle Kurs vom Durchschnitt entfernt ?

			float result = (float) ((value - durchschnitt) / staAbw);
			// mit dem Ergebnis den Indkator erzeugen.
			kurs.addIndikator(this, result);

		}
	}

	@Override
	public String getKurzname() {
		return "MinMax";
	}

}
