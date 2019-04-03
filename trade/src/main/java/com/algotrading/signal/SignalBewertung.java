package com.algotrading.signal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

/**
 * Speichert das Ergebnis der Bewertung, wie gut ein Signal einen Kurs prognostizieren kann 
 * Die Berechnung findet an der Aktie statt. 
 * @author oskar
 *
 */
@Entity
@Table( name = "SIGNALBEWERTUNGEN" )
public class SignalBewertung {
	
	// der Zeithorizont in Tagen
	private int tage;
	
	private Zeitraum zeitraum; 
	
	private SignalAlgorithmus sA; 
	
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	private Long id;
	public Long getId() {
		return id;
	}
	
	public int getTage() {
		return tage;
	}
	
	protected SignalBewertung() {}  // für JPA
	
	SignalBewertung(SignalAlgorithmus sA) {
		this.sA = sA; 
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
	
	public float performance; // die Performance des Kurses im betrachteten Zeitraum 
	
	public String toString () {
		return this.sA + " Kauf:" + kauf + 
				" korrekt:" + Util.rundeBetrag(kaufKorrekt, 3) + 
				" Signal:" + Util.rundeBetrag(summeSKauf,3) + 
				" Bewertung:" + Util.rundeBetrag(summeBKauf,3) + 
				" Verkauf:" + verkauf + 
				" korrekt:" + Util.rundeBetrag(verkaufKorrekt, 3) + 
				" Signal:" + Util.rundeBetrag(summeSVerkauf,3) +
				" Bewertung:" + Util.rundeBetrag(summeBVerkauf,3) +
				" BewSumme:" + Util.rundeBetrag(summeBewertungen,3) + 
				" Performance:" + Util.rundeBetrag(performance,3);
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
