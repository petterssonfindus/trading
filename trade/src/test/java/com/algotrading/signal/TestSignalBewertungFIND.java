package com.algotrading.signal;

import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.algotrading.component.Signalverwaltung;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.jpa.SignalBewertungDAO;
import com.algotrading.jpa.SignalBewertungRepository;
import com.algotrading.signalbewertung.SignalBewertung;
import com.algotrading.util.AbstractTest;
import com.algotrading.util.Zeitraum;

public class TestSignalBewertungFIND extends AbstractTest {

	@Autowired
	Signalverwaltung sV;

	@Autowired
	SignalBewertungDAO sBDAO;

	@Autowired
	SignalBewertungRepository sBR;

	/**
	 * SignalBewertung aus der Datenbank lesen und testen
	 */
	@Test
	public void testFindSignalBewertungByID() {
		SignalBewertung test = sV.find(Long.valueOf(132154L));
		assertNotNull(test);
		SignalAlgorithmus sA = test.getSignalAlgorithmus();
		assertNotNull(sA);
		List<IndikatorAlgorithmus> liste = sA.getIndikatorAlgorithmen();
		assertTrue(liste.size() > 0);
	}

	/**
	 * Sucht mit Aktienname, Tage, SignalAlgo
	 */
	@Test
	public void testFindEqualsNameAndTage() {
		// sucht alle vergleichbaren SignalBewertungen
		List<SignalBewertung> testSBs = sBDAO.findByAktieNameAndTage("dax ariva", 30);
		assertTrue(testSBs.size() > 0);
		System.out.println(
				"Anzahl Bewertungen: " + testSBs.size());
		// für jede gefundene SignalBewertung sucht er das Pendant in der DB
		for (SignalBewertung sB : testSBs) {
			// vollständige SignalBewertung nachladen anhand der ID
			SignalBewertung sBneu = sV.find(sB.getId());
			System.out.println(
					"SB gesucht: " + sB.getId());
		}
	}

	@Test
	public void testFindBySignal() {
		// sucht alle vergleichbaren SignalBewertungen
		List<SignalBewertung> testSBs = sBR.findBySignal("testaktie", "MinMax", 10, "dauer", "15");
		System.out.println(
				"Anzahl Bewertungen: " + testSBs.size());
		// für jede gefundene SignalBewertung sucht er das Pendant in der DB
		for (SignalBewertung sB : testSBs) {
			// vollständige SignalBewertung nachladen
			System.out.println(
					"SB gesucht: " + sB.getId());
		}

	}

	/**
	 * Prüft mit einer SignalBewertung mit SignalAlgo und 1 IndikatorAlgo
	 */
	@Test
	public void testFindByTypedQuery() {
		SignalBewertung test = sV.find(Long.valueOf(132158L));
		// 		test.getSignalAlgorithmus().getIndikatorAlgorithmen().get(0).setP1wert("28");

		List<SignalBewertung> testSBs = sBDAO.findByTypedQuery(test);
		System.out.println(
				"Anzahl Bewertungen: " + testSBs.size());
		// für jede gefundene SignalBewertung sucht er das Pendant in der DB
		for (SignalBewertung sB : testSBs) {
			// vollständige SignalBewertung nachladen
			System.out.println(
					"SB gesucht: " + sB.getId());
		}

	}

	@Test
	public void testByAktieNameByQuery() {
		List<SignalBewertung> test = sBR.findByAktieNameByQuery("testaktie");
		System.out.println(
				"Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
			System.out.println(
					"SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}

	@Test
	public void testFindAllByQuery() {
		List<SignalBewertung> test = sBR.findAllByQuery();
		System.out.println(
				"Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
			System.out.println(
					"SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}

	@Test
	public void testFindByAktieName() {
		List<SignalBewertung> test = sBDAO.findByAktieName("testaktie");
		System.out.println(
				"Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
			System.out.println(
					"SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}

	@Test
	public void testFindByAktieNameAndTAge() {
		List<SignalBewertung> test = sBDAO.findByAktieNameAndTage("testaktie", 10);
		System.out.println(
				"Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
			System.out.println(
					"SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}

	@Test
	public void testFindByBeginn() {
		Zeitraum zeitraum = new Zeitraum(2016, 2016);
		GregorianCalendar beginn = zeitraum.beginn;
		List<SignalBewertung> test = sBDAO.findByZeitraumBeginn(beginn);
		System.out.println(
				"Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
			System.out.println(
					"SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}

	@Test
	public void testFindByAktieDauerBeginn() {
		Zeitraum zeitraum = new Zeitraum(2016, 2016);
		GregorianCalendar beginn = zeitraum.beginn;
		List<SignalBewertung> test = sBDAO.findByAktieNameAndTageAndZeitraumBeginn("testaktie", 10, beginn);
		System.out.println(
				"Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
			System.out.println(
					"SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}

}
