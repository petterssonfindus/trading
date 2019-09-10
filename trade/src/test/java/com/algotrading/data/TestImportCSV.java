package com.algotrading.data;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.AbstractTest;

public class TestImportCSV extends AbstractTest {

	@Autowired
	private ImportCSV importCSV;

	@Autowired
	private ReadDataYahoo readDataYahoo;

	@Test
	public void testImportCSVYahoo() {
		Aktie aktie = aV.getAktieMitKurse(17478l);
		Aktie aktieNew = importCSV.readKurseYahooCSV("^NYA", aktie);
	}

	@Test
	public void testImportCSVAriva() {
		Aktie aktie = aV.getAktieMitKurse(30596l);
		Aktie aktieNew = importCSV.readKurseArivaCSV("wkn_710000_historic", aktie);
	}

	public void testReadYahoo() {
		readDataYahoo.YahooWSController("abt");
		assertTrue(true);
	}

}
