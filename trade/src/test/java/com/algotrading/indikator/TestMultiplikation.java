package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;

import junit.framework.TestCase;

public class TestMultiplikation extends TestCase {
	
	public void testMultiplikation () {
		Aktie aktie = Aktien.getInstance().getAktie("testaktie");
		IndikatorAlgorithmus iB = aktie.addIndikator(new IndikatorKurswert());
		IndikatorAlgorithmus iB2 = aktie.addIndikator(new IndikatorVolatilitaet());
		iB2.addParameter("dauer", 30);
		
		IndikatorAlgorithmus iB3 = aktie.addIndikator(new IndikatorMultiplikation());
		iB3.addParameter("indikator1", iB);
		iB3.addParameter("indikator2", iB2);
	}

}
