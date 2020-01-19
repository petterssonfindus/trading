package com.algotrading.indikator;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.component.AktieCache;
import com.algotrading.util.AbstractTest;

public class TestAccumulationDistributionLine extends AbstractTest {

	private static Aktie aktie;
	private static IndikatorAlgorithmus iA;

	@Override
	public void setUp() {
		// TODO Auto-generated method stub
		super.setUp();

		aktie = aV.getAktieLazy("testaktie");
		iA = aktie.addIndikatorAlgorithmus(new IndikatorADL());
		aktie.addIndikatorAlgorithmus(iA);
		iA.addParameter("dauer", 10);
	}

	public void testAccumulationDistributionLine() {
		aktie.rechneIndikatoren();

		List<Kurs> kurse = aktie.getKursListe();
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(-24865.18f, testKurs.getIndikatorWert(iA));

	}

}
