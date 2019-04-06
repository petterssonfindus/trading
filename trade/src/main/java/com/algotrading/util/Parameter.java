package com.algotrading.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Verwaltet eine Liste von Parametern mit Namen und beliebigem Object.
 * Es kann sich um einen Zahlen-Wert handeln oder einen Zeitraum oder eine IndikatorAlgorithmus
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
		if (result == null) {
		}
		return result; 
	}
	
	public HashMap<String, Object> getAllParameter () {
		return this.parameter;
	}
	
	/**
	 * Alle Parameter als Liste mit Para-Objekten 
	 */
	public List<Para> getParameterList () {
		ArrayList<Para> paras = new ArrayList<Para>();
		for (String name : this.parameter.keySet()) {
			// einen neuen Para erzeugen mit Name und zugehörigem Objekt 
			paras.add(new Para(name, this.parameter.get(name)));
		}
		return paras; 
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
			// der Parameter "indikator" ist eine Referenz auf eine IndikatorAlgorithmus
//			if (key =="indikator") result = result.concat(value.toString());
			result = result.concat(Util.separatorCSV + key + ":" + value);
		}
		return result; 
	}
	
	public class Para {
		private String name; 
		private Object object;
		Para(String name, Object object){
			this.name = name; 
			this.object = object; 
		}
		public String getName() {
			return name;
		}
		public Object getObject() {
			return object;
		} 
	}
	
}
