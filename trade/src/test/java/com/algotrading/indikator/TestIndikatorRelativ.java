package com.algotrading.indikator;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;

import junit.framework.TestCase;

public class TestIndikatorRelativ extends TestCase {
	
	public static void testRSIRelativ() {
		
		Aktie aktie = Aktien.getInstance().getAktie("testaktie");
		IndikatorAlgorithmus iA = aktie.createIndikatorAlgorithmus(new IndikatorRSIRelativ());
		iA.addParameter("dauer", 10);
		IndikatorAlgorithmus iA2 = aktie.createIndikatorAlgorithmus(new IndikatorRSI2());
		iA2.addParameter("dauer", 10);
		
		aktie.rechneIndikatoren();
		
		List<Kurs> liste = aktie.getBoersenkurse();
		Kurs test = liste.get(13);
		float rsi = test.getIndikatorWert(iA);
		
		assertEquals(0.36231863f, rsi);
		
//		aktie.writeFileKursIndikatorSignal();
		
	}
	

}
