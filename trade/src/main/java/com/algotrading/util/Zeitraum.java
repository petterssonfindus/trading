package com.algotrading.util;

import java.util.GregorianCalendar;

public class Zeitraum {
	public GregorianCalendar beginn;
	public GregorianCalendar ende;
	public String zeitraumString;

	public Zeitraum(GregorianCalendar beginn, GregorianCalendar ende) {
		this.beginn = beginn;
		this.ende = ende;
		this.zeitraumString = toString();
	}

	/**
	 * Erzeugt einen Zeitraum von Beginn des Beginn-Jahres bis Ende des Ende-Jahres
	 */
	public Zeitraum(int jahrBeginn, int jahrEnde) {
		this.beginn = DateUtil.createGregorianCalendar(01, 01, jahrBeginn);
		this.ende = DateUtil.createGregorianCalendar(31, 12, jahrEnde);
		this.zeitraumString = toString();
	}

	/**
	 * Anzahl der Handelstage innerhalb des Zeitraums Geht von 5 Handelstage pro
	 * Woche aus
	 */
	public int getHandestage() {
		int tage = DateUtil.anzahlKalenderTage(beginn, ende);
		return (int) (tage * 0.685f);
	}

	public boolean equals(Object input) {
		Zeitraum inputZS = (Zeitraum) input;
		if (DateUtil.istGleicherKalendertag(inputZS.beginn, this.beginn) && DateUtil
				.istGleicherKalendertag(inputZS.ende, this.ende)) {
			return true;
		}
		return false;
	}

	public String toString() {
		return ("Beginn: " + DateUtil.formatDate(beginn) + " Ende: " + DateUtil.formatDate(ende));
	}

	public String toStringJahre() {
		return (DateUtil.getJahr(this.beginn) + "-" + DateUtil.getJahr(this.ende));
	}

	public GregorianCalendar getBeginn() {
		return beginn;
	}

	public GregorianCalendar getEnde() {
		return ende;
	}
}
