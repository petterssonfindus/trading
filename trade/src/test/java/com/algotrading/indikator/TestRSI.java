package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;
import junit.framework.TestCase;

public class TestRSI extends TestCase {
	
	Aktie rsiAktie; 
	IndikatorAlgorithmus rsi;
	
	public void setUp() {
		rsiAktie = Aktien.newInstance().getAktie("sardata5");
		rsi = rsiAktie.addIndikator(new IndikatorRSI());
		rsi.addParameter("tage", 10);
		rsiAktie.addIndikator(rsi);
	}
	
	public void testRSI() {
		
		rsiAktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = rsiAktie.getBoersenkurse();
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(0.3162709f,testKurs.getIndikatorWert(rsi));

	}

}
