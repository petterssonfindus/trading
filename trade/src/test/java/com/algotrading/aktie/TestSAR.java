package com.algotrading.aktie;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.indikator.Indikatoren;

import junit.framework.TestCase;

public class TestSAR extends TestCase {
	
	private static Aktie aktie; 
	private static IndikatorBeschreibung indikatorBeschreibung; 
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		aktie = Aktien.getInstance().getAktie("sardata5");
		indikatorBeschreibung = new IndikatorBeschreibung(Indikatoren.INDIKATOR_SAR);
		aktie.addIndikator(indikatorBeschreibung);
		indikatorBeschreibung.addParameter("start", 0.02f);
		indikatorBeschreibung.addParameter("stufe", 0.02f);
		indikatorBeschreibung.addParameter("maximum", 0.2f);
	}
	
	public void testSAR () {
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		Kurs testKurs;
		testKurs = kurse.get(3);
		assertEquals(46.55f,testKurs.getIndikatorWert(indikatorBeschreibung));
		assertEquals(46.27f,testKurs.high);
		assertEquals(45.92f,testKurs.low);
		testKurs = kurse.get(30);
		assertEquals(42.350117f,testKurs.getIndikatorWert(indikatorBeschreibung));
		assertEquals(44.57f,testKurs.high);
		assertEquals(44.26f,testKurs.low);
		
	}

}
