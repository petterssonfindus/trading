package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

import junit.framework.TestCase;

public class TestOnBalanceVolume extends TestCase {
	
	private static Aktie aktie; 
	private static IndikatorAlgorithmus iA; 
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		aktie = Aktien.newInstance().getAktie("testaktie");
		IndikatorAlgorithmus iAVolume = aktie.createIndikatorAlgorithmus(new IndikatorKurswert());
		iAVolume.addParameter("typ", 5);
		iA = aktie.createIndikatorAlgorithmus(new IndikatorOBV());
		aktie.createIndikatorAlgorithmus(iA);
		iA.addParameter("dauer", 10);
	}
	
	public void testOnBalanceVolume () {
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(-183700f,testKurs.getIndikatorWert(iA));
		
//		aktie.writeFileKursIndikatorSignal();
	}

}
