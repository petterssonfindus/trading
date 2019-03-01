package com.algotrading.data;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;

import junit.framework.TestCase;

public class DBManagerCheckDateTest extends TestCase {

	public void testCheckKursreiheTage () {
		List<Aktie> aktien = Aktien.getInstance().getAllAktien();
		for (Aktie aktie : aktien) {
			DBManager.checkKursreiheTage(aktie.name);
		}
		
	}
	
}
