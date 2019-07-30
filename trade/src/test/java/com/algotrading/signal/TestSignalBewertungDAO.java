package com.algotrading.signal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.algotrading.Application;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment=WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class TestSignalBewertungDAO {

	@Autowired
	SignalBewertungDAO sBDAO; 
	
	@Autowired
	SignalBewertungRepository sBR; 
	
	@Test
	public void test() {
		// holt sich die erste Signalbewertung die er findet 
		SignalBewertung sB1 = null; 
		Iterable<SignalBewertung> sBs = sBR.findAll();
		for (SignalBewertung sB : sBs) {
			sB1 = sB; 
			break; 
		}
		// instantiiert anhand der ID die SignalBewertung
		SignalBewertung sB2 = sBDAO.find(sB1.getId());
		boolean test = sB1.equals(sB2);
		assertTrue(test);
		
	}

}
