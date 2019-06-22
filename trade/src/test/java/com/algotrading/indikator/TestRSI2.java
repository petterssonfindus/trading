package com.algotrading.indikator;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Kurs;

import junit.framework.TestCase;

public class TestRSI2 extends TestCase {

	public static void testRSI2() { 
		
		Aktie aktie = AktieVerzeichnis.getInstance().getAktie("testaktie");
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
