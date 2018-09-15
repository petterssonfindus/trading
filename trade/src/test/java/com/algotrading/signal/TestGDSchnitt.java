package com.algotrading.signal;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.indikator.Indikatoren;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

public class TestGDSchnitt extends TestCase {
	
	private static final Logger log = LogManager.getLogger(SignalsucheTest.class);
	private static Aktie aktie;
	private static IndikatorBeschreibung indikator10; 
	private static IndikatorBeschreibung indikator20; 
	private static SignalBeschreibung signal; 
	GregorianCalendar beginn = new GregorianCalendar(2015,4,1);
	GregorianCalendar ende = new GregorianCalendar(2018,1,1);

	
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		aktie = Aktien.getInstance().getAktie("testaktie");
		assertNotNull(aktie);
		assertTrue(aktie.getBoersenkurse().size() > 1);
		log.info("Kursreihe hat Kurse: " + aktie.getBoersenkurse().size());
		
		indikator10 = new IndikatorBeschreibung(Indikatoren.INDIKATOR_GLEITENDER_DURCHSCHNITT);
		aktie.addIndikator(indikator10);
		indikator10.addParameter("dauer", 10);
		
		indikator20 = new IndikatorBeschreibung(Indikatoren.INDIKATOR_GLEITENDER_DURCHSCHNITT);
		aktie.addIndikator(indikator20);
		indikator20.addParameter("dauer", 20);
		
		// Indikatoren berechnen
		aktie.rechneIndikatoren();
		
		signal = new SignalBeschreibung(Signal.GDDurchbruch);
		signal.addParameter("zeitraum", new Zeitraum(beginn, ende));
		aktie.addSignalBeschreibung(signal);
		aktie.rechneSignale();
	}


	public void testGDSchnitt () {
		// Signale berechnen 
		ArrayList<Signal> signale = aktie.getSignale();
		assertNotNull(signale);
		assertTrue(signale.size() > 0);

//		aktie.writeFileSignale();
	
	}

}
