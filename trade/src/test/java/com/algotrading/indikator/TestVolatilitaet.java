package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.indikator.Indikatoren;

import junit.framework.TestCase;

public class TestVolatilitaet extends TestCase {
	
	private static Aktie aktie; 
	private static IndikatorBeschreibung indikatorBeschreibung10; 
	private static IndikatorBeschreibung indikatorBeschreibung20; 
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		aktie = Aktien.getInstance().getAktie("sardata5");
		
		indikatorBeschreibung10 = new IndikatorBeschreibung(Indikatoren.INDIKATOR_VOLATILITAET);
		aktie.addIndikator(indikatorBeschreibung10);
		indikatorBeschreibung10.addParameter("dauer", 10);
		
		indikatorBeschreibung20 = new IndikatorBeschreibung(Indikatoren.INDIKATOR_VOLATILITAET);
		aktie.addIndikator(indikatorBeschreibung20);
		indikatorBeschreibung20.addParameter("dauer", 20);
	}
	
	public void testRechne () {
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(0.7262107f,testKurs.getIndikatorWert(indikatorBeschreibung10));
		testKurs = kurse.get(30);
		assertEquals(0.68043214f,testKurs.getIndikatorWert(indikatorBeschreibung10));
		assertEquals(0.90420943f,testKurs.getIndikatorWert(indikatorBeschreibung20));
		
	}

}
