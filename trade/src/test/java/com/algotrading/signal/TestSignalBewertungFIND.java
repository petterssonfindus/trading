package com.algotrading.signal;

import java.util.GregorianCalendar;
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
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment=WebEnvironment.RANDOM_PORT)
@DirtiesContext

public class TestSignalBewertungFIND extends TestCase {
	
	@Autowired 
	Signalverwaltung sV;
	
	@Autowired 
	SignalBewertungDAO sBDAO; 
	
	@Autowired
	SignalBewertungRepository sBR;
	
//	@Ignore
	@Test 
	public void testFindSignalBewertungByID() {
		SignalBewertung test = sV.find(Long.valueOf(22));
		assertNotNull(test);
	}
	
//	@Ignore
	@Test
	public void testByAktieNameByQuery() {
		List<SignalBewertung> test = sBR.findByAktieNameByQuery("testaktie");
		System.out.println("Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
		System.out.println("SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}

//	@Ignore
	@Test
	public void testFindAllByQuery() {
		List<SignalBewertung> test = sBR.findAllByQuery();
		System.out.println("Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
		System.out.println("SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}

	@Ignore
	@Test
	public void testFindByAktieName() {
		List<SignalBewertung> test = sBDAO.findByAktieName("testaktie");
		System.out.println("Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
		System.out.println("SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}
	@Ignore
	@Test
	public void testFindByAktieNameAndTAge() {
		List<SignalBewertung> test = sBDAO.findByAktieNameAndTage("testaktie",10);
		System.out.println("Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
		System.out.println("SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}
	
	@Ignore
	@Test
	public void testFindByBeginn() {
		Zeitraum zeitraum = new Zeitraum(2016, 2016);
		GregorianCalendar beginn = zeitraum.beginn;
		List<SignalBewertung> test = sBDAO.findByZeitraumBeginn(beginn);
		System.out.println("Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
			System.out.println("SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}
	
	@Ignore
	@Test
	public void testFindByAktieDauerBeginn() {
		Zeitraum zeitraum = new Zeitraum(2016, 2016);
		GregorianCalendar beginn = zeitraum.beginn;
		List<SignalBewertung> test = sBDAO.findByAktieNameAndTageAndZeitraumBeginn("testaktie", 10, beginn);
		System.out.println("Anzahl Bewertungen: " + test.size());
		for (SignalBewertung sB : test) {
			System.out.println("SB:" + sB.getAktieName() + sB.getId());
		}
		assertNotNull(test);
	}
	
}
