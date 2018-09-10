package com.algotrading.indikator;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.util.Parameter;

/**
 * Beschreibt einen Indikator mit Typ und Wert
 * Der Wert ist meist die Zeitdauer
 * @author oskar
 *
 */
public class IndikatorBeschreibung extends Parameter {
	private static final Logger log = LogManager.getLogger(IndikatorBeschreibung.class);
	
	short typ; 

	/**
	 * Setzt den Typ. Parameter kännen mit addParameter hinzugefägt werden. 
	 * @param typ
	 */
	public IndikatorBeschreibung (short typ) {
		this.typ = typ; 
	}
	/**
	 * Ein Konstruktor, der den Standard-Parameter "dauer" setzt 
	 * @param typ
	 * @param dauer
	 */
	public IndikatorBeschreibung (short typ, int dauer) {
		this.typ = typ; 
		addParameter("dauer", (float) dauer);
	}

	/**
	 * enthält den Typ und eine Liste der vorhandenen Parameter
	 */
	public String toString() {
		String result = "Indi-" + this.typ; 
		for (String name : this.getAllParameter().keySet()) {
			result = result + ("-" + name + ":" + this.getParameter(name));
		}
		return result; 
	}

}
