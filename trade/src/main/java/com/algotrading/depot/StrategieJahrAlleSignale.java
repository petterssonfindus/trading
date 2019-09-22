package com.algotrading.depot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.aktie.Kurs;
import com.algotrading.component.AktieVerwaltung;
import com.algotrading.signal.Signal;

@Service
public class StrategieJahrAlleSignale extends SignalStrategie {
	static final Logger log = LogManager.getLogger("Strategie");

	@Autowired
	AktieVerwaltung aV;

	/**
	 * Nutzt Jahrestag um aktive und passive Handelsphase zu steuern 
	 * Wenn Jahrestag-Verkauf, dann kein Handel 
	 * An der Aktie wird ein Parameter "phase" genutzt, um den Zustand abzubilden. 
	 */
	@Override
	public Order entscheideSignal(Signal signal, Depot depot) {
		Kurs kurs = signal.getKurs();
		String wertpapier = kurs.getAktieName();
		Order order = null;
		// wenn es ein Jahrestag-Signal ist 
		if (signal.getSignalAlgorithmus().getClass().getName() == "signal.Jahrestag") {

			if (signal.getKaufVerkauf() == Order.KAUF) {
				// Speichert an der Aktie über einen Parameter die Phase
				aV.getAktieLazy(signal.getKurs()).addParameter("phase", 1);
				log.debug("JahrestagSignal Kauf: " + signal.toString());
				order = depot.kaufe(depot.geld, kurs.getAktie());
			}
			// beim Verkauf wird alles verkauft 
			// und nicht mehr gehandelt, bis ein Kauf-Signal auftritt 
			if (signal.getKaufVerkauf() == Order.VERKAUF) {
				// Order wird nur dann ausgefährt, wenn ein Bestand vorhanden ist
				order = depot.verkaufeGesamtbestand();
				// Speichert an der Aktie äber einen Parameter die Phase
				aV.getAktieLazy(signal.getKurs()).addParameter("phase", 0);
				if (order != null) {
					log.debug("JahrestagSignal Verkauf: " + signal.toString() + " Order: " + order.toString());
				}
			}
		} else {	// ein GD-Durchbruch-Signal
			// ein Kauf erfolgt nur, wenn sich die Aktien nicht in der Phase 0 (Verkauf) befindet

			if (signal.getKaufVerkauf() == Order.KAUF) {
				Object object = signal.getKurs().getAktie().getParameter("phase");
				int phase = 0;
				if (object != null) {
					phase = (int) object;
				}

				if (object == null || phase == 1) {	// es wird nur gekauft, wenn sich Aktien in der Kauf-Phase befindet
					// Kauf-Betrag wird errechnet als Anteil aus der Anfangsinvestition 
					float kaufbetrag = (float) this.getParameter("kaufbetrag");
					order = depot.kaufe(depot.anfangsbestand * kaufbetrag, wertpapier);
					if (order != null) {
						log.debug("Signal->Kauf: " + signal.toString() + " Order: " + order.toString());
					}
				}
			}
			// beim Verkauf wird die Phase nicht gepräft 
			if (signal.getKaufVerkauf() == Order.VERKAUF) {
				// Verkaufs-Order wird nur erzeugt, wenn ein Bestand vorhanden ist. 
				order = depot.verkaufe(wertpapier);
				if (order != null) {
					log.debug("Signal->Verkauf: " + signal.toString() + " Order: " + order.toString());
				}
			}
		}
		return order;
	}

}
