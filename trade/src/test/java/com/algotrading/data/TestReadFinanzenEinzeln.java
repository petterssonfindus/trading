package com.algotrading.data;

import junit.framework.TestCase;

public class TestReadFinanzenEinzeln extends TestCase {
	

	public void testReadFinanzenEinzeln () {
		// liest Kurse und schreibt in eine Datei 
		String name = "vdax-new-3m";
		String datei = ReadDataFinanzen.FinanzenWSController(name ,true, true, null);
		System.out.println("Finanzen-Datei geschrieben: " + datei);
// 		ReadDataFinanzen.readFileWriteDB(datei ,name);

	}

}
