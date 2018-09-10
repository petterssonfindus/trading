package com.algotrading.signal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;

import junit.framework.TestCase;

public class SignalsucheTest extends TestCase {
	
	private static final Logger log = LogManager.getLogger(SignalsucheTest.class);

	public void testSignalsuche () {
		
	// Kursreihe erzeugen appl, dax
	Aktie aktie = Aktien.getInstance().getAktie("dax");
	assertNotNull(aktie);
	assertTrue(aktie.getBoersenkurse().size() > 1);
	log.info("Kursreihe hat Kurse: " + aktie.getBoersenkurse().size());
	
	// Indikatoren berechnen
	aktie.rechneIndikatoren();
//	kursreihe.writeFileIndikatoren();
	
	// Signale berechnen 
	Signalsuche.rechneSignale(aktie);
	aktie.writeFileSignale();

	}

}
