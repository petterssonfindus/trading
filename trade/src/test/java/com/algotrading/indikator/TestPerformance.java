package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.AbstractTest;
import com.algotrading.util.Util;

public class TestPerformance extends AbstractTest {

	public void testPerformancePositiv() {
		Aktie aktie = aV.getVerzeichnis().getAktieOhneKurse("testaktie");
		IndikatorAlgorithmus performance = aktie.addIndikatorAlgorithmus(new IndikatorPerformance());
		float kurs = aktie.getKursListe().get(50).getKurs();
		System.out.println("Performancekurs " + kurs);
		float kurs2 = aktie.getKursListe().get(40).getKurs();
		System.out.println("Performancekurs2 " + kurs2);
		float ergebnis = ((kurs - kurs2) / kurs2) * 25;
		System.out.println("Performance " + ergebnis);

		performance.addParameter("dauer", 10);
		performance.rechne(aktie);
		float x = aktie.getKurse().get(50).getIndikatorWert(performance);
		System.out.println("Performance 50: " + x);
		assertEquals(ergebnis, x);
	}

	public void testPerformanceNegativ() {
		Aktie aktie = aV.getVerzeichnis().getAktieOhneKurse("testaktie");
		IndikatorAlgorithmus performance = aktie.addIndikatorAlgorithmus(new IndikatorPerformance());
		float kurs = aktie.getKursListe().get(50).getKurs();
		System.out.println("NPerformancekurs " + kurs);
		float kurs2 = aktie.getKursListe().get(63).getKurs();
		System.out.println("NPerformancekurs2 " + kurs2);
		float ergebnis = ((kurs - kurs2) / kurs2) * 19.23f;
		System.out.println("NPerformance " + ergebnis);

		performance.addParameter("dauer", -13);
		performance.rechne(aktie);
		float x = aktie.getKurse().get(50).getIndikatorWert(performance);
		float x2 = Util.rundeBetrag(x);
		System.out.println("NPerformance 50: " + x);
		assertEquals(-0.51f, x2);

	}

}
