package com.algotrading.signal;

import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

/**
 * Speichert das Ergebnis der Bewertung, wie gut ein Signal einen Kurs prognostizieren kann 
 * Die Berechnung findet an der Aktie statt. 
 * @author oskar
 *
 */
public class SignalBewertung {
	
	// der Zeithorizont in Tagen
	private int tage;
	
	private Zeitraum zeitraum; 
	
	private SignalBeschreibung sB; 
	
	public int getTage() {
		return tage;
	}
	SignalBewertung(SignalBeschreibung sB) {
		this.sB = sB; 
	}
	
	public int kauf; // Anzahl Kauf-Signale
	public float kaufKorrekt; // Anzahl korrekter Kauf-Signale im Verhältnis zu Käufen 
	public float summeBKauf; // Bewertungs-Summe als Saldo aller Kauf-Empfehlungen
	public float summeSKauf; // die Summe aller Kauf-Prognosen
	
	public int verkauf; // Anzahl Verkauf-Signale
	public float verkaufKorrekt; // Anzahl korrekter Verkauf-Signale im Verhältnis zu Verkäufen 
	public float summeBVerkauf; // Bewertungs-Summe als Saldo aller Verkauf-Empfehlungen
	public float summeSVerkauf;// die Summe aller Verkauf-Empfehlungen
	
	public float summeBewertungen; // Bewertungs-Summe als Saldo positiver und negativer Prognosequalität. 
	
	public String toString () {
		return this.sB + " Kauf:" + kauf + 
				" korrekt:" + Util.rundeBetrag(kaufKorrekt, 3) + 
				" Signal:" + Util.rundeBetrag(summeSKauf,3) + 
				" Bewertung:" + Util.rundeBetrag(summeBKauf,3) + 
				" Verkauf:" + verkauf + 
				" korrekt:" + Util.rundeBetrag(verkaufKorrekt, 3) + 
				" Signal:" + Util.rundeBetrag(summeSVerkauf,3) +
				" Bewertung:" + Util.rundeBetrag(summeBVerkauf,3) +
				" BewSumme:" + Util.rundeBetrag(summeBewertungen,3);
	}
	public Zeitraum getZeitraum() {
		return zeitraum;
	}
	public void setZeitraum(Zeitraum zeitraum) {
		this.zeitraum = zeitraum;
	}
	public void setTage(int tage) {
		this.tage = tage;
	}

}
