package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.indikator.Indikatoren;

import junit.framework.TestCase;

public class TestMoneyFlowMultiplier extends TestCase {
	
	private static Aktie aktie; 
	private static IndikatorBeschreibung indikatorBeschreibung; 
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		
		aktie = Aktien.getInstance().getAktie("testaktie");
		indikatorBeschreibung = new IndikatorBeschreibung(Indikatoren.INDIKATOR_MFM);
		aktie.addIndikator(indikatorBeschreibung);
		indikatorBeschreibung.addParameter("dauer", 10);
	}
	
	public void testMoneyFlowMultiplier () {
		aktie.rechneIndikatoren();
		
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		Kurs testKurs;
		testKurs = kurse.get(13);
		assertEquals(-0.43711352f,testKurs.getIndikatorWert(indikatorBeschreibung));
		
	}

}
