package com.algotrading.indikator;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;

import junit.framework.TestCase;

public class TestIndikatorBerechnung extends TestCase {

	static Aktie aktie; 
	
	public void setUp() {
		aktie = Aktien.newInstance().getAktie("testaktie");
	}
	
	/**
	 * stellt sicher, dass die Reihenfolge des Einstellens bei der Berechnung gleich bleibt. 
	 */
	public static void testReihenfolge() {
		IndikatorAlgorithmus iA1 = aktie.addIndikatorAlgorithmus(new IndikatorGDalt());
		iA1.addParameter("test", 1);
		IndikatorAlgorithmus iA2 = aktie.addIndikatorAlgorithmus(new IndikatorGDalt());
		iA2.addParameter("test", 2);
		IndikatorAlgorithmus iA3 = aktie.addIndikatorAlgorithmus(new IndikatorGDalt());
		iA3.addParameter("test", 3);
		IndikatorAlgorithmus iA4 = aktie.addIndikatorAlgorithmus(new IndikatorGDalt());
		iA4.addParameter("test", 4);
		IndikatorAlgorithmus iA5 = aktie.addIndikatorAlgorithmus(new IndikatorGDalt());
		iA5.addParameter("test", 5);
		IndikatorAlgorithmus iA6 = aktie.addIndikatorAlgorithmus(new IndikatorGDalt());
		iA6.addParameter("test", 6);
		
		List<IndikatorAlgorithmus> algos = aktie.getIndikatorAlgorithmen();
		for (IndikatorAlgorithmus iATest : algos) {
			int t = (Integer) iATest.getParameter("test");
			if (t == 4) assertEquals(iATest, iA4);
		}
		
	}
	
	
}
