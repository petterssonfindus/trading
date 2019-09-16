package com.algotrading.aktie;

import java.util.List;

import com.algotrading.util.AbstractTest;

public class TestAktien extends AbstractTest {

	public Aktie aktie;

	public void setUp() {

		aktie = aV.getAktie("testaktie");

	}

	/*	
		public void testGetKursreihe() {
			assertNotNull(aktie);
			assertEquals("testaktie", aktie.name);
			ArrayList<Kurs> kursreihe = aktie.getBoersenkurse();
			assertTrue(kursreihe.size() > 200);
			// der 2. Aufruf bekommt die selbe Kursreihe 
			Aktie aktie2 = Aktien.getInstance().getAktie("testaktie");
			assertTrue(aktie == aktie2);
			assertTrue(aktie.getBoersenkurse() == aktie2.getBoersenkurse());
			
		}
	*/
	public void testVerzeichnis() {
		assertNotNull(aktie);
		List<Aktie> alleAktien = aV.getAktienListe();
		assertNotNull(alleAktien);
		assertTrue(alleAktien.size() > 10);
		for (Aktie aktie : alleAktien) {
			assertNotNull(aktie);
		}
	}
	/*	
	public void testGetAktien () {
		
		GregorianCalendar beginn = DateUtil.createGregorianCalendar(1, 1, 2000); 
		List<Aktie> aktien = Aktien.getInstance().getAktien(beginn);
		assertNotNull(aktien);
		assertTrue(aktien.size() > 30);
		
	}
	*/

}
