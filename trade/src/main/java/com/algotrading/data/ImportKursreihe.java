package com.algotrading.data;

import java.util.ArrayList;
import java.util.List;

import com.algotrading.aktie.Kurs;

/**
 * Dient als Zwischenspeicher für eingelesene Kursdaten, die anschließend in die DB geschrieben werden. 
 * Wird genutzt aus csv oder aus json-WebService-Response
 * @author Oskar
 */
public class ImportKursreihe {

	protected String kuerzel;
	protected List<Kurs> kurse = new ArrayList<Kurs>();

	ImportKursreihe(String kuerzel) {
		this.kuerzel = kuerzel;
	}

	public List<Kurs> getKurse() {
		return kurse;
	}

}
