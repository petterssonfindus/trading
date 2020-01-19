package com.algotrading.aktie;

import java.util.List;

import org.junit.Test;

import com.algotrading.util.AbstractTest;
import com.algotrading.util.DateUtil;

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
		long beginn = DateUtil.getTimeInMillis();
		Aktie aktie = aV.getAktieLazy(259l);
		long lazy = DateUtil.getTimeInMillis();
		assertNotNull(aktie);
		Aktie aktie2 = aV.getAktieLazy(259l);
		long lazy2 = DateUtil.getTimeInMillis();
		aktie.getKursListe();
		long ende = DateUtil.getTimeInMillis();
		System.out.println("Aktienkurse: " + aktie.getKursListe().size() + " Dauer: " + (lazy - beginn));
		System.out.println("Kurse lesen1 " + (ende - beginn));
		System.out.println("Kurse lesen2 " + (lazy2 - lazy));
	}
}
