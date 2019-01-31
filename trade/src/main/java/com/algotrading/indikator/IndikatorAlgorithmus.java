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
	
	boolean istBerechnet = false; 
	
	/**
	 * iteriert über alle Kurse dieser Aktie und berechnet die Indikatorenwerte, die dann am Kurs hängen
	 * Die Parameter hängen am Indikator-Algorithmus
	 */
	public abstract void rechne (Aktie aktie);
	
	public abstract String getKurzname () ;
	
	/**
	 * Wird von der Aktie aufgerufen, wenn die Berechnung erfolgt ist
	 */
	public void berechnet () {
		istBerechnet = true; 
	}
	
	/**
	 * Gibt Auskunft, ob bereits berechnet wurde
	 */
	public boolean istBerechnet() {
		return istBerechnet;
	}
	
	/**
	 * enthält den Kurznamen und eine Liste der vorhandenen Parameter
	 */
	public String toString() {
		String result = ";I:" + getKurzname(); 
		for (String name : this.getAllParameter().keySet()) {
			result = result + (Util.separatorCSV + name + ":" + this.getParameter(name));
		}
		return result; 
	}


}
