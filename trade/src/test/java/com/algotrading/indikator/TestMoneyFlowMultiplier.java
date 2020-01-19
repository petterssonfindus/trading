package com.algotrading.indikator;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.component.AktieCache;
import com.algotrading.util.AbstractTest;

public class TestMoneyFlowMultiplier extends AbstractTest {

	private static Aktie aktie;
	private static IndikatorAlgorithmus iA;

	@Override
	public void setUp() {
		super.setUp();

		aktie = aV.getAktieLazy("testaktie");
		iA = aktie.addIndikatorAlgorithmus(new IndikatorMFM());
		aktie.addIndikatorAlgorithmus(iA);
		iA.addParameter("dauer", 10);
	}

	public void testMoneyFlowMultiplier() {
		aktie.rechneIndikatoren();

		List<Kurs> kurse = aktie.getKursListe();
		assertNotNull(kurse);
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(-0.43711352f, testKurs.getIndikatorWert(iA));

	}

}
