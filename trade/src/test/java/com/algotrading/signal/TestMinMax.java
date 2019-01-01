package com.algotrading.signal;

import java.util.ArrayList;

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
		
		// Indikator konfigurieren
		IndikatorBeschreibung iB = new IndikatorBeschreibung(Indikatoren.INDIKATOR_GLEITENDER_DURCHSCHNITT);
		iB.addParameter("dauer", 10);
		ArrayList<IndikatorBeschreibung> indikatoren = new ArrayList<IndikatorBeschreibung>();
		indikatoren.add(iB);
		
		// Indikator berechnen
		aktie.rechneIndikatoren();
		
		// Signal konfigurieren
		SignalBeschreibung sB = new SignalBeschreibung(Signal.MinMax);
		sB.addParameter("dauer", 15);		// 15 Tage zurück 
		sB.addParameter("schwelle", 1);		// 1-fache Standardabweichung
		sB.addParameter("durchbruch", 0);	// tägliches Signal in der Extremzone
		
		// Signale berechnen
		aktie.rechneSignale();
		aktie.writeFileSignale();
		
		
	}

}
