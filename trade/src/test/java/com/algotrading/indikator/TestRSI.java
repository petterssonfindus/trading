package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;
import junit.framework.TestCase;

/**
 * die beiden Berechnungs-Methoden liefern genau die selben Ergebnisse 
 * @author oskar
 *
 */
public class TestRSI extends TestCase {
	
	Aktie rsiAktie; 
	IndikatorAlgorithmus rsi;
	
	public void setUp() {
		rsiAktie = Aktien.newInstance().getAktie("sardata5");
	}
	
	public void testRSI() {
		rsi = rsiAktie.addIndikatorAlgorithmus(new IndikatorRSI());
		rsi.addParameter("dauer", 10);
		
		rsiAktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = rsiAktie.getBoersenkurse();
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(0.3162709f,testKurs.getIndikatorWert(rsi));

	}

	public void testRSI2() {
		IndikatorAlgorithmus iA2 = rsiAktie.addIndikatorAlgorithmus(new IndikatorRSI2());
		iA2.addParameter("dauer", 10);
		
		rsiAktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = rsiAktie.getBoersenkurse();
		Kurs testKurs = kurse.get(13);
		assertEquals(0.3162709f,testKurs.getIndikatorWert(iA2));

		
		
	}
}
