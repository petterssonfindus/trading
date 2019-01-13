package com.algotrading.util;

import java.util.GregorianCalendar;

public class Zeitraum {
	public GregorianCalendar beginn;
	public GregorianCalendar ende;
	
	public Zeitraum (GregorianCalendar beginn, GregorianCalendar ende){
		this.beginn = beginn;
		this.ende = ende; 
	}
	/**
	 * Erzeugt einen Zeitraum von Beginn des Beginn-Jahres bis Ende des Ende-Jahres
	 */
	public Zeitraum (int jahrBeginn, int jahrEnde) {
		this.beginn = DateUtil.createGregorianCalendar(01, 01, jahrBeginn);
		this.ende = DateUtil.createGregorianCalendar(31, 12, jahrEnde);
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
	public String toStringJahre () {
		return (DateUtil.getJahr(this.beginn) + "-" + DateUtil.getJahr(this.ende));
	}
}
	
