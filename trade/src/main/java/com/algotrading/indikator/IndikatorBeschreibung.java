package com.algotrading.indikator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.util.Parameter;

/**
 * Beschreibt einen Indikator mit Typ 
 * Alle Parameter werden über die Parameter festgelegt
 * @author oskar
 *
 */
public class IndikatorBeschreibung extends Parameter {
	private static final Logger log = LogManager.getLogger(IndikatorBeschreibung.class);
	
	private short typ; 

	/**
	 * Setzt den Typ. Parameter können mit addParameter hinzugefügt werden. 
	 * @param typ
	 */
	public IndikatorBeschreibung (short typ) {
		this.typ = typ; 
	}

	public short getTyp() {
		return typ;
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
