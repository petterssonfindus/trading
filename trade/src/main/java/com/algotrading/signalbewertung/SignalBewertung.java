package com.algotrading.signalbewertung;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.algotrading.signal.SignalAlgorithmus;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

/**
 * Speichert das Ergebnis der Bewertung, wie gut ein Signal einen Kurs
 * prognostizieren kann Die Berechnung findet an der Aktie statt. Wird über JPA
 * persistiert
 * 
 * @author oskar
 *
 */

@Entity
@Table(name = "SIGNALBEWERTUNG")
public class SignalBewertung {

	// der Zeithorizont in Tagen
	@Column(name = "tage")
	private int tage;

	private String aktieName;

	private String ISIN;

	@Column(name = "SAName")
	private String SAName;

	@JoinColumn(name = "signalAlgorithmus_ID")
	@OneToOne(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private SignalAlgorithmus signalAlgorithmus;

	@Transient
	private Zeitraum zeitraum;
	private GregorianCalendar zeitraumBeginn;
	private GregorianCalendar zeitraumEnde;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

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

	protected SignalBewertung() {
	} // für JPA

	/**
	 * Bei der Erzeugung wird der zugehörige SignalAlgorithmus gesetzt Damit können
	 * Informationen aus der Aktie geholt werden Die Indikator-Algorithmen werden
	 * aus der Aktie geholt
	 */
	public SignalBewertung(SignalAlgorithmus sA) {
		this.signalAlgorithmus = sA;
		this.aktieName = sA.getAktie().getName();
		this.ISIN = sA.getAktie().getISIN();
	}

	@PrePersist
	public void generateID() {

	}

	/**
	 * erzeugt aus den Parametern mit UUID-Referenz echte Objekte durch Nach-Lesen
	 */
	public void instanziiereParameter() {
		SignalAlgorithmus sA = this.getSignalAlgorithmus();
	}

	/**
	 * alle Parameter müssen gleich sein und die SignalAlgorithmusParameter
	 */
	public boolean equals(SignalBewertung sB) {
		// erst die eigenen Parameter vergleichen
		if (!this.aktieName.matches(sB.aktieName))
			return false;
		if (this.kauf != sB.kauf)
			return false;
		if (this.verkauf != sB.verkauf)
			return false;
		if (!floatEquals(this.kaufKorrekt, sB.kaufKorrekt))
			return false;
		if (!floatEquals(this.verkaufKorrekt, sB.verkaufKorrekt))
			return false;
		if (!floatEquals(this.performance, sB.performance))
			return false;
		if (!floatEquals(this.summeBewertungen, sB.summeBewertungen))
			return false;
		if (!floatEquals(this.summeBKauf, sB.summeBKauf))
			return false;
		if (!floatEquals(this.summeBVerkauf, sB.summeBVerkauf))
			return false;
		if (!floatEquals(this.summeSKauf, sB.summeSKauf))
			return false;
		if (!floatEquals(this.summeSVerkauf, sB.summeSVerkauf))
			return false;
		if (tage != sB.tage)
			return false;
		if (!this.getZeitraum().equals(sB.getZeitraum()))
			return false;
		// SignalAlgorithmus muss gleich sein
		if (!this.signalAlgorithmus.equals(sB.getSignalAlgorithmus()))
			return false;
		// IndikatorAlgorithmen werden nicht verglichen
		// über die Parameter der SignalAlgorithmen werden die IAs implizit verglichen
		// 		if (! this.equalsIndikatoren(sB.indikatorAlgorithmen)) return false; 
		return true;
	}

	/**
	 * die beiden float-Werte sind gleich, wenn sie innerhalb einer Bandbreite von X
	 * % sind
	 * 
	 * @param para1
	 * @param para2
	 * @return
	 */
	private boolean floatEquals(float para1, float para2) {
		if (para1 == para2)
			return true;
		boolean result = false;
		float test1;
		float test2;
		float testAbs = 0.05f * Math.abs(para1);
		float bandbreite = 0.05f * testAbs;
		if (para1 > para2) { // para1 ist > als para2
			test1 = para1 - bandbreite; // para1 reduzieren um bandbreite
			if (test1 < para2)
				result = true; // ist para1 jetzt kleiner ?
		} else {
			test1 = para1 + bandbreite;
			if (test1 > para2)
				result = true;
		}
		return result;
	}

//	@formatter:off
	public String toString() {
		return this.signalAlgorithmus + " Kauf:" + kauf + " korrekt:" + Util
				.rundeBetrag(kaufKorrekt, 3) + " Signal:" + Util.rundeBetrag(summeSKauf, 3) + " Bewertung:" + Util
						.rundeBetrag(summeBKauf, 3) + " Verkauf:" + verkauf + " korrekt:" + Util.rundeBetrag(
								verkaufKorrekt,
								3) + " Signal:" + Util.rundeBetrag(summeSVerkauf, 3) + " Bewertung:" + Util
										.rundeBetrag(summeBVerkauf, 3) + " BewSumme:" + Util.rundeBetrag(
												summeBewertungen,
												3) + " Performance:" + Util.rundeBetrag(performance, 3);
	}

	/**
	 * Kauf, KaufKorrekt, SummeSignalKauf, Bewertung, 
	 * Verkauf, VerkaufKorrekt, SummeSignalVerkauf, Bewertung, 
	 * BewertungSumme, Performance
	 */
//	@formatter:off
	public String toCSVString() {
		return this.signalAlgorithmus.toString() + Util.separatorCSV
				.concat(DateUtil.formatDate(this.zeitraumBeginn) + Util.separatorCSV)
				.concat(DateUtil.formatDate(this.zeitraumEnde) + Util.separatorCSV)
				.concat(tage + Util.separatorCSV)
				.concat(kauf + Util.separatorCSV)
				.concat(Util.toString(Util.rundeBetrag(kaufKorrekt, 3)) + Util.separatorCSV)
				.concat(Util.toString(Util.rundeBetrag(summeSKauf, 3)) + Util.separatorCSV)
				.concat(Util.toString(Util.rundeBetrag(summeBKauf, 3)) + Util.separatorCSV) 
				.concat(verkauf + Util.separatorCSV) 
				.concat(Util.toString(Util.rundeBetrag(verkaufKorrekt,3)) + Util.separatorCSV) 
				.concat(Util.toString(Util.rundeBetrag(summeSVerkauf, 3)) + Util.separatorCSV) 
				.concat(Util.toString(Util.rundeBetrag(summeBVerkauf, 3)) + Util.separatorCSV) 
				.concat(Util.toString(Util.rundeBetrag(summeBewertungen,3)) + Util.separatorCSV) 
				.concat(Util.toString(Util.rundeBetrag(performance, 3)) + Util.separatorCSV);
	}
//	@formatter:on

	private String toCSVStringKauf() {
		return " Kauf:" + kauf + Util.separatorCSV;
	}

	public Long getId() {
		return id;
	}

	public void setTage(int tage) {
		this.tage = tage;
	}

	public int getTage() {
		return tage;
	}

	private void setZeitraumBeginn() {
		this.zeitraumBeginn = this.zeitraum.beginn;
	}

	private void setZeitraumEnde() {
		this.zeitraumEnde = this.zeitraum.ende;
	}

	public GregorianCalendar getZeitraumBeginn() {
		return zeitraumBeginn;
	}

	public void setZeitraumBeginn(GregorianCalendar zeitraumBeginn) {
		this.zeitraumBeginn = zeitraumBeginn;
	}

	public GregorianCalendar getZeitraumEnde() {
		return zeitraumEnde;
	}

	public void setZeitraumEnde(GregorianCalendar zeitraumEnde) {
		this.zeitraumEnde = zeitraumEnde;
	}

	public int getKauf() {
		return kauf;
	}

	public void setKauf(int kauf) {
		this.kauf = kauf;
	}

	public float getKaufKorrekt() {
		return kaufKorrekt;
	}

	public void setKaufKorrekt(float kaufKorrekt) {
		this.kaufKorrekt = kaufKorrekt;
	}

	public float getSummeBKauf() {
		return summeBKauf;
	}

	public void setSummeBKauf(float summeBKauf) {
		this.summeBKauf = summeBKauf;
	}

	public float getSummeSKauf() {
		return summeSKauf;
	}

	public void setSummeSKauf(float summeSKauf) {
		this.summeSKauf = summeSKauf;
	}

	public int getVerkauf() {
		return verkauf;
	}

	public void setVerkauf(int verkauf) {
		this.verkauf = verkauf;
	}

	public float getVerkaufKorrekt() {
		return verkaufKorrekt;
	}

	public void setVerkaufKorrekt(float verkaufKorrekt) {
		this.verkaufKorrekt = verkaufKorrekt;
	}

	public float getSummeBVerkauf() {
		return summeBVerkauf;
	}

	public void setSummeBVerkauf(float summeBVerkauf) {
		this.summeBVerkauf = summeBVerkauf;
	}

	public float getSummeSVerkauf() {
		return summeSVerkauf;
	}

	public void setSummeSVerkauf(float summeSVerkauf) {
		this.summeSVerkauf = summeSVerkauf;
	}

	public float getSummeBewertungen() {
		return summeBewertungen;
	}

	public void setSummeBewertungen(float summeBewertungen) {
		this.summeBewertungen = summeBewertungen;
	}

	public float getPerformance() {
		return performance;
	}

	public void setPerformance(float performance) {
		this.performance = performance;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAktieName() {
		return aktieName;
	}

	/*
	 * der Aktienname wird anhand der Referenz zum SignalAlgo und zur Aktie gesetzt
	 * Verwendung während der Simulation
	 */
	private void setAktieName() {
		this.aktieName = this.signalAlgorithmus.getAktie().getName();
	}

	/*
	 * Verwendung zur Reproduktion der Bewertung aus der DB
	 */
	public void setAktieName(String aktieName) {
		this.aktieName = aktieName;
	}

	public Zeitraum getZeitraum() {
		if (this.zeitraum == null) {
			this.zeitraum = new Zeitraum(this.zeitraumBeginn, this.zeitraumEnde);
		}
		return this.zeitraum;
	}

	public void setZeitraum(Zeitraum zeitraum) {
		this.zeitraum = zeitraum;
		this.zeitraumBeginn = zeitraum.getBeginn();
		this.zeitraumEnde = zeitraum.getEnde();
	}

	public String getISIN() {
		return ISIN;
	}

	public void setISIN(String iSIN) {
		ISIN = iSIN;
	}

	/*
	 * die ISIN wird anhand der Referenz zur SignalAlgo und zur Aktie gesetzt
	 */
	private void setISIN() {
		ISIN = this.signalAlgorithmus.getAktie().getISIN();
	}

	public String getSAName() {
		return SAName;
	}

	public void setSAName(String sAName) {
		SAName = sAName;
	}

	/*
	 * der SA-Name wird anhand der Referenz zum SA gesetzt.
	 */
	private void setSAName() {
		SAName = this.signalAlgorithmus.getKurzname();
	}

	public SignalAlgorithmus getSignalAlgorithmus() {
		return signalAlgorithmus;
	}

}
