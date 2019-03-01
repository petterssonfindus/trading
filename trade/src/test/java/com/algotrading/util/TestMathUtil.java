package com.algotrading.util;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorAbweichung;

import junit.framework.TestCase;

public class TestMathUtil extends TestCase {
	
	static Aktie aktie; 
	
	public void setUp() {
		aktie = Aktien.newInstance().getAktie("testaktie");
	}
	
	public static void testRechneStandardabweichung() {
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorAbweichung());
		iA.addParameter("typ", 1);
		aktie.rechneIndikatoren();
		System.out.println("Kurs" + aktie.getBoersenkurse().get(13).getIndikatorWert(iA));
		assertEquals(20.51f, aktie.getBoersenkurse().get(13).getIndikatorWert(iA));
		
		MathUtil.transformiereIndikator(aktie, iA);
		System.out.println("Kurs TrafoOhne" + aktie.getBoersenkurse().get(13).getIndikatorWert(iA));
		assertEquals(0.39640743f, aktie.getBoersenkurse().get(13).getIndikatorWert(iA));
		
	}
	
	public static void testRechneAbweichung() {
		Aktie aktie = Aktien.getInstance().getAktie("testaktie");
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorAbweichung());
		iA.addParameter("typ", 1);
		aktie.rechneIndikatoren();
		System.out.println("Kurs" + aktie.getBoersenkurse().get(13).getIndikatorWert(iA));
		assertEquals(20.51f, aktie.getBoersenkurse().get(13).getIndikatorWert(iA));
		
		MathUtil.transformiereIndikator(aktie, iA);
		System.out.println("KursAbweichung " + aktie.getBoersenkurse().get(13).getIndikatorWert(iA));
		assertEquals(-0.26487976f, aktie.getBoersenkurse().get(13).getIndikatorWert(iA));
		
	}

}
