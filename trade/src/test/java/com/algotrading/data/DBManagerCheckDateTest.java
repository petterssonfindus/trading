package com.algotrading.data;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;

import junit.framework.TestCase;

public class DBManagerCheckDateTest extends TestCase {

	public void testCheckKursreiheTage () {
		ArrayList<Aktie> aktien = Aktien.getInstance().getAllAktien();
		for (Aktie aktie : aktien) {
			DBManager.checkKursreiheTage(aktie.name);
		}
		
	}
	
}
