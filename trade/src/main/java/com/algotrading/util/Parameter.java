package com.algotrading.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.function.BiConsumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.indikator.IndikatorBeschreibung;

/**
 * Verwaltet eine Liste von Parametern mit Namen und beliebigem Object.
 * Es kann sich um einen Zahlen-Wert handeln oder einen Zeitraum oder eine Indikatorbeschreibung
 * Über den Namen kann man sich den Wert besorgen. 
 * @author oskar
 */
public class Parameter {
	static final Logger log = LogManager.getLogger(Parameter.class);

	private HashMap<String, Object> parameter = new HashMap<String, Object>();

	/**
	 * Holt ein Parameter, falls er vorhanden ist - ansonsten null
	 * @param name
	 * @return der Wert als Object, oder null
	 */
	public Object getParameter (String name) {
		Object result; 
		result = this.parameter.get(name);
		return result; 
	}
	
	public HashMap<String, Object> getAllParameter () {
		return this.parameter;
	}
	/**
	 * wenn es den Parameter bereits gibt, wird der Wert überschrieben 
	 */
	public void addParameter (String name, float wert) {
		this.parameter.put(name, wert);
	}
	
	/**
	 * wenn es den Parameter bereits gibt, wird der Wert überschrieben 
	 */
	public void addParameter (String name, int wert) {
		this.parameter.put(name, wert);
	}

	public void addParameter (String name, Number zahl) {
		
		this.parameter.put(name, zahl);
	}
	
	public void addParameter (String name, Object o) {
		
		this.parameter.put(name, o);
	}
	
	public String toStringParameter () {
		String result = ""; 
		Iterator<String> it = this.parameter.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();	// der Key als String
			String value = this.parameter.get(key).toString();  // der Value als Object
			// der Parameter "indikator" ist eine Referenz auf eine IndikatorBeschreibung
//			if (key =="indikator") result = result.concat(value.toString());
			result = result.concat(Util.separatorCSV + key + ":" + value);
		}
		return result; 
	}
	
}
