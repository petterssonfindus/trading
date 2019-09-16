package com.algotrading.indikator;

import java.util.List;

import org.junit.Test;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.util.AbstractTest;

public class TestIndikatorRelativ extends AbstractTest {

	@Test
	public void testRSIRelativ() {

		Aktie aktie = aV.getAktieOhneKurse("testaktie");
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
