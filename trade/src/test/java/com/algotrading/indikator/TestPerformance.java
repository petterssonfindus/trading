package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.util.Util;

import junit.framework.TestCase;

public class TestPerformance extends TestCase {


	public void testPerformancePositiv() {
		PerformancePA performance = new PerformancePA();
		Aktie aktie = Aktien.getInstance().getAktie("testaktie");
		float kurs = aktie.getBoersenkurse().get(50).getKurs();
		System.out.println("Performancekurs " + kurs);
		float kurs2 = aktie.getBoersenkurse().get(40).getKurs();
		System.out.println("Performancekurs2 " + kurs2);
		float ergebnis = ((kurs - kurs2) / kurs2) * 25;
		System.out.println("Performance " + ergebnis);
		
		IndikatorBeschreibung iB = new IndikatorBeschreibung(Indikatoren.PERFORMANCE);
		iB.addParameter("dauer", 10);
		performance.rechne(aktie, iB);
		float x = aktie.getKurse().get(50).getIndikatorWert(iB);
		System.out.println("Performance 50: " + x);
		assertEquals(ergebnis, x);
	}

	public void testPerformanceNegativ() {
		PerformancePA performance = new PerformancePA();
		Aktie aktie = Aktien.getInstance().getAktie("testaktie");
		float kurs = aktie.getBoersenkurse().get(50).getKurs();
		System.out.println("NPerformancekurs " + kurs);
		float kurs2 = aktie.getBoersenkurse().get(63).getKurs();
		System.out.println("NPerformancekurs2 " + kurs2);
		float ergebnis = ((kurs - kurs2) / kurs2) * 19.23f;
		System.out.println("NPerformance " + ergebnis);
		
		IndikatorBeschreibung iB = new IndikatorBeschreibung(Indikatoren.PERFORMANCE);
		iB.addParameter("dauer", -13);
		performance.rechne(aktie, iB);
		float x = aktie.getKurse().get(50).getIndikatorWert(iB);
		float x2 = Util.rundeBetrag(x);
		System.out.println("NPerformance 50: " + x);
		assertEquals(-0.51f, x2);
		
	}
}
