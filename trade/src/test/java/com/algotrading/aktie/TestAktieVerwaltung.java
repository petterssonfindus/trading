package com.algotrading.aktie;

import org.junit.Test;

import com.algotrading.util.AbstractTest;

public class TestAktieVerwaltung extends AbstractTest {

	@Test
	public void testNeueAktie() {

		createAktien();
		AktieVerzeichnis aktieVerzeichnis = aV.getVerzeichnis();
		for (Aktie aktie : aktieVerzeichnis.getAllAktien()) {
			System.out.println("Aktie: " + aktie.getName());
		}
		Iterable<Aktie> it2 = aV.getAll();
		AktieVerzeichnis aktieVerzeichnis2 = aV.getVerzeichnis();
		for (Aktie aktie : aktieVerzeichnis2.getAllAktien()) {
			System.out.println("Aktie: " + aktie.getName());
		}
	}

	@Test
	public void testGetAktie() {
		Aktie aktie = aV.getAktie(31l);
		assertNotNull(aktie);
	}

}
