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

public class TestGDDurchbruch extends TestCase {
	
	private static final Logger log = LogManager.getLogger(SignalsucheTest.class);
	private static Aktie aktie;
	private static IndikatorBeschreibung indikator10001; 
	private static IndikatorBeschreibung indikator10002; 
	private static IndikatorBeschreibung indikator10003; 
	private static SignalBeschreibung signal; 
	private static SignalBeschreibung signal2; 
	private static SignalBeschreibung signal3; 
	GregorianCalendar beginn = new GregorianCalendar(2015,4,1);
	GregorianCalendar ende = new GregorianCalendar(2018,1,1);

	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		aktie = Aktien.newInstance().getAktie("testaktie");
		assertNotNull(aktie);
		assertTrue(aktie.getBoersenkurse().size() > 1);
		
		indikator10001 = new IndikatorBeschreibung(Indikatoren.INDIKATOR_GLEITENDER_DURCHSCHNITT);
		aktie.addIndikator(indikator10001);
		indikator10001.addParameter("dauer", 10);
		indikator10002 = new IndikatorBeschreibung(Indikatoren.INDIKATOR_GLEITENDER_DURCHSCHNITT);
		aktie.addIndikator(indikator10002);
		indikator10002.addParameter("dauer", 10);
		indikator10003 = new IndikatorBeschreibung(Indikatoren.INDIKATOR_GLEITENDER_DURCHSCHNITT);
		aktie.addIndikator(indikator10003);
		indikator10003.addParameter("dauer", 10);
		
		// Indikatoren berechnen
		aktie.rechneIndikatoren();
		
		signal = new SignalBeschreibung(Signal.GDDurchbruch);
		signal.addParameter("indikator", indikator10001);
		signal.addParameter("zeitraum", new Zeitraum(beginn, ende));
		signal.addParameter("schwelle", 0.01f);
		aktie.addSignalBeschreibung(signal);

		signal2 = new SignalBeschreibung(Signal.GDDurchbruch);
		signal2.addParameter("indikator", indikator10001);
		signal2.addParameter("zeitraum", new Zeitraum(beginn, ende));
		signal2.addParameter("schwelle", 0.02f);
		aktie.addSignalBeschreibung(signal2);

		signal3 = new SignalBeschreibung(Signal.GDDurchbruch);
		signal3.addParameter("indikator", indikator10001);
		signal3.addParameter("zeitraum", new Zeitraum(beginn, ende));
		signal3.addParameter("schwelle", 0.03f);
		aktie.addSignalBeschreibung(signal3);

		// Signale berechnen 
		aktie.rechneSignale();
	}


	public void testGDSchnitt () {
		ArrayList<Signal> signale = aktie.getSignale(signal);
		ArrayList<Signal> signale2 = aktie.getSignale(signal2);
		ArrayList<Signal> signale3 = aktie.getSignale(signal3);

		assertTrue(signale.size() == 96);
		assertTrue(signale2.size() == 58);
		assertTrue(signale3.size() == 26);
	
	}

}
