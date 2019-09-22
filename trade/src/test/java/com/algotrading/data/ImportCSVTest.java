package com.algotrading.data;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import junit.framework.TestCase;

public class ImportCSVTest extends TestCase {
	private static final Logger log = LogManager.getLogger(ImportCSV.class);
	private static String name = "wkn_846931_historic";

	/*
		public void testAllesEinlesen() {
			ImportCSVsimple.readPfadKurseYahooCSV();
		}
	*/

	/*
		public void testImportCSVYahoo() {
			// holt sich ein File der gewünschten Datei
			File file = ImportCSVsimple.getCSVFile(name);
			ImportKursreihe kursreihe = ImportCSVsimple.readKurseYahooCSV(file);
			assertNotNull(kursreihe);
			log.info("Kursreihe wurde eingelesen: " + kursreihe.kuerzel);
			DBManager.schreibeNeueAktieTabelle(kursreihe.kuerzel);
			DBManager.schreibeKurse(kursreihe);
		}
	*/

	public void testImportCSVAriva() {
		// holt sich ein File der gewünschten Datei
		File file = ImportCSV.getCSVFile("wkn__historic");
		String aktiename = "umlaufrendite-de-oeffentliche-hand";
		ImportKursreihe kursreihe = ImportCSV.readKurseArivaCSV(file, aktiename);
		assertNotNull(kursreihe);
		log.info("Kursreihe wurde eingelesen: " + kursreihe.kuerzel);
		//		DBManager.schreibeNeueAktieTabelle(kursreihe.kuerzel);
		DBManager.schreibeKurse(kursreihe);
	}

	/*
	public void testImportPath() {
		String test; 
		File[] files = ImportCSVsimple.getCSVFilesInPath();
		for (File file : files) {
			String name = Util.addAnfZeichen(file.getName().replace(".csv", ""));
			test = "		verzeichnis.put(" + name + ", new Aktie (" + name + ", " + name + ", Aktien.INDEXDOWJONES, Aktien.BOERSENYSE));";
			System.out.println(test);
		}
	}
	*/

}
