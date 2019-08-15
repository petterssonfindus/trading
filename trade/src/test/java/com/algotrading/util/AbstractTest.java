package com.algotrading.util;

import org.junit.Before;
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
import com.algotrading.component.Signalverwaltung;
import com.algotrading.signal.SignalBewertungDAO;
import com.algotrading.signal.SignalBewertungRepository;

import junit.framework.TestCase;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext

public class AbstractTest extends TestCase {

	@Autowired
	protected Signalverwaltung sV;

	@Autowired
	protected SignalBewertungDAO sBDAO;

	@Autowired
	protected SignalBewertungRepository sBR;

	public Aktie aktie;

	@Before
	public void setUp() {
		System.out.println("JUnitSetup() ausgeführt");
		this.aktie = AktieVerzeichnis.newInstance().getAktieMitKurse("testaktie");
	}

}
