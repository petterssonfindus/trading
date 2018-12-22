package com.algotrading.data;

import junit.framework.TestCase;

public class TestReadFinanzenEinzeln extends TestCase {
	

	public void testReadFinanzen () {
		// liest Kurse und schreibt in eine Datei 
		String name = "fresenius";
		String datei = ReadDataFinanzen.FinanzenWSController(name ,false);
		
		ReadDataFinanzen.readFileWriteDB(datei ,name);

	}

}
