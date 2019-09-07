package com.algotrading.signal;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Zeitraum;

/**
 * Das Signal "Jahrestag" wird genau ein Mal pro Jahr ausgeläst. 
 * An jedem Tag wird gepräft, ob es eingetreten ist. 
 * Der parameter "jahreszahl" merkt sich, ob das Signal im aktuellen jahr bereits eingetreten ist.
 * @author oskar
 *
 */
public class Jahrestag extends SignalAlgorithmus {
	static final Logger log = LogManager.getLogger(Jahrestag.class);
	// merkt sich das Jahr der letzten Signalerzeugung
	private static int jahreszahl = 0;

	/**
	 * erzeugt ein Signal, wenn der Jahrestag eintritt 
	 * Genau ein Signal pro Jahr
	 * @param kursreihe
	 */
	public int rechne(Aktie aktie) {
		if (aktie == null)
			log.error("Inputparameter Aktie ist null");
		int anzahl = 0;
		jahreszahl = 0;
		int tage = (Integer) getParameter("tage");
		int kaufverkauf = (Integer) getParameter("kaufverkauf");
		Zeitraum zeitraum = (Zeitraum) getParameter("zeitraum");

		for (Kurs kurs : aktie.getKurse(zeitraum)) {
			if (pruefeJahrestag(kurs, tage, kaufverkauf))
				anzahl++;
		}
		return anzahl;
	}

	/**
	 * erzeugt Jahrestag-Signal und hängt es an den Kurs an
	 */
	private boolean pruefeJahrestag(Kurs tageskurs, int jahrestag, int kaufverkauf) {
		if (tageskurs == null)
			log.error("Inputvariable ist null");
		boolean result = false;
		GregorianCalendar datum = tageskurs.datum;
		int dayofyear = datum.get(Calendar.DAY_OF_YEAR);
		int year = datum.get(Calendar.YEAR);
		if (dayofyear > jahrestag && year > jahreszahl) {
			log.debug(
					"Jahrestag eingetreten: " + tageskurs
							.getWertpapier() + " " + jahrestag + " " + kaufverkauf + " " + DateUtil.formatDate(datum));
			tageskurs.createSignal(this, (byte) kaufverkauf, 0);
			jahreszahl = datum.get(Calendar.YEAR);
			result = true;
		}

		return result;
	}

	@Override
	public String getKurzname() {
		return "Jahrestag";
	}
}
