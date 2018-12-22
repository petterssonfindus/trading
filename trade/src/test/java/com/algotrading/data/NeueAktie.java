package com.algotrading.data;

import com.algotrading.aktie.Aktie;

import junit.framework.TestCase;

public class NeueAktie extends TestCase {
	
	public static void testNeueAktie() {
		String kuerzel = "^IXIC";
		String firmenname = "Nasdaq Composite";
		String indexname = "";
		byte boersenplatz = 1;
		
		Aktie aktie = new Aktie(kuerzel, firmenname, indexname, boersenplatz);
		aktie.setQuelle(1);
		aktie.setISIN("XC0009694271");
		
		DBManager.neueAktie(aktie);
		
	}

}
