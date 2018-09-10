package com.algotrading.data;

import java.util.ArrayList;

import com.algotrading.aktie.Kurs;

/**
 * Dient als Zwischenspeicher fär eingelesene Kursdaten, die anschlieäend in die DB geschrieben werden. 
 * @author Oskar
 */
public class ImportKursreihe {
	
	protected String kuerzel; // wird aus dem Dateinamen erzeugt
	protected ArrayList<Kurs> kurse = new ArrayList<Kurs>();
	
	ImportKursreihe (String kuerzel) {
		this.kuerzel = kuerzel; 
	}
	
}
