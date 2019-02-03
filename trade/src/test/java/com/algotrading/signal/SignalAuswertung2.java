package com.algotrading.signal;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGDalt;
import com.algotrading.indikator.IndikatorMinMax;
import com.algotrading.indikator.IndikatorMultiplikation;
import com.algotrading.indikator.IndikatorVolatilitaet;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

public class SignalAuswertung2 extends TestCase {

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
		iB3.addParameter("reziprok2", 1);
		iB3.addParameter("faktor", 100f);
		
		// Indikator berechnen und ausgeben 
		aktie.rechneIndikatoren();
		
		// Signal konfigurieren und an Aktie hängen 
		SignalAlgorithmus sB = aktie.createSignalAlgorithmus(new SignalAlgorithmus() {
			
			@Override
			public int rechne(Aktie aktie) {
				// TODO Auto-generated method stub
				return 0;
			}
			
			@Override
			public String getKurzname() {
				// TODO Auto-generated method stub
				return null;
			}
		});
		sB.addParameter("indikator", iB3);
		sB.addParameter("dauer", 30);		// Min-Max-Berechnung x Tage zurück 
		sB.addParameter("schwelle", 2.5f);		// 1-fache Standardabweichung
		sB.addParameter("durchbruch", 0);	// tägliches Signal in der Extremzone
		
		// Signale berechnen und ausgeben 
		aktie.rechneSignale();
		aktie.bewerteSignale(null, 10);
		aktie.bewerteSignale(null, 20);
		aktie.bewerteSignale(null, 30);
		aktie.bewerteSignale(null, 50);
		aktie.bewerteSignale(null, 60);
		aktie.bewerteSignale(null, 90);
		
		
		aktie.writeFileKursIndikatorSignal();
//		aktie.writeFileSignale();
	}
	
}
