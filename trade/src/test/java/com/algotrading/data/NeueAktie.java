package com.algotrading.data;

import com.algotrading.aktie.Aktie;

import junit.framework.TestCase;

public class NeueAktie extends TestCase {
	
	public static void testNeueAktie() {
		String kuerzel = "umlaufrendite-de-oeffentliche-hand";
		String firmenname = "Umlaufrendite Deutschland";
		String indexname = "";
		byte boersenplatz = 0;
		
		Aktie aktie = new Aktie(kuerzel, firmenname, indexname, boersenplatz);
		aktie.setQuelle(3);
		aktie.setISIN("");
		
		DBManager.neueAktie(aktie);
		
		// Kurse einlesen
		if (aktie.getQuelle() == 1) {
			ReadDataYahoo.YahooWSController(kuerzel);
		}
		else if(aktie.getQuelle() == 2) {
			System.out.println("Kurse für Finanzen müssen manuell Beginndatum vorgegeben werden.");

		}

		
	}

}
