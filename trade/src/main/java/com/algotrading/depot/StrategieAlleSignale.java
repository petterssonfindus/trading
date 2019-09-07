package com.algotrading.depot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Kurs;
import com.algotrading.signal.Signal;

public class StrategieAlleSignale extends SignalStrategie {
	static final Logger log = LogManager.getLogger(StrategieAlleSignale.class);

	/**
	 * Nutzt jedes Kaufsignal zum Kauf und Verkaufsignal zum Verkauf
	 * Aber am gleichen Tag wird nicht gekauft und verkauft 
	 */
	@Override
	public Order entscheideSignal(Signal signal, Depot depot) {
		Kurs kurs = signal.getKurs();
		String wertpapier = kurs.getWertpapier();
		Order order = null;

		if (signal.getKaufVerkauf() == Order.KAUF) {
			log.debug("Signal->Kauf: " + signal.toString());
			order = depot.kaufe(depot.anfangsbestand / 3, wertpapier);
		}
		if (signal.getKaufVerkauf() == Order.VERKAUF) {
			// Ein Verkauf erfolgt nur, wenn ein Bestand dieses Wertpapiers vorhanden ist 
			if (depot.getWertpapierBestand(wertpapier) != null) {
				log.debug("Signal->Verkauf: " + signal.toString());
				order = depot.verkaufe(wertpapier);
			}
		}
		return order;
	}

}
