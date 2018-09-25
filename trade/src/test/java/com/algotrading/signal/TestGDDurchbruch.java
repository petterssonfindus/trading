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
	private static IndikatorBeschreibung indikator10; 
	private static IndikatorBeschreibung indikator30; 
	private static SignalBeschreibung signal; 
	private static SignalBeschreibung signal2; 
	private static SignalBeschreibung signal3; 
	private static SignalBeschreibung signal4; 
	GregorianCalendar beginn = new GregorianCalendar(2015,4,1);
	GregorianCalendar ende = new GregorianCalendar(2018,1,1);

	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		aktie = Aktien.newInstance().getAktie("testaktie");
		assertNotNull(aktie);
		assertTrue(aktie.getBoersenkurse().size() > 1);
		
		// Indikator1 konfigurieren und an aktie hängen
		indikator10 = new IndikatorBeschreibung(Indikatoren.INDIKATOR_GLEITENDER_DURCHSCHNITT);
		aktie.addIndikator(indikator10);
		indikator10.addParameter("dauer", 10);

		// Indikator3 konfigurieren
		indikator30 = new IndikatorBeschreibung(Indikatoren.INDIKATOR_GLEITENDER_DURCHSCHNITT);
		aktie.addIndikator(indikator30);
		indikator30.addParameter("dauer", 30);
		
		// Indikatoren berechnen
		aktie.rechneIndikatoren();
		
		// Signal1 Schwelle 0.01
		signal = new SignalBeschreibung(Signal.GDDurchbruch);
		signal.addParameter("indikator", indikator10);
		signal.addParameter("zeitraum", new Zeitraum(beginn, ende));
		signal.addParameter("schwelle", 0.01f);
		aktie.addSignalBeschreibung(signal);
		// Signal2 Schwelle 0.02
		signal2 = new SignalBeschreibung(Signal.GDDurchbruch);
		signal2.addParameter("indikator", indikator10);
		signal2.addParameter("zeitraum", new Zeitraum(beginn, ende));
		signal2.addParameter("schwelle", 0.02f);
		aktie.addSignalBeschreibung(signal2);
		// Signal3 Schwelle 0.03
		signal3 = new SignalBeschreibung(Signal.GDDurchbruch);
		signal3.addParameter("indikator", indikator10);
		signal3.addParameter("zeitraum", new Zeitraum(beginn, ende));
		signal3.addParameter("schwelle", 0.03f);
		aktie.addSignalBeschreibung(signal3);
		// Signal4 Dauer 30 Tage
		signal4 = new SignalBeschreibung(Signal.GDDurchbruch);
		signal4.addParameter("indikator", indikator30);
		signal4.addParameter("zeitraum", new Zeitraum(beginn, ende));
		signal4.addParameter("schwelle", 0.01f);
		aktie.addSignalBeschreibung(signal4);

		// Signale berechnen 
		aktie.rechneSignale();
	}


	public void testGDSchnittSchwelle () {
		ArrayList<Signal> signale = aktie.getSignale(signal);
		ArrayList<Signal> signale2 = aktie.getSignale(signal2);
		ArrayList<Signal> signale3 = aktie.getSignale(signal3);

		assertTrue(signale.size() == 96);
		assertTrue(signale2.size() == 58);
		assertTrue(signale3.size() == 26);
	}
	
	public void testGDSchnitt30Tage () {
		ArrayList<Signal> signale4 = aktie.getSignale(signal4);
		assertTrue(signale4.size() == 60);
		
	}

}
