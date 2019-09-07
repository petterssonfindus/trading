package com.algotrading.indikator;

import java.util.List;

import org.junit.Test;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.util.AbstractTest;

public class TestRSI2 extends AbstractTest {

	@Test
	public void testRSI2() {

		Aktie aktie = aV.getVerzeichnis().getAktieOhneKurse("testaktie");
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorRSI2());
		iA.addParameter("dauer", 10);

		aktie.rechneIndikatoren();

		List<Kurs> liste = aktie.getKursListe();
		Kurs test = liste.get(13);
		float rsi = test.getIndikatorWert(iA);
		System.out.println("Kurs13=" + test.getKurs());
		System.out.println("RSI13=" + rsi);

		//		aktie.writeFileKursIndikatorSignal();

	}

}
