package com.algotrading.indikator;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.algotrading.Application;
import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment=WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class TestIndikatorDAO {

	@Autowired
	IndikatorAlgorithmusDAO iADAO; 
	
	@Ignore
	@Test
	public void testFindByID() {
		List<IndikatorAlgorithmus> liste = iADAO.findAll();
		IndikatorAlgorithmus iA1 = liste.get(0);
		
		IndikatorAlgorithmus iA2 = iADAO.findByUUID(iA1.getId());
		boolean test = iA1.equals(iA2);
		assertTrue(test);
		
	}

	/**
	 * inhaltlich identisch, aber instantiiert und aus DB 
	 */
	@Test
	public void testIADAO() {
		
		List<IndikatorAlgorithmus> liste = iADAO.findAll();
		IndikatorAlgorithmus iA1 = liste.get(0);

		Aktie aktie = AktieVerzeichnis.getInstance().getAktieOhneKurse("testaktie");
		IndikatorAlgorithmus iA2 = aktie.addIndikatorAlgorithmus(new IndikatorAbweichung());
		iA2.addParameter("typ", 1);  // Typ 1 = open
		aktie.rechneIndikatoren();
		
		boolean test = iA1.equals(iA2);
		assertTrue(test);
		
	}

}
