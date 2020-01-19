package com.algotrading.signal;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.depot.Order;
import com.algotrading.indikator.IndikatorAlgorithmus;

/**
 * Wenn sich der aktuelle Kurs vom Indikator weiter wegbewegt als die Schwelle 
 * @parameter indikator
 * @parameter schwelle 
 */
@Entity(name = "SignalstrategieSchwelle")
@DiscriminatorValue("SignalstrategieSchwelle")
public class SignalSchwelle extends SignalAlgorithmus {
	static final Logger log = LogManager.getLogger(SignalSchwelle.class);

	@Override
	public int rechne(@NotNull Aktie aktie) {
		int anzahl = 0;
		// *** indikator ***
		IndikatorAlgorithmus indikator = (IndikatorAlgorithmus) getIndikatorAlgorithmen().get(0);
		if (indikator == null)
			log.error("Signal enthaelt keinen Indikator");
		// *** schwelle ***
		Object o = getParameter("schwelle");
		if (o == null)
			log.error("IndikatorSchwelle enthält keine Schwelle");
		float schwelle = (Float) o;
		if (schwelle == 0)
			log.error("IndikatorSchwelle enthält keine Schwelle");

		List<Kurs> kurse = aktie.getKursListe();
		for (Kurs kurs : aktie.getKursListe()) {
			Float Value = getValue(indikator, kurs); // der Indikator-Wert
			if (Value == null) {
				continue;
			}
			float value = (float) Value;
			// Kurs oberhalb Indikator * Schwelle 
			if (kurs.getKurs() > ((1 + schwelle) * value)) {
				// Signal erzeugen 
				Signal signal = kurs.createSignal(this, Order.KAUF, 0);
				// wie weit ist der Kurs oberhalb des Indikators
				signal.setStaerke(kurs.getKurs() / value);
				anzahl++;
			} else if (kurs.getKurs() < ((1 - schwelle) * value)) {
				// Signal erzeugen 
				Signal signal = kurs.createSignal(this, Order.VERKAUF, 0);
				// wie weit ist der Kurs unterhalb des Indikators
				signal.setStaerke(kurs.getKurs() / value);
				anzahl++;
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
