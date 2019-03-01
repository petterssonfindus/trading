package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;

import junit.framework.TestCase;

public class TestMinMax extends TestCase {
	
	public void testMinMax() {
		Aktie aktie = Aktien.getInstance().getAktie("testaktie");
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorMinMax());
		iA.addParameter("dauer", 10);
		aktie.rechneIndikatoren();
		
		Kurs kurs = aktie.getKurse().get(20);
		float test = kurs.getIndikatorWert(iA);
		System.out.println("test" + test);
		assertEquals(0.43922076f, test);
		
//		aktie.writeFileIndikatoren();
		
	}

}
