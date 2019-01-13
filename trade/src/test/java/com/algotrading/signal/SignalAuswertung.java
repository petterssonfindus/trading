package com.algotrading.signal;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.indikator.Indikatoren;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

public class SignalAuswertung extends TestCase {

	public void testSignalauswertung() {
		Aktie aktie = Aktien.getInstance().getAktie("^gdaxi");
		assertNotNull(aktie);
		assertTrue(aktie.getBoersenkurse().size() > 1);
		
		// Indikator konfigurieren und an Aktie hängen
		IndikatorBeschreibung iB = new IndikatorBeschreibung(Indikatoren.INDIKATOR_GLEITENDER_DURCHSCHNITT);
		iB.addParameter("dauer", 30);  // Typ 1 = open
		aktie.addIndikator(iB);
		
		// Indikator berechnen und ausgeben 
		aktie.rechneIndikatoren();
		
		// Signal konfigurieren und an Aktie hängen 
		SignalBeschreibung sB = aktie.createSignalBeschreibung(Signal.MinMax);
		sB.addParameter("indikator", iB);
		sB.addParameter("dauer", 120);		// Min-Max-Berechnung x Tage zurück 
		sB.addParameter("schwelle", 1f);		// 1-fache Standardabweichung
		sB.addParameter("durchbruch", 0);	// tägliches Signal in der Extremzone
		
//		System.out.println("Signalbeschreibng:" + sB.toString());
		
		// Signale berechnen und ausgeben 
		aktie.rechneSignale();
		// Signal-Bewertung aggregieren und ausgeben 
/*		
		List<Zeitraum> liste = DateUtil.getJahresZeitraeume(1990, 2018, 1);
		for (Zeitraum zeitraum : liste) {
			aktie.bewerteSignale(zeitraum, 10);
		}
 */
		aktie.bewerteSignale(new Zeitraum(1990, 2018), 10);
		aktie.bewerteSignale(new Zeitraum(1990, 2018), 20);
		aktie.bewerteSignale(new Zeitraum(1990, 2018), 30);
		aktie.bewerteSignale(new Zeitraum(1990, 2018), 40);
		aktie.bewerteSignale(new Zeitraum(1990, 2018), 50);
		aktie.bewerteSignale(new Zeitraum(1990, 2018), 60);
		aktie.bewerteSignale(new Zeitraum(1990, 2018), 90);
		
		
//		aktie.writeFileIndikatoren();
//		aktie.writeFileSignale();
	}
	
}
