package com.algotrading.data;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;

import junit.framework.TestCase;

public class DBManagerCheckDateTest extends TestCase {

	public void testCheckKursreiheTage () {
		List<Aktie> aktien = AktieVerzeichnis.getInstance().getAllAktien();
		for (Aktie aktie : aktien) {
			DBManager.checkKursreiheTage(aktie.name);
		}
		
	}
	
}
