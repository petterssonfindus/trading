package com.algotrading.signal;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGD;
import com.algotrading.indikator.IndikatorMinMax;
import com.algotrading.indikator.IndikatorMultiplikation;
import com.algotrading.indikator.IndikatorVolatilitaet;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

public class SignalAuswertung extends TestCase {

	public void testSignalauswertung() {
		Aktie aktie = Aktien.getInstance().getAktie("^gdaxi");
		
		// Indikator konfigurieren und an Aktie hängen
		IndikatorAlgorithmus iB = aktie.createIndikatorAlgorithmus(new IndikatorMinMax());
		iB.addParameter("dauer", 30);
		IndikatorAlgorithmus iB2 = aktie.createIndikatorAlgorithmus(new IndikatorVolatilitaet());
		iB2.addParameter("dauer", 30);
		IndikatorAlgorithmus iB3 = aktie.createIndikatorAlgorithmus(new IndikatorMultiplikation());
		iB3.addParameter("indikator1", iB);
		iB3.addParameter("indikator2", iB2);
		
		// Indikator berechnen und ausgeben 
		aktie.rechneIndikatoren();
		
		// Signal konfigurieren und an Aktie hängen 
		SignalAlgorithmus sA = aktie.createSignalAlgorithmus(new SignalMinMax());
		sA.addParameter("indikator", iB);
		sA.addParameter("dauer", 30);		// Min-Max-Berechnung x Tage zurück 
		sA.addParameter("schwelle", 1f);		// 1-fache Standardabweichung
		sA.addParameter("durchbruch", 0);	// tägliches Signal in der Extremzone
		
		SignalAlgorithmus sA2 = aktie.createSignalAlgorithmus(new SignalMinMax());
		sA2.addParameter("indikator", iB);
		sA2.addParameter("dauer", 30);		// Min-Max-Berechnung x Tage zurück 
		sA2.addParameter("schwelle", 1f);		// 1-fache Standardabweichung
		sA2.addParameter("durchbruch", 0);	// tägliches Signal in der Extremzone
		
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
