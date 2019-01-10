package com.algotrading.signal;

import java.util.GregorianCalendar;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.indikator.Indikatoren;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

public class TestMinMax extends TestCase {
/*	
	public void testIndikatorMinMax() {
		// Kursreihe erzeugen appl, dax
		Aktie aktie = Aktien.getInstance().getAktie("testaktie");
		assertNotNull(aktie);
		assertTrue(aktie.getBoersenkurse().size() > 1);
		
		// Indikator konfigurieren und an Aktie hängen
		IndikatorBeschreibung iB = new IndikatorBeschreibung(Indikatoren.INDIKATOR_GLEITENDER_DURCHSCHNITT);
		iB.addParameter("dauer", 10);
		aktie.addIndikator(iB);
		
		// Indikator berechnen und ausgeben 
		aktie.rechneIndikatoren();
//		aktie.writeFileIndikatoren();
		
		// Signal konfigurieren und an Aktie hängen 
		SignalBeschreibung sB = new SignalBeschreibung(Signal.MinMax);
		sB.addParameter("indikator", iB);
		sB.addParameter("dauer", 15);		// 15 Tage zurück 
		sB.addParameter("schwelle", 1f);		// 1-fache Standardabweichung
		sB.addParameter("durchbruch", 0);	// tägliches Signal in der Extremzone
		aktie.addSignalBeschreibung(sB);
		
		// Signale berechnen und ausgeben 
		aktie.rechneSignale();
		aktie.writeFileSignale();
	}
*/
	public void testKurswertMinMax() {
		Aktie aktie = Aktien.getInstance().getAktie("testaktie");
		assertNotNull(aktie);
		assertTrue(aktie.getBoersenkurse().size() > 1);
		
		// Indikator konfigurieren und an Aktie hängen
		IndikatorBeschreibung iB = new IndikatorBeschreibung(Indikatoren.KURSWERT);
		iB.addParameter("typ", 1);  // Typ 1 = open
		aktie.addIndikator(iB);
		
		// Indikator berechnen und ausgeben 
		aktie.rechneIndikatoren();
		// am ersten Tag 18.03.2015 ist der open-Kurs 19,50 
		assertEquals(19.5f, aktie.getBoersenkurse().get(0).getIndikatorWert(iB));
		// der close-Kurs ist 20.05
		assertEquals(20.05f, aktie.getBoersenkurse().get(0).getKurs());
		
		// Signal konfigurieren und an Aktie hängen 
		SignalBeschreibung sB = aktie.createSignalBeschreibung(Signal.MinMax);
		sB.addParameter("indikator", iB);
		sB.addParameter("dauer", 15);		// Min-Max-Berechnung 15 Tage zurück 
		sB.addParameter("schwelle", 1f);		// 1-fache Standardabweichung
		sB.addParameter("durchbruch", 0);	// tägliches Signal in der Extremzone
		
		// Signale berechnen und ausgeben 
		aktie.rechneSignale();
		// Signal-Bewertung aggregieren und ausgeben 
		GregorianCalendar beginn = DateUtil.createGregorianCalendar(01, 01, 2017);
		GregorianCalendar ende = DateUtil.createGregorianCalendar(31, 12, 2017);
		Zeitraum zeitraum = new Zeitraum(beginn, ende);
		aktie.bewerteSignale(zeitraum);
		
//		aktie.writeFileIndikatoren();
		aktie.writeFileSignale();
	}
	
}
