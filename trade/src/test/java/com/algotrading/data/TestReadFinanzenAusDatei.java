package com.algotrading.data;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.AbstractTest;

public class TestReadFinanzenAusDatei extends AbstractTest {

	@Autowired
	private ReadDataFinanzen readDataFinanzen;

	@Test
	public void testReadKursAusFile() {
		readDataFinanzen.readFileWriteDB("vdax-new-3m%1554444002381.csv", "VDAX_NEW3M");
	}

	@Test
	public void testCreateAndReadKursAusFile() {
		Aktie aktie = new Aktie("VDAX_NEW9M");
		aktie.setLand(1);
		aktie.setQuelle(1);
		Aktie newAktie = aV.createAktie(aktie);
		readDataFinanzen.readFileWriteDB("vdax-new-9m%1554444300167.csv", newAktie.getName());
	}
}
