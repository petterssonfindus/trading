package com.algotrading.data;

import junit.framework.TestCase;

public class TestReadFinanzenAusDatei extends TestCase {
	
	public void testReadKursAusFile () {
		ReadDataFinanzen.readFileWriteDB("VDAX_NEW1545321804412.txt","VDAX_NEW");
	}

}
