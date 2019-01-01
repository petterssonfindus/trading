package com.algotrading.data;

import com.algotrading.aktie.Aktie;

import junit.framework.TestCase;

public class NeueAktie extends TestCase {
	
	public static void testNeueAktie() {
		String kuerzel = "^VIX1Y";
		String firmenname = "Volatilität Chicago Options CBOE 1 Jahr";
		String indexname = "";
		byte boersenplatz = 1;
		
		Aktie aktie = new Aktie(kuerzel, firmenname, indexname, boersenplatz);
		aktie.setQuelle(1);
		aktie.setISIN("");
		
		DBManager.neueAktie(aktie);
		
		// Kurse einlesen
		if (aktie.getQuelle() == 1) {
			ReadDataYahoo.YahooWSController(kuerzel);
		}
		else if(aktie.getQuelle() == 2) {
			String datei = ReadDataFinanzen.FinanzenWSController(kuerzel, false);
			ReadDataFinanzen.readFileWriteDB(datei ,kuerzel);

		}

		
	}

}
