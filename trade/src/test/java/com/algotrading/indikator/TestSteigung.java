package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.AbstractTest;

public class TestSteigung extends AbstractTest {

	public void testPerformancePositiv() {
		Aktie aktie = aV.getAktieOhneKurse("testaktie");
		IndikatorAlgorithmus iB = aktie.addIndikatorAlgorithmus(new IndikatorSteigung());
		float kurs = aktie.getKursListe().get(50).getKurs();
		System.out.println("Performancekurs " + kurs);
		float kurs2 = aktie.getKursListe().get(40).getKurs();
		System.out.println("Performancekurs2 " + kurs2);
		float ergebnis = (kurs / kurs2) - 1;
		System.out.println("Steigung " + ergebnis);

		iB.addParameter("dauer", 10);
		iB.rechne(aktie);
		float x = aktie.getKursListe().get(50).getIndikatorWert(iB);
		System.out.println("Steigung50: " + x);
		assertEquals(ergebnis, x);
	}

}
