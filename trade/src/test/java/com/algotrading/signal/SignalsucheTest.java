package com.algotrading.signal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;

import junit.framework.TestCase;

public class SignalsucheTest extends TestCase {
	
	private static final Logger log = LogManager.getLogger(SignalsucheTest.class);

	public void testSignalsuche () {
		
	// Kursreihe erzeugen appl, dax
	Aktie aktie = AktieVerzeichnis.getInstance().getAktie("testaktie");
	assertNotNull(aktie);
	assertTrue(aktie.getKursListe().size() > 1);
	log.info("Kursreihe hat Kurse: " + aktie.getKursListe().size());
	
	// Indikatoren berechnen
	aktie.rechneIndikatoren();
//	kursreihe.writeFileIndikatoren();
	
	// Signale berechnen 
	aktie.rechneSignale();
//	aktie.writeFileSignale();

	}

}
