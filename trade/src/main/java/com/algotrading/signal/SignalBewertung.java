package com.algotrading.signal;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorAlgorithmusDAO;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

/**
 * Speichert das Ergebnis der Bewertung, wie gut ein Signal einen Kurs prognostizieren kann 
 * Die Berechnung findet an der Aktie statt. 
 * Wird über JPA persistiert
 * @author oskar
 *
 */
@Entity
@Table( name = "SIGNALBEWERTUNGEN" )
public class SignalBewertung {
	
	// der Zeithorizont in Tagen
	@Column(name = "tage")
	private int tage;

	private String aktieName; 
	
	private String ISIN;
	
	@Column(name = "SAName")
	private String SAName;
	
	@OneToMany(cascade = CascadeType.ALL)  
	@JoinColumn(name="bewertungID")  
	@OrderColumn(name="type")  
	private List<IndikatorAlgorithmusDAO> indikatorAlgorithmenDAO;
	
	@Transient  // die Original-Indikator-Objekte werden nicht persistiert
	private List<IndikatorAlgorithmus> indikatorAlgorithmen;
	
	@Transient
	private Zeitraum zeitraum; 
	private GregorianCalendar zeitraumBeginn; 
	private GregorianCalendar zeitraumEnde; 
	@Transient
	private SignalAlgorithmus sA; 
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)  
	private Long id;

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
	
	protected SignalBewertung() {}  // für JPA
	
	/**
	 * Bei der Erzeugung wird der zugehörige SignalAlgorithmus gesetzt
	 * Damit können Informationen aus der Aktie geholt werden 
	 */
	SignalBewertung(SignalAlgorithmus sA) {
		this.sA = sA; 
	}
	/**
	 * sorgt für die Aktualisierung aller Daten vor der Persistierung 
	 */
	public void synchronize () {
		this.setAktieName();
		this.setISIN();
		this.setIndikatorAlgorithmen();
		this.setZeitraumBeginn();
		this.setZeitraumEnde();
		this.setSAName();
		
		// für jeden IndikatorAlgorithmus ein DAO-Objekt erzeugen. 
		this.indikatorAlgorithmenDAO = new ArrayList<IndikatorAlgorithmusDAO>();
		for (IndikatorAlgorithmus iA : this.indikatorAlgorithmen) {
			this.indikatorAlgorithmenDAO.add(new IndikatorAlgorithmusDAO(iA));
		}
		
	}
	
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
	public Long getId() {
		return id;
	}
	public void setTage(int tage) {
		this.tage = tage;
	}
	public int getTage() {
		return tage;
	}
	private void setZeitraumBeginn () {
		this.zeitraumBeginn = this.zeitraum.beginn;
	}
	private void setZeitraumEnde () {
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

	public SignalAlgorithmus getsA() {
		return sA;
	}

	public void setsA(SignalAlgorithmus sA) {
		this.sA = sA;
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
		this.aktieName = this.sA.getAktie().getName();
	}
	/*
	 * Verwendung zur Reproduktion der Bewertung aus der DB
	 */
	public void setAktieName(String aktieName) {
		this.aktieName = aktieName;
	}
	

	public Zeitraum getZeitraum() {
		return zeitraum;
	}

	public void setZeitraum(Zeitraum zeitraum) {
		this.zeitraum = zeitraum;
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
	private void setISIN () {
		ISIN = this.sA.getAktie().getISIN();
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
		SAName = this.sA.getKurzname();
	}

	public List<IndikatorAlgorithmus> getIndikatorAlgorithmen() {
		return indikatorAlgorithmen;
	}

	public void setIndikatorAlgorithmen(List<IndikatorAlgorithmus> indikatorAlgorithmen) {
		this.indikatorAlgorithmen = indikatorAlgorithmen;
	}
	/*
	 * die Liste der verwendeten IndikatorAlgos werden über die Referenz zur Aktie gesetzt 
	 */
	private void setIndikatorAlgorithmen() {
		this.indikatorAlgorithmen = this.sA.getAktie().getIndikatorAlgorithmen();
	}

}
