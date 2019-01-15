package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.Parameter;
import com.algotrading.util.Util;

/**
 * Alle Indikatoren müssen rechnen können 
 * @author oskar
 *
 */
public abstract class IndikatorAlgorithmus extends Parameter {
	
	/**
	 * iteriert über alle Kurse dieser Aktie und berechnet die Indikatorenwerte, die dann am Kurs hängen
	 * Die Parameter hängen an der Indikator-Beschreibung
	 * @param aktie
	 * @param IndikatorAlgorithmus
	 */
	abstract void rechne (Aktie aktie);
	
	public abstract String getKurzname () ;
	
	/**
	 * enthält den Typ und eine Liste der vorhandenen Parameter
	 */
	public String toString() {
		String result = ";I:" + getKurzname(); 
		for (String name : this.getAllParameter().keySet()) {
			result = result + (Util.separatorCSV + name + ":" + this.getParameter(name));
		}
		return result; 
	}


}
