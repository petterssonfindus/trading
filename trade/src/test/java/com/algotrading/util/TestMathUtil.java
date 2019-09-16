package com.algotrading.util;

import org.junit.Test;

import com.algotrading.aktie.Aktie;
import com.algotrading.indikator.IndikatorAbweichung;
import com.algotrading.indikator.IndikatorAlgorithmus;

public class TestMathUtil extends AbstractTest {

	static Aktie aktie;

	public void setUp() {
		aktie = aV.getAktieLazy("testaktie");
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

	@Test
	public void testRechneAbweichung() {
		Aktie aktie = aV.getAktieLazy("dax");
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
