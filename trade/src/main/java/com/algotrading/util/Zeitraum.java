package com.algotrading.util;

import java.util.GregorianCalendar;

public class Zeitraum {
	public GregorianCalendar beginn;
	public GregorianCalendar ende;
	
	public Zeitraum (GregorianCalendar beginn, GregorianCalendar ende){
		this.beginn = beginn;
		this.ende = ende; 
	}
	
	public boolean equals (Object input) {
		Zeitraum inputZS = (Zeitraum) input; 
		if (DateUtil.istGleicherKalendertag(inputZS.beginn, this.beginn) && DateUtil.istGleicherKalendertag(inputZS.ende, this.ende)) {
			return true;
		}
		return false;
	}
	
	public String toString () {
		return ("Beginn: " + DateUtil.formatDate(beginn) + " Ende: " + DateUtil.formatDate(ende));
	}
}
	
