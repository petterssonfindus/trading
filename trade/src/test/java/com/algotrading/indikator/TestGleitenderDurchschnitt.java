package com.algotrading.indikator;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.component.AktieVerzeichnis;
import com.algotrading.util.AbstractTest;

public class TestGleitenderDurchschnitt extends AbstractTest {

	private static Aktie aktie;
	private static IndikatorAlgorithmus iA10;
	private static IndikatorAlgorithmus iA20;
	private static IndikatorAlgorithmus iAstabw;
	private static IndikatorAlgorithmus iArel;
	private static IndikatorAlgorithmus iAfaktor;
	private static IndikatorAlgorithmus iAlog;

	@Override
	public void setUp() {
		// TODO Auto-generated method stub
		super.setUp();

		aktie = aV.getAktieLazy("sardata5");

		iA10 = aktie.addIndikatorAlgorithmus(new IndikatorGD());
		iA10.addParameter("dauer", 10);

		iA20 = aktie.addIndikatorAlgorithmus(new IndikatorGD());
		iA20.addParameter("dauer", 20);

		iAstabw = aktie.addIndikatorAlgorithmus(new IndikatorGD());
		iAstabw.addParameter("dauer", 10);
		iAstabw.addParameter("stabw", 10);

		iArel = aktie.addIndikatorAlgorithmus(new IndikatorGD());
		iArel.addParameter("dauer", 10);
		iArel.addParameter("stabw", 10);
		iArel.addParameter("relativ", 1);

		iAfaktor = aktie.addIndikatorAlgorithmus(new IndikatorGD());
		iAfaktor.addParameter("dauer", 10);
		iAfaktor.addParameter("stabw", 10);
		iAfaktor.addParameter("faktor", 1);

		iAlog = aktie.addIndikatorAlgorithmus(new IndikatorGD());
		iAlog.addParameter("dauer", 10);
		iAlog.addParameter("stabw", 10);
		iAlog.addParameter("log", 1);
	}

	public void testRechne() {
		aktie.rechneIndikatoren();
		List<Kurs> kurse = aktie.getKursListe();

		Kurs kurs23 = aktie.getKursListe().get(23);
		float gd10 = kurs23.getIndikatorWert(iA10);
		System.out.println("Kurs23 GD10: " + gd10);
		float stabw = kurs23.getIndikatorWert(iAstabw);
		System.out.println("Kurs23 stabw: " + stabw);
		float relativ = kurs23.getIndikatorWert(iArel);
		System.out.println("Kurs23 rel: " + relativ);
		float faktor = kurs23.getIndikatorWert(iAfaktor);
		System.out.println("Kurs23 faktor: " + faktor);
		float log = kurs23.getIndikatorWert(iAlog);
		System.out.println("Kurs23 log: " + log);

		Kurs testKurs = kurse.get(13);
		assertEquals("45.9", testKurs.getIndikatorWert(iA10).toString().substring(0, 4));
		testKurs = kurse.get(30);
		assertEquals("43.4", testKurs.getIndikatorWert(iA10).toString().substring(0, 4));
		assertEquals("43.8", testKurs.getIndikatorWert(iA20).toString().substring(0, 4));

		aktie.writeFileKursIndikatorSignal();

	}

}
