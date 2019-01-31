package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

import junit.framework.TestCase;

public class TestOnBalanceVolume extends TestCase {
	
	private static Aktie aktie; 
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		aktie = Aktien.newInstance().getAktie("testaktie");
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
		IndikatorAlgorithmus iAVolume = aktie.createIndikatorAlgorithmus(new IndikatorAbweichung());
		iAVolume.addParameter("typ", 5);
		
		IndikatorAlgorithmus iAOBV = aktie.createIndikatorAlgorithmus(new IndikatorOBV());
		iAOBV.addParameter("dauer", 10);
		iAOBV.addParameter("relativ", 1);
		
		IndikatorAlgorithmus iAGD = aktie.createIndikatorAlgorithmus(new IndikatorGD());
		iAGD.addParameter("dauer", 10);
		
		IndikatorAlgorithmus iAM = aktie.createIndikatorAlgorithmus(new IndikatorMultiplikation());
		iAM.addParameter("indikator1", iAOBV);
		iAM.addParameter("indikator2", iAGD);
		
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		Kurs testKurs;
		testKurs = kurse.get(13);
		System.out.println("IndikatorMultiplikator " + kurse.get(13).getIndikatorWert(iAM));
//		assertEquals(-183700f,testKurs.getIndikatorWert(iAM));
		
		aktie.writeFileKursIndikatorSignal();
	}
	

}
