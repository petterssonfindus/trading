package com.algotrading.aktie;

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

public class AktienTest extends TestCase {
	
	public void testGetKursreihe() {
		Aktie aktie = Aktien.getInstance().getAktie("dax");
		assertNotNull(aktie);
		assertEquals("dax", aktie.name);
		ArrayList<Kurs> kursreihe = aktie.getBoersenkurse();
		assertTrue(kursreihe.size() > 200);
		// der 2. Aufruf bekommt die selbe Kursre ihe 
		Aktie aktie2 = Aktien.getInstance().getAktie("dax");
		assertTrue(aktie == aktie2);
		assertTrue(aktie.getBoersenkurse() == aktie2.getBoersenkurse());
		
	}
	
	public void testGetAktien () {
		
		GregorianCalendar beginn = new GregorianCalendar(2000,0,1); 
		GregorianCalendar ende = new GregorianCalendar(2010,0,1); 
		Zeitraum zeitraum = new Zeitraum (beginn, ende);
		ArrayList<Aktie> aktien = Aktien.getInstance().getAktien(zeitraum, false);
		assertNotNull(aktien);
		assertTrue(aktien.size() > 30);
		
	}
	

}
