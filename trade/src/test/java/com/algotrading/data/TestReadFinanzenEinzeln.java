package com.algotrading.data;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.AbstractTest;

public class TestReadFinanzenEinzeln extends AbstractTest {

	@Autowired
	private ReadDataFinanzen rdf;

	@Test
	public void testReadFinanzenEinzeln() {
		// liest Kurse und schreibt in eine Datei 
		Aktie aktie = aV.getAktieLazy("dax");
		String datei = rdf.FinanzenWSController(aktie, true, true, null);
		System.out.println("Finanzen-Datei geschrieben: " + datei);
		// 		ReadDataFinanzen.readFileWriteDB(datei ,name);

	}

}
