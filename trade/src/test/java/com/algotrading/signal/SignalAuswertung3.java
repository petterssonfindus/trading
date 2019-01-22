package com.algotrading.signal;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorRSI;
import com.algotrading.indikator.IndikatorRSI2;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

public class SignalAuswertung3 extends TestCase {
	
	public static void testSignalAuswertung () {
		Aktie aktie = Aktien.getInstance().getAktie("^gdaxi");
		IndikatorAlgorithmus iA = aktie.addIndikator(new IndikatorRSI2());
		iA.addParameter("dauer", 30);
		aktie.rechneIndikatoren();
		
		SignalBeschreibung sB = aktie.createSignalBeschreibung(Signal.MinMax);
		sB.addParameter("dauer", 30);
		sB.addParameter("indikator" , iA);
		sB.addParameter("schwelle" , 2f);
		aktie.rechneSignale();
		
		aktie.bewerteSignale(new Zeitraum(2010, 2011), 30);
		aktie.bewerteSignale(new Zeitraum(2012, 2013), 30);
		aktie.bewerteSignale(new Zeitraum(2014, 2015), 30);
		aktie.bewerteSignale(new Zeitraum(2016, 2017), 30);
		aktie.bewerteSignale(new Zeitraum(2000, 2017), 30);
		
//		aktie.writeFileKursIndikatorSignal();
	}

}
