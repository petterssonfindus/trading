package com.algotrading.aktie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.signal.Signalverwaltung;
import com.algotrading.util.Zeitraum;

@Service
public class AktieVerwaltung {

	@Autowired
	private Signalverwaltung sV;

	/**
	 * Bewertet alle Signale, die an der Aktie hängen
	 * 
	 * @param zeitraum   der Zeitraum in dem die signale auftreten Wenn null, dann
	 *                   maximaler Zeitraum, für den Signale vorliegen.
	 * @param tageVoraus für die Erfolgsmessung in die Zukunft
	 */
	public void bewerteSignale(Aktie aktie, Zeitraum zeitraum, int tage) {
		aktie.bewerteSignale(zeitraum, tage);

	}

}
