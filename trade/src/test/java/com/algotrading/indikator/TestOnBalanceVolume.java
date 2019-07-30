package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

import junit.framework.TestCase;

public class TestOnBalanceVolume extends TestCase {
	
	private static Aktie aktie; 
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		aktie = AktieVerzeichnis.newInstance().getAktieOhneKurse("testaktie");
	}
	
/*
	public void testOnBalanceVolume () {
		iA = aktie.createIndikatorAlgorithmus(new IndikatorOBV());
		iA.addParameter("dauer", 10);
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(-183700f,testKurs.getIndikatorWert(iA));
		
//		aktie.writeFileKursIndikatorSignal();
	}
*/
	public void testOnBalanceVolumeRelativ () {
		IndikatorAlgorithmus iAVolume = aktie.addIndikatorAlgorithmus(new IndikatorAbweichung());
		iAVolume.addParameter("typ", 5);

		IndikatorAlgorithmus iAOBVa = aktie.addIndikatorAlgorithmus(new IndikatorOBV());
		iAOBVa.addParameter("dauer", 10);
		
		IndikatorAlgorithmus iAOBVs = aktie.addIndikatorAlgorithmus(new IndikatorOBV());
		iAOBVs.addParameter("dauer", 10);
		iAOBVs.addParameter("stabw", 10);
		
		IndikatorAlgorithmus iAOBVr = aktie.addIndikatorAlgorithmus(new IndikatorOBV());
		iAOBVr.addParameter("dauer", 10);
		iAOBVr.addParameter("stabw", 10);
		iAOBVr.addParameter("relativ", 1);

		IndikatorAlgorithmus iAOBVf = aktie.addIndikatorAlgorithmus(new IndikatorOBV());
		iAOBVf.addParameter("dauer", 10);
		iAOBVf.addParameter("stabw", 10);
		iAOBVf.addParameter("faktor", 1);

		IndikatorAlgorithmus iAOBVl = aktie.addIndikatorAlgorithmus(new IndikatorOBV());
		iAOBVl.addParameter("dauer", 10);
		iAOBVl.addParameter("stabw", 10);
		iAOBVl.addParameter("log", 1);

		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getKursListe();
		Kurs testKurs = kurse.get(23);
		System.out.println("IndikatorOBVabsolut " + testKurs.getIndikatorWert(iAOBVa));
		System.out.println("IndikatorOBVStabw " + testKurs.getIndikatorWert(iAOBVs));
		System.out.println("IndikatorOBVrelativ " + testKurs.getIndikatorWert(iAOBVr));
		System.out.println("IndikatorOBVfaktor " + testKurs.getIndikatorWert(iAOBVf));
		System.out.println("IndikatorOBVlog " + testKurs.getIndikatorWert(iAOBVl));
		assertEquals(188800.0f,testKurs.getIndikatorWert(iAOBVa));
		assertEquals(139502.28f,testKurs.getIndikatorWert(iAOBVs));
		assertEquals(-0.15254231f,testKurs.getIndikatorWert(iAOBVr));
		assertEquals(0.98474574f,testKurs.getIndikatorWert(iAOBVf));
		assertEquals(-0.0066758767f,testKurs.getIndikatorWert(iAOBVl));
		
//		aktie.writeFileKursIndikatorSignal();
	}
	

}
