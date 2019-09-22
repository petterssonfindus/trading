package com.algotrading.aktie;

import java.util.List;

import org.junit.Test;

import com.algotrading.util.AbstractTest;

public class TestAktieVerwaltung extends AbstractTest {

	@Test
	public void testNeueAktie() {

		List<Aktie> aktieListe = aV.getAktienListe();
		for (Aktie aktie : aktieListe) {
			System.out.println("Aktie: " + aktie.getName());
		}
		Iterable<Aktie> it2 = aV.getAktienAusDB();

		for (Aktie aktie : it2) {
			System.out.println("Aktie: " + aktie.getName());
		}
	}

	@Test
	public void testGetAktieLazyLoading() {
		Aktie aktie = aV.getAktieLazy(getTestAktieId());
		assertNotNull(aktie);
		aktie.getKursListe();
		System.out.println("Aktienkurse: " + aktie.getKursListe().size());
	}

	@Test
	public void testGetAktieLazy() {
		Aktie aktie = aV.getAktieLazy(31l);
		assertNotNull(aktie);
		aktie.getKursListe();
		System.out.println("Aktienkurse: " + aktie.getKursListe().size());
	}
}
