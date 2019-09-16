package com.algotrading.indikator;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.component.AktieVerzeichnis;
import com.algotrading.util.AbstractTest;

public class TestVolatilitaet extends AbstractTest {

	private static Aktie aktie;
	private static IndikatorAlgorithmus IndikatorAlgorithmus10;
	private static IndikatorAlgorithmus IndikatorAlgorithmus20;

	@Override
	public void setUp() {
		// TODO Auto-generated method stub
		super.setUp();

		aktie = aV.getAktieLazy("sardata5");

		IndikatorAlgorithmus10 = aktie.addIndikatorAlgorithmus(new IndikatorVolatilitaet());
		aktie.addIndikatorAlgorithmus(IndikatorAlgorithmus10);
		IndikatorAlgorithmus10.addParameter("dauer", 10);

		IndikatorAlgorithmus20 = aktie.addIndikatorAlgorithmus(new IndikatorVolatilitaet());
		aktie.addIndikatorAlgorithmus(IndikatorAlgorithmus20);
		IndikatorAlgorithmus20.addParameter("dauer", 20);
	}

	public void testRechne() {
		aktie.rechneIndikatoren();

		List<Kurs> kurse = aktie.getKursListe();
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(0.7262107f, testKurs.getIndikatorWert(IndikatorAlgorithmus10));
		testKurs = kurse.get(30);
		assertEquals(0.68043214f, testKurs.getIndikatorWert(IndikatorAlgorithmus10));
		assertEquals(0.90420943f, testKurs.getIndikatorWert(IndikatorAlgorithmus20));

	}

}
