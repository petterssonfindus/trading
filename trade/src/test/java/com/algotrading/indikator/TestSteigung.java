package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;

import junit.framework.TestCase;

public class TestSteigung extends TestCase {
	
	public void testPerformancePositiv() {
		Steigung steigung = new Steigung();
		Aktie aktie = Aktien.getInstance().getAktie("testaktie");
		float kurs = aktie.getBoersenkurse().get(50).getKurs();
		System.out.println("Performancekurs " + kurs);
		float kurs2 = aktie.getBoersenkurse().get(40).getKurs();
		System.out.println("Performancekurs2 " + kurs2);
		float ergebnis = (kurs / kurs2) -1;
		System.out.println("Steigung " + ergebnis);
		
		IndikatorBeschreibung iB = new IndikatorBeschreibung(Indikatoren.STEIGUNG);
		iB.addParameter("dauer", 10);
		steigung.rechne(aktie, iB);
		float x = aktie.getKurse().get(50).getIndikatorWert(iB);
		System.out.println("Steigung50: " + x);
		assertEquals(ergebnis, x);
	}

}
