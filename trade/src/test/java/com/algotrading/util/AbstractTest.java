package com.algotrading.util;

import java.util.GregorianCalendar;

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
import com.algotrading.component.AktieVerwaltung;
import com.algotrading.component.Signalverwaltung;
import com.algotrading.jpa.SignalBewertungDAO;
import com.algotrading.jpa.SignalBewertungRepository;

import junit.framework.TestCase;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class AbstractTest extends TestCase {

	@Autowired
	protected AktieVerwaltung aV;

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
	}

	/**
	 * Kann von Testfällen genutzt werden, um H2-DB mit initialen Aktien zu befüllen  
	 */
	public void createAktien() {
		Aktie aktie = new Aktie("umalauf" + new GregorianCalendar().getTimeInMillis());
		aktie.setISIN("123456789012");
		aktie.setLand(1);
		aktie.setQuelle(1);
		aktie.setWaehrung(1);
		aktie.setIndexname("DAX");
		aktie.setFirmenname("AG");
		aktie.setBoersenplatz((byte) 2);

		aV.saveAktie(aktie);

	}

}
