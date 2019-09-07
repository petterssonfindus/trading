package com.algotrading.data;

import com.algotrading.util.AbstractTest;

public class TestReadFinanzenAusDatei extends AbstractTest {

	public void testReadKursAusFile() {
		ReadDataFinanzen.readFileWriteDB("VDAX_NEW1545321804412.txt", "VDAX_NEW");
	}

}
