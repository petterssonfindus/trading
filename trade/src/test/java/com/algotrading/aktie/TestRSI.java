package com.algotrading.aktie;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.indikator.Indikatoren;
import junit.framework.TestCase;

public class TestRSI extends TestCase {
	
	Aktie rsiAktie; 
	IndikatorBeschreibung rsi;
	
	public void setUp() {
		rsiAktie = Aktien.getInstance().getAktie("sardata5");
		rsi = new IndikatorBeschreibung(Indikatoren.INDIKATOR_RSI);
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
