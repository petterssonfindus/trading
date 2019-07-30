package com.algotrading.indikator;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Kurs;

import junit.framework.TestCase;

public class TestIndikatorRelativ extends TestCase {
	
	public static void testRSIRelativ() {
		
		Aktie aktie = AktieVerzeichnis.getInstance().getAktieOhneKurse("testaktie");
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorRSIRelativ());
		iA.addParameter("dauer", 10);
		IndikatorAlgorithmus iA2 = aktie.addIndikatorAlgorithmus(new IndikatorRSI2());
		iA2.addParameter("dauer", 10);
		
		aktie.rechneIndikatoren();
		
		List<Kurs> liste = aktie.getKursListe();
		Kurs test = liste.get(13);
		float rsi = test.getIndikatorWert(iA);
		
		assertEquals(0.36231863f, rsi);
		
//		aktie.writeFileKursIndikatorSignal();
		
	}
	

}
