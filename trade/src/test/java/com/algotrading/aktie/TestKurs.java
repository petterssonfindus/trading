package com.algotrading.aktie;

import java.util.List;

import org.junit.Test;

import com.algotrading.util.AbstractTest;

public class TestKurs extends AbstractTest {

	@Test
	public void testGetKurs() {

		Kurs kurs = kV.findKurs(50L);
		System.out.println("Kurs: " + kurs.toString());

	}

	@Test
	public void testSaveKursAll() {

		for (long l = 100000; l < 150000; l++) {
			Kurs kurs = kV.findKurs(l);
			if (kurs != null) {
				kurs.setAktie(aktie);
				System.out.println("Kurs: " + l + " " + kurs.getAktieName() + kurs.toString());
				kV.save(kurs);
			}
		}
	}

	@Test
	public void testCountAktie() {
		Aktie aktie = aV.getAktieLazy(49L);
		long test = kursDAO.countByAktie(aktie);
		System.out.println("anzahl:" + test);
	}

	@Test
	public void testFindKurseByAktie() {
		Aktie aktie = aV.getAktieLazy(49L);
		List<Kurs> kurse = kursDAO.findByAktie(aktie);
		System.out.println("kurse: " + kurse.size());
		for (Kurs kurs : kurse) {
			System.out.println("kurs: " + kurs.toString());
		}

	}
}
