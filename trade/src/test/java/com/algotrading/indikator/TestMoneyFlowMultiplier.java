package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

import junit.framework.TestCase;

public class TestMoneyFlowMultiplier extends TestCase {
	
	private static Aktie aktie; 
	private static IndikatorAlgorithmus iA; 
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		aktie = Aktien.newInstance().getAktie("testaktie");
		iA = aktie.addIndikator(new IndikatorMFM());
		aktie.addIndikator(iA);
		iA.addParameter("dauer", 10);
	}
	
	public void testMoneyFlowMultiplier () {
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		assertNotNull(kurse);
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(-0.43711352f,testKurs.getIndikatorWert(iA));
		
	}

}
