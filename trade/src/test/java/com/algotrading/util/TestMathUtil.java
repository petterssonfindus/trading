package com.algotrading.util;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorAbweichung;

import junit.framework.TestCase;

public class TestMathUtil extends TestCase {
	
	static Aktie aktie; 
	
	public void setUp() {
		aktie = AktieVerzeichnis.newInstance().getAktieOhneKurse("testaktie");
	}
	
	public static void testRechneStandardabweichung() {
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorAbweichung());
		iA.addParameter("typ", 1);
		aktie.rechneIndikatoren();
		System.out.println("Kurs" + aktie.getKursListe().get(13).getIndikatorWert(iA));
		assertEquals(20.51f, aktie.getKursListe().get(13).getIndikatorWert(iA));
		
		MathUtil.transformiereIndikator(aktie, iA);
		System.out.println("Kurs TrafoOhne" + aktie.getKursListe().get(13).getIndikatorWert(iA));
		assertEquals(0.39640743f, aktie.getKursListe().get(13).getIndikatorWert(iA));
		
	}
	
	public static void testRechneAbweichung() {
		Aktie aktie = AktieVerzeichnis.getInstance().getAktieOhneKurse("testaktie");
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorAbweichung());
		iA.addParameter("typ", 1);
		aktie.rechneIndikatoren();
		System.out.println("Kurs" + aktie.getKursListe().get(13).getIndikatorWert(iA));
		assertEquals(20.51f, aktie.getKursListe().get(13).getIndikatorWert(iA));
		
		MathUtil.transformiereIndikator(aktie, iA);
		System.out.println("KursAbweichung " + aktie.getKursListe().get(13).getIndikatorWert(iA));
		assertEquals(-0.26487976f, aktie.getKursListe().get(13).getIndikatorWert(iA));
		
	}

}
