package com.algotrading.signal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.algotrading.Application;
import com.algotrading.component.Signalverwaltung;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext

public class TestSignalVerwaltung {

	@Autowired
	Signalverwaltung sV;

	@Test
	public void testExistsInDB() {
		SignalBewertung test = sV.find(Long.valueOf(110));
		assertNotNull(test);
		List<SignalBewertung> liste = sV.existsExact(test);
		assertEquals(1, liste.size());
	}

}
