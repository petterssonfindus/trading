package com.algotrading.data;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.algotrading.util.AbstractTest;

public class TestNeueAktie extends AbstractTest {

	@Autowired
	private ReadDataYahoo readDataYahoo;

	@Test
	public void testNeueAktie() {

		// Kurse einlesen
		if (aktie.getQuelle() == 1) {
			String kuerzel = null;
			readDataYahoo.YahooWSController(kuerzel);
		} else if (aktie.getQuelle() == 2) {
			System.out.println("Kurse für Finanzen müssen manuell Beginndatum vorgegeben werden.");

		}

	}

}
