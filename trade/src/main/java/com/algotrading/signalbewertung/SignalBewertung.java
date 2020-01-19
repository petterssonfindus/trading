package com.algotrading.signalbewertung;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.aktie.Aktie;
import com.algotrading.component.AktieVerwaltung;
import com.algotrading.component.SignalVerwaltung;
import com.algotrading.depot.Order;
import com.algotrading.signal.Signal;
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
@Service
@Entity
@Table(name = "SIGNALBEWERTUNG")
public class SignalBewertung {
	@Transient
	private static final Logger log = LogManager.getLogger(SignalBewertung.class);

	// der Zeithorizont in Tagen
	@Column(name = "tage")
	private int tage;

	private String aktieName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Aktie aktie;

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
	public float summePKauf; // Bewertungs-Summe als Saldo aller Kauf-Empfehlungen
	public float summeSKauf; // die Summe aller Kauf-Prognosen
	private float performanceProKauf;
	private float performanceKaufDelta;

	public int verkauf; // Anzahl Verkauf-Signale
	public float verkaufKorrekt; // Anzahl korrekter Verkauf-Signale im Verhältnis zu Verkäufen
	public float summePVerkauf; // Bewertungs-Summe als Saldo aller Verkauf-Empfehlungen
	public float summeSVerkauf;// die Summe aller Verkauf-Empfehlungen
	private float performanceProVerkauf;
	private float performanceVerkaufDelta;

	public float performance; // die Performance des Kurses im betrachteten Zeitraum

	@Transient
	@Autowired
	private SignalVerwaltung sV;

	@Transient
	@Autowired
	private AktieVerwaltung aV;

	protected SignalBewertung() {
	} // für JPA

	/**
	 * Bei der Erzeugung wird der zugehörige SignalAlgorithmus gesetzt Damit können
	 * Informationen aus der Aktie geholt werden Die Indikator-Algorithmen werden
	 * aus der Aktie geholt
	 */
	public SignalBewertung(SignalAlgorithmus sA) {
		this.setaV(sA.getaV());
		this.signalAlgorithmus = sA;
		this.aktieName = sA.getAktie().getName();
		this.aktie = sA.getAktie();
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
		if (!floatEquals(this.summePKauf, sB.summePKauf))
			return false;
		if (!floatEquals(this.summePVerkauf, sB.summePVerkauf))
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

	/**
	 * Bewertet alle Signale und schreibt das Ergebnis in die Datenbank Signalstärke
	 * * Erfolg (Performance) Signalstärke ist positiv für Kauf-_Signale - negativ
	 * für Verkauf-Signale Erfolg ist positiv bei steigenden Kursen - negativ bei
	 * fallenden Kursen. ==> Hohe positive Werte bedeuten gute Prognose-Qualität bei
	 * Kauf und Verkauf ! ==> Hohe negative Wert bedeuten entgegen gesetzte
	 * Prognose-Qualität Prognose-Quantität: wie viele Signale gehen in die
	 * erwartete Richtung. Prognose-Qualität:
	 * 
	 * @param zeitraum   der Zeitraum in dem die signale auftreten Wenn null, dann
	 *                   maximaler Zeitraum, für den Signale vorliegen.
	 * @param tageVoraus für die Erfolgsmessung in die Zukunft
	 */
	public SignalBewertung bewerteSignale(Zeitraum zeitraum, int tage) {
		this.setTage(tage);
		if (zeitraum == null) {
			// maximaler Zeitraum ermitteln
			zeitraum = sV.getZeitraumSignale(this.getSignalAlgorithmus());
		}
		this.setZeitraum(zeitraum);
		// alle zugehörigen Signale
		// TODO: hier könnte man den Zeitraum bereits berücksichtigen
		List<Signal> signale = this.getSignalAlgorithmus().getSignale();

		this.performance = aV.rechnePerformance(this.getSignalAlgorithmus().getAktie(), zeitraum);
		float staerke = 0; // die Signal-Stärke eines einzelnen Signals
		int kaufKorrekt = 0;
		int verkaufKorrekt = 0;

		// für alle Signale zu dieser SignalBeschreibung
		for (Signal signal : signale) {
			// Signale im vorgegebenen Zeitraum filtern
			if (!DateUtil.istInZeitraum(signal.getKurs().getDatum(), zeitraum))
				continue;
			// die Bewertung wird am Signal ermittelt
			Float performanceFloat = signal.getPerformance(tage);
			// wenn Performance nicht berechnet werden kann, z.B. am Ende der Laufzeit
			if (performanceFloat == null)
				continue;
			float p = performanceFloat;
			if (signal.getKaufVerkauf() == Order.KAUF) {
				this.kauf++;
				this.summePKauf += p;
				this.summeSKauf += signal.getStaerke();
				if (p > this.performance)
					kaufKorrekt++;
			} else {
				this.verkauf++;
				this.summePVerkauf += p;
				this.summeSVerkauf += signal.getStaerke();
				// die tatsächliche Wertentwicklung ist geringer als der Durchschnitt  
				if (p < this.performance)
					verkaufKorrekt++;
			}
		}

		// Aufbereitung der Gesamt-Ergebnisse
		// Prüfung auf Division 0, da ansonsten NaN entsteht
		if (this.kauf > 0) {
			this.kaufKorrekt = (float) ((double) kaufKorrekt / this.kauf);
			this.performanceProKauf = (float) ((double) summePKauf / this.kauf);
			this.performanceKaufDelta = this.performanceProKauf - this.performance;
		}
		if (this.verkauf > 0) {
			this.verkaufKorrekt = (float) ((double) verkaufKorrekt / this.verkauf);
			this.performanceProVerkauf = (float) ((double) summePVerkauf / this.verkauf);
			this.performanceVerkaufDelta = this.performanceProVerkauf - this.performance;
		}

		// @formatter:off
		System.out.printf("%s%s%s%s%s%s%s", 
				this.getSignalAlgorithmus().getAktie().getName(),
				" B:",
				tage,
				Util.separatorCSV,
				zeitraum.toStringJahre(),
				Util.separatorCSV,
				this.toString());
		// @formatter:on
		return this;
	}

//	@formatter:off
	public String toString() {
		return this.signalAlgorithmus + 
				" Kauf:" + kauf + 
				" korrekt:" + Util.rundeBetrag(kaufKorrekt, 3) + 
				" Signal:" + Util.rundeBetrag(summeSKauf, 3) + 
				" Bewertung:" + Util.rundeBetrag(summePKauf, 3) + 
				" Verkauf:" + verkauf + 
				" korrekt:" + Util.rundeBetrag(verkaufKorrekt,3) + 
				" Signal:" + Util.rundeBetrag(summeSVerkauf, 3) + 
				" Bewertung:" + Util.rundeBetrag(summePVerkauf, 3) + 
				" Performance:" + Util.rundeBetrag(performance, 3);
	}
//	@formatter:on

	/**
	 * Kauf, KaufKorrekt, SummeSignalKauf, Bewertung, 
	 * Verkauf, VerkaufKorrekt, SummeSignalVerkauf, Bewertung, 
	 * BewertungSumme, Performance
	 */

	public String toCSVString() {
		StringBuilder sB = new StringBuilder();
		sB.append(DateUtil.formatDate(this.zeitraumBeginn) + Util.separatorCSV);
		sB.append(DateUtil.formatDate(this.zeitraumEnde) + Util.separatorCSV);
		sB.append(tage + Util.separatorCSV);
		sB.append(kauf + Util.separatorCSV);
		sB.append(Util.toStringExcel(kaufKorrekt, 3) + Util.separatorCSV);
		sB.append(Util.toStringExcel(summePKauf, 3) + Util.separatorCSV);
		sB.append(Util.toStringExcel(performanceProKauf, 3) + Util.separatorCSV);
		sB.append(Util.toStringExcel(performanceKaufDelta, 3) + Util.separatorCSV);
		sB.append(verkauf + Util.separatorCSV);
		sB.append(Util.toStringExcel(verkaufKorrekt, 3) + Util.separatorCSV);
		sB.append(Util.toStringExcel(summePVerkauf, 3) + Util.separatorCSV);
		sB.append(Util.toStringExcel(performanceProVerkauf, 3) + Util.separatorCSV);
		sB.append(Util.toStringExcel(performanceVerkaufDelta, 3) + Util.separatorCSV);
		sB.append(Util.toStringExcel(performance, 3) + Util.separatorCSV);
		sB.append(this.signalAlgorithmus.toString() + Util.separatorCSV);
		return sB.toString();
	}

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
		return summePKauf;
	}

	public void setSummeBKauf(float summeBKauf) {
		this.summePKauf = summeBKauf;
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
		return summePVerkauf;
	}

	public void setSummeBVerkauf(float summeBVerkauf) {
		this.summePVerkauf = summeBVerkauf;
	}

	public float getSummeSVerkauf() {
		return summeSVerkauf;
	}

	public void setSummeSVerkauf(float summeSVerkauf) {
		this.summeSVerkauf = summeSVerkauf;
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

	public void setaV(AktieVerwaltung aV) {
		this.aV = aV;
	}

	public float getPerformanceProKauf() {
		return performanceProKauf;
	}

	protected void setPerformanceProKauf(float performanceProKauf) {
		this.performanceProKauf = performanceProKauf;
	}

	public float getPerformanceProVerkauf() {
		return performanceProVerkauf;
	}

	protected void setPerformanceProVerkauf(float performanceProVerkauf) {
		this.performanceProVerkauf = performanceProVerkauf;
	}

	public float getPerformanceKaufDelta() {
		return performanceKaufDelta;
	}

	protected void setPerformanceKaufDelta(float performanceKaufDelta) {
		this.performanceKaufDelta = performanceKaufDelta;
	}

	public float getPerformanceVerkaufDelta() {
		return performanceVerkaufDelta;
	}

	protected void setPerformanceVerkaufDelta(float performanceVerkaufDelta) {
		this.performanceVerkaufDelta = performanceVerkaufDelta;
	}

}
