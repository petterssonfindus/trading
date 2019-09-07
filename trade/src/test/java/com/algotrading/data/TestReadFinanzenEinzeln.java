package com.algotrading.data;

import com.algotrading.util.AbstractTest;

public class TestReadFinanzenEinzeln extends AbstractTest {

	public void testReadFinanzenEinzeln() {
		// liest Kurse und schreibt in eine Datei 
		String name = "vdax-new-3m";
		String datei = ReadDataFinanzen.FinanzenWSController(name, true, true, null);
		System.out.println("Finanzen-Datei geschrieben: " + datei);
		// 		ReadDataFinanzen.readFileWriteDB(datei ,name);

	}

}
