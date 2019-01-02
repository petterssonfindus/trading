package com.algotrading.signal;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.indikator.Indikatoren;

import junit.framework.TestCase;

public class TestMinMax extends TestCase {
	
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

}
