package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

import junit.framework.TestCase;

public class TestAccumulationDistributionLine extends TestCase {
	
	private static Aktie aktie; 
	private static IndikatorAlgorithmus iA; 
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		aktie = AktieVerzeichnis.newInstance().getAktieOhneKurse("testaktie");
		iA = aktie.addIndikatorAlgorithmus(new IndikatorADL());
		aktie.addIndikatorAlgorithmus(iA);
		iA.addParameter("dauer", 10);
	}
	
	public void testAccumulationDistributionLine () {
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getKursListe();
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(-24865.18f,testKurs.getIndikatorWert(iA));
		
	}

}
