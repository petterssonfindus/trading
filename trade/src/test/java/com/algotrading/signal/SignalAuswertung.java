package com.algotrading.signal;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGD;
import com.algotrading.indikator.IndikatorVolatilitaet;
import com.algotrading.indikator.Indikatoren;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

public class SignalAuswertung extends TestCase {

	public void testSignalauswertung() {
		Aktie aktie = Aktien.getInstance().getAktie("^gdaxi");
		assertNotNull(aktie);
		assertTrue(aktie.getBoersenkurse().size() > 1);
		
		// Indikator konfigurieren und an Aktie hängen
		IndikatorAlgorithmus iB = aktie.addIndikator(new IndikatorGD());
		IndikatorAlgorithmus iB2 = aktie.addIndikator(new IndikatorVolatilitaet());
		
		// Indikator berechnen und ausgeben 
		aktie.rechneIndikatoren();
		
		// Signal konfigurieren und an Aktie hängen 
		SignalBeschreibung sB = aktie.createSignalBeschreibung(Signal.MinMax);
		sB.addParameter("indikator", iB);
		sB.addParameter("dauer", 30);		// Min-Max-Berechnung x Tage zurück 
		sB.addParameter("schwelle", 1f);		// 1-fache Standardabweichung
		sB.addParameter("durchbruch", 0);	// tägliches Signal in der Extremzone
		
		SignalBeschreibung sB2 = aktie.createSignalBeschreibung(Signal.MinMax);
		sB2.addParameter("indikator", iB);
		sB2.addParameter("dauer", 30);		// Min-Max-Berechnung x Tage zurück 
		sB2.addParameter("schwelle", 1f);		// 1-fache Standardabweichung
		sB2.addParameter("durchbruch", 0);	// tägliches Signal in der Extremzone
		
//		SignalBeschreibung sB3 = aktie.createSignalBeschreibung(Signal.AND);
		
		
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
		aktie.bewerteSignale(null, 10);
		aktie.bewerteSignale(null, 20);
		aktie.bewerteSignale(null, 30);
		aktie.bewerteSignale(null, 50);
		aktie.bewerteSignale(null, 60);
		aktie.bewerteSignale(null, 90);
		
		
//		aktie.writeFileIndikatoren();
//		aktie.writeFileSignale();
	}
	
}
