package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

import junit.framework.TestCase;

public class TestVolatilitaet extends TestCase {
	
	private static Aktie aktie; 
	private static IndikatorAlgorithmus IndikatorAlgorithmus10; 
	private static IndikatorAlgorithmus IndikatorAlgorithmus20; 
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		aktie = AktieVerzeichnis.newInstance().getAktieOhneKurse("sardata5");
		
		IndikatorAlgorithmus10 = aktie.addIndikatorAlgorithmus(new IndikatorVolatilitaet());
		aktie.addIndikatorAlgorithmus(IndikatorAlgorithmus10);
		IndikatorAlgorithmus10.addParameter("dauer", 10);
		
		IndikatorAlgorithmus20 = aktie.addIndikatorAlgorithmus(new IndikatorVolatilitaet());
		aktie.addIndikatorAlgorithmus(IndikatorAlgorithmus20);
		IndikatorAlgorithmus20.addParameter("dauer", 20);
	}
	
	public void testRechne () {
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getKursListe();
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(0.7262107f,testKurs.getIndikatorWert(IndikatorAlgorithmus10));
		testKurs = kurse.get(30);
		assertEquals(0.68043214f,testKurs.getIndikatorWert(IndikatorAlgorithmus10));
		assertEquals(0.90420943f,testKurs.getIndikatorWert(IndikatorAlgorithmus20));
		
	}

}
