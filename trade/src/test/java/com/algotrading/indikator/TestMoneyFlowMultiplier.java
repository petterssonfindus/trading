package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

import junit.framework.TestCase;

public class TestMoneyFlowMultiplier extends TestCase {
	
	private static Aktie aktie; 
	private static IndikatorAlgorithmus iA; 
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		aktie = AktieVerzeichnis.newInstance().getAktieOhneKurse("testaktie");
		iA = aktie.addIndikatorAlgorithmus(new IndikatorMFM());
		aktie.addIndikatorAlgorithmus(iA);
		iA.addParameter("dauer", 10);
	}
	
	public void testMoneyFlowMultiplier () {
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getKursListe();
		assertNotNull(kurse);
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(-0.43711352f,testKurs.getIndikatorWert(iA));
		
	}

}
