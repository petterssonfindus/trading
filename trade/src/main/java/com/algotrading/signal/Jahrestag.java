package com.algotrading.signal;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.util.DateUtil;
import com.algotrading.util.Zeitraum;
import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
/**
 * Das Signal "Jahrestag" wird genau ein Mal pro Jahr ausgeläst. 
 * An jedem Tag wird gepräft, ob es eingetreten ist. 
 * Der parameter "jahreszahl" merkt sich, ob das Signal im aktuellen jahr bereits eingetreten ist.
 * @author oskar
 *
 */
public class Jahrestag implements SignalAlgorithmus {
	static final Logger log = LogManager.getLogger(Jahrestag.class);
	// merkt sich das Jahr der letzten Signalerzeugung
	private static int jahreszahl = 0;
	
	/**
	 * erzeugt ein Signal, wenn der Jahrestag eintritt 
	 * Genau ein Signal pro Jahr
	 * @param kursreihe
	 */
	public int ermittleSignal(Aktie aktie, SignalBeschreibung sB) {
		if (aktie == null) log.error("Inputparameter Aktie ist null");
		if (sB == null) log.error("Inputparameter Signalbeschreibung ist null");
		int anzahl = 0;
		jahreszahl = 0;
		int tage = (Integer) sB.getParameter("tage");
		int kaufverkauf = (Integer) sB.getParameter("kaufverkauf");
		Zeitraum zeitraum = (Zeitraum) sB.getParameter("zeitraum");
		
		for (Kurs kurs : aktie.getKurse(zeitraum)) {
			if (Jahrestag.pruefeJahrestag(sB, kurs, tage, kaufverkauf)) anzahl++;
		}
		return anzahl; 
	}

	/**
	 * erzeugt Jahrestag-Signal und hängt es an den Kurs an
	 */
	private static boolean pruefeJahrestag (SignalBeschreibung sB, Kurs tageskurs, int jahrestag, int kaufverkauf) {
		if (tageskurs == null ) log.error("Inputvariable ist null"); 
		boolean result = false; 
		GregorianCalendar datum = tageskurs.datum;
		int dayofyear = datum.get(Calendar.DAY_OF_YEAR);
		int year = datum.get(Calendar.YEAR);
		if (dayofyear > jahrestag && year > jahreszahl) {
			log.debug("Jahrestag eingetreten: " + tageskurs.wertpapier + " " + 
					jahrestag + " " + kaufverkauf + " " + DateUtil.formatDate(datum));
			Signal.create(sB, tageskurs, (byte) kaufverkauf, 0);
			jahreszahl = datum.get(Calendar.YEAR);
			result = true; 
		}

		return result; 
	}
}
