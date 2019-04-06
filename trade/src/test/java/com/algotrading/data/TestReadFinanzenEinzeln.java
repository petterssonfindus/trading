package com.algotrading.data;

import junit.framework.TestCase;

public class TestReadFinanzenEinzeln extends TestCase {
	

	public void testReadFinanzen () {
		// liest Kurse und schreibt in eine Datei 
		String name = "vdax_new";
		String datei = ReadDataFinanzen.FinanzenWSController(name ,true);
		
		ReadDataFinanzen.readFileWriteDB(datei ,name);

	}

}
