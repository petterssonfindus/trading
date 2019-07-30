package com.algotrading.aktie;

import java.io.File;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.algotrading.Application;
import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Kurs;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment=WebEnvironment.RANDOM_PORT)
@DirtiesContext

public class TestAktien extends TestCase {
	
	public Aktie aktie; 
	
	public void setUp() {
		
		aktie = AktieVerzeichnis.newInstance().getAktieOhneKurse("testaktie");
		
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
		List<Aktie> alleAktien = AktieVerzeichnis.getInstance().getAllAktien();
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
