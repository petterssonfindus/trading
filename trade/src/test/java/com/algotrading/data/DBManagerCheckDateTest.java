package com.algotrading.data;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.AbstractTest;

public class DBManagerCheckDateTest extends AbstractTest {

	public void testCheckKursreiheTage() {
		List<Aktie> aktien = aV.getAktienListe();
		for (Aktie aktie : aktien) {
			DBManager.checkKursreiheTage(aktie.name);
		}

	}

}
