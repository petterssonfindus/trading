package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

import junit.framework.TestCase;

public class TestGleitenderDurchschnitt extends TestCase {
	
	private static Aktie aktie; 
	private static IndikatorAlgorithmus iA10; 
	private static IndikatorAlgorithmus iA20; 
	private static IndikatorAlgorithmus iAR;
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		aktie = Aktien.newInstance().getAktie("sardata5");
		
		iA10 = aktie.addIndikator(new IndikatorGD());
		iA10.addParameter("dauer", 10);
		
		iA20 = aktie.addIndikator(new IndikatorGD());
		iA20.addParameter("dauer", 20);
		
		iAR = aktie.addIndikator(new IndikatorGD());
		iAR.addParameter("dauer", 10);
		iAR.addParameter("berechnungsart", 2);
	}
	
	public void testRechne () {
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(45.944f,testKurs.getIndikatorWert(iA10));
		testKurs = kurse.get(30);
		assertEquals(43.461f,testKurs.getIndikatorWert(iA10));
		assertEquals(43.8395f,testKurs.getIndikatorWert(iA20));
		
		assertEquals(0.024882235f,testKurs.getIndikatorWert(iAR));
		
		
	}

}
