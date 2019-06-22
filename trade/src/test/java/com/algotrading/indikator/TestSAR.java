package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

import junit.framework.TestCase;

public class TestSAR extends TestCase {
	
	private static Aktie aktie; 
	private static IndikatorAlgorithmus iA; 
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		aktie = AktieVerzeichnis.newInstance().getAktie("sardata5");
		iA = aktie.addIndikatorAlgorithmus(new IndikatorStatisticSAR());
		iA.addParameter("start", 0.02f);
		iA.addParameter("stufe", 0.02f);
		iA.addParameter("maximum", 0.2f);
	}
	
	public void testSAR () {
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getKursListe();
		Kurs testKurs;
		testKurs = kurse.get(3);
		assertEquals(46.55f,testKurs.getIndikatorWert(iA));
		assertEquals(46.27f,testKurs.high);
		assertEquals(45.92f,testKurs.low);
		testKurs = kurse.get(30);
		assertEquals(42.350117f,testKurs.getIndikatorWert(iA));
		assertEquals(44.57f,testKurs.high);
		assertEquals(44.26f,testKurs.low);
		
	}

}
