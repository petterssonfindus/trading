package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.util.Util;

import junit.framework.TestCase;

public class TestMultiplikation extends TestCase {
	
	Aktie aktie; 
	
	protected void setUp() throws Exception {
		super.setUp();
		aktie = Aktien.newInstance().getAktie("testaktie");
	}	
	
	/**
	 * Kurswert * Volatilität
	 */
	public void testMultiplikation () {
		IndikatorAlgorithmus iB = aktie.createIndikatorAlgorithmus(new IndikatorAbweichung());
		IndikatorAlgorithmus iB2 = aktie.createIndikatorAlgorithmus(new IndikatorVolatilitaet());
		iB2.addParameter("dauer", 10);
		
		IndikatorAlgorithmus iB3 = aktie.createIndikatorAlgorithmus(new IndikatorMultiplikation());
		iB3.addParameter("indikator1", iB);
		iB3.addParameter("indikator2", iB2);
		aktie.rechneIndikatoren();
		
		assertEquals(21.39f, aktie.getBoersenkurse().get(20).getIndikatorWert(iB));
		assertEquals("0,51", Util.toString(aktie.getBoersenkurse().get(20).getIndikatorWert(iB2)));
		assertEquals("10,909", Util.toString(aktie.getBoersenkurse().get(20).getIndikatorWert(iB3)));
	}
	
	/**
	 * Multipliziert einen Kurs-Ausschlag mit der Volatilität
	 * Ist dann hoch, wenn Vola und Ausschlag hoch ist. 
	 */
	public void testMultiMinMax() {
		IndikatorAlgorithmus iB = aktie.createIndikatorAlgorithmus(new IndikatorMinMax());
		iB.addParameter("dauer", 30);
		IndikatorAlgorithmus iB2 = aktie.createIndikatorAlgorithmus(new IndikatorVolatilitaet());
		iB2.addParameter("dauer", 30);
		
		IndikatorAlgorithmus iB3 = aktie.createIndikatorAlgorithmus(new IndikatorMultiplikation());
		iB3.addParameter("indikator1", iB);
		iB3.addParameter("indikator2", iB2);
		aktie.rechneIndikatoren();
//		aktie.writeFileIndikatoren();
		
	}

	/**
	 * Kurswert * Volatilität
	 */
	public void testMultiplikationReziprok () {
		IndikatorAlgorithmus iB = aktie.createIndikatorAlgorithmus(new IndikatorAbweichung());
		IndikatorAlgorithmus iB2 = aktie.createIndikatorAlgorithmus(new IndikatorVolatilitaet());
		iB2.addParameter("dauer", 10);
		
		IndikatorAlgorithmus iB3 = aktie.createIndikatorAlgorithmus(new IndikatorMultiplikation());
		iB3.addParameter("indikator1", iB);
		iB3.addParameter("indikator2", iB2);
		iB3.addParameter("reziprok2", 1);
		aktie.rechneIndikatoren();

		assertEquals(21.39f, aktie.getBoersenkurse().get(20).getIndikatorWert(iB));
		assertEquals("0,51", Util.toString(aktie.getBoersenkurse().get(20).getIndikatorWert(iB2)));
		assertEquals("41,942", Util.toString(aktie.getBoersenkurse().get(20).getIndikatorWert(iB3)));
	}

}
