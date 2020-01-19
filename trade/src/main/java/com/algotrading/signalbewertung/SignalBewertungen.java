package com.algotrading.signalbewertung;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.uimodel.UIFileText;
import com.algotrading.util.FileUtil;
import com.algotrading.util.Util;

/**
 * SignalBewertungen sind eine beliebige Sammlung von SignalBewertung-en.
 * Sie können zusammen gestellt werden nach unterschiedlichen 
 * 		Laufzeiten, TagenBewertungs-Tage, Signal-Algorithmus-Parametern, Schwellwerten. 
 * Es gibt die Möglichkeit zur Gruppierung, zur Summenbildung nach verschiedenen Kriterien 
 * Persistiert die Gesamt-Ergebnisse. 
 * Die Gruppen-Ergebnisse werden bei Bedarf ermittelt. 
 *
 */
// @formatter:off
@NamedEntityGraph(name = "SignalBewertungen.signalBewertung", attributeNodes = @NamedAttributeNode("signalBewertung"))
// @formatter:on
@Entity
@Table(name = "SIGNALBEWERTUNGEN")
public class SignalBewertungen {

	private static final Logger log = LogManager.getLogger(SignalBewertungen.class);
	@Transient
	private static final short GRUPPIERUNG_LAUFZEIT = 1;
	@Transient
	private static final short GRUPPIERUNG_BEWERTUNGTAGE = 2;
	@Transient
	private static final short GRUPPIERUNG_S_ALGO_PARAMETER = 3;  // Signal-Algorithmus-Parameter

	private short gruppierung;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	// Liste aller SignalBewertungen
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "signalbewertung_id")
	private List<SignalBewertung> signalBewertung = new ArrayList<SignalBewertung>();

	private float kaeufe;
	private float verkaeufe;
	private float kaeufeKorrekt;
	private float verkaeufeKorrekt;
	private float performanceProKauf;
	private float performanceProVerkauf;
	private float kaeufePerformanceDelta;
	private float verkaeufePerformanceDelta;
	private float gesamtPerformance;

	public void calculate() {
		int summeKaeufe = 0;
		int summeVerkaeufe = 0;
		float summeKaeufeKorrekt = 0;
		float summeVerkaeufeKorrekt = 0;
		float summePerformanceProKauf = 0;
		float summePerformanceProVerkauf = 0;
		float summeKaeufePerformanceDelta = 0;
		float summeVerkaeufePerformanceDelta = 0;
		float summeGesamtPerformance = 0;
		int anzahl = this.getSignalBewertungen().size();
		for (SignalBewertung sB : this.getSignalBewertungen()) {
			summeKaeufe += sB.getKauf();
			summeVerkaeufe += sB.getVerkauf();
			summeKaeufeKorrekt += sB.getKaufKorrekt();
			summeVerkaeufeKorrekt += sB.getVerkaufKorrekt();
			summePerformanceProKauf += sB.getPerformanceProKauf();
			summePerformanceProVerkauf += sB.getPerformanceProVerkauf();
			summeKaeufePerformanceDelta += sB.getPerformanceKaufDelta();
			summeVerkaeufePerformanceDelta += sB.getPerformanceVerkaufDelta();
			summeGesamtPerformance += sB.getPerformance();
		}
		this.kaeufe = (float) (summeKaeufe / (double) anzahl);
		this.verkaeufe = (float) (summeVerkaeufe / (double) anzahl);
		this.kaeufeKorrekt = (float) (summeKaeufeKorrekt / (double) anzahl);
		this.verkaeufeKorrekt = (float) (summeVerkaeufeKorrekt / (double) anzahl);
		this.performanceProKauf = (float) (summePerformanceProKauf / (double) anzahl);
		this.performanceProVerkauf = (float) (summePerformanceProVerkauf / (double) anzahl);
		this.kaeufePerformanceDelta = (float) (summeKaeufePerformanceDelta / (double) anzahl);
		this.verkaeufePerformanceDelta = (float) (summeVerkaeufePerformanceDelta / (double) anzahl);
		this.gesamtPerformance = (float) (summeGesamtPerformance / (double) anzahl);
	}

	public SignalBewertungen() {

	}

	public SignalBewertungen(short gruppierung) {
		this.gruppierung = gruppierung;
	}

	public Long getId() {
		return id;
	}

	public List<SignalBewertung> getSignalBewertungen() {
		return signalBewertung;
	}

	/**
	 * Fügt eine Liste von Bewertungen hinzu
	 * Berechnet anschließend die eigenen Bewertungsparameter
	 */
	public void addSignalBewertungen(List<SignalBewertung> bewertungen) {
		this.signalBewertung.addAll(bewertungen);
		this.calculate();
	}

	/**
	 * schreibt eine neue Datei mit Kursen, Indikatoren, Signalen, SignalPerformance
	 */
	public UIFileText writeFileSignalBewertungen(long id) {
		String dateiname = String.format("signalbew%s-%s", this.getAktieName(), id);
		List<String> zeilen = this.writeBewertungen();
		File file = FileUtil.writeCSVFile(zeilen, dateiname);
		UIFileText result = new UIFileText(file.getAbsolutePath(), zeilen);
		log.info("Datei Bewertungen geschrieben: " + dateiname);
		return result;
	}

	/**
	 * gibt den Inhalt der csv als Ltring-Liste
	 */
	public List<String> writeStringSignalBewertungen(long id) {
		return this.writeBewertungen();
	}

	/**
	 * sortiert eine Liste von SignalBewertungen nach vorgegebener Gruppierung 
	 */
	private static List<SignalBewertung> group(List<SignalBewertung> liste, short gruppierung) {
		List<SignalBewertung> result = new ArrayList<SignalBewertung>();
		if (gruppierung == GRUPPIERUNG_BEWERTUNGTAGE) {
			for (SignalBewertung sB : liste) {

			}
		}
		return result;
	}

	/**
	 * schreibt pro SignalBewertung eine Zeile
	 * Gruppiert anhand der vorgegebenen Gruppierung
	 */
	private List<String> writeBewertungen() {
		List<String> zeilen = new ArrayList<>();
		// Header-Zeile
		zeilen.add(toStringSignalBewertungHeader(this.signalBewertung.get(0)));
		// für jeden Kurs eine Zeile
		for (SignalBewertung sB : this.getSignalBewertungen()) {
			zeilen.add(sB.toCSVString());
		}
		// die SummenZeile
		zeilen.add(this.toStringSignalBewertungen(this.signalBewertung.get(0)));
		return zeilen;
	}

	private String toStringSignalBewertungHeader(SignalBewertung signalBewertung) {
		String parameter = signalBewertung.getSignalAlgorithmus().toStringParameter();
//		@formatter:off
		
		StringBuilder sB = new StringBuilder();
		sB.append("Beginn" + Util.separatorCSV);
		sB.append("Ende" + Util.separatorCSV);
		sB.append("Tage" + Util.separatorCSV);
		sB.append("Käufe" + Util.separatorCSV);
		sB.append("Käufe korrekt" + Util.separatorCSV);
		sB.append("SummePerformanceKäufe" + Util.separatorCSV);
		sB.append("PerformanceProKauf" + Util.separatorCSV);
		sB.append("PerformanceKaufDelta" + Util.separatorCSV);
		sB.append("Verkäufe" + Util.separatorCSV);
		sB.append("Verkäufe korrekt" + Util.separatorCSV);
		sB.append("SummePerformanceVerkäufe" + Util.separatorCSV);
		sB.append("PerformanceProVerkauf" + Util.separatorCSV);
		sB.append("PerformanceVerkaufDelta" + Util.separatorCSV);
		sB.append("GesamtPerformance" + Util.separatorCSV);
		sB.append("SignalAlgo" + Util.separatorCSV);
		sB.append(parameter + Util.separatorCSV);
		return sB.toString();
	}

	private String toStringSignalBewertungen(SignalBewertung signalBewertung) {
		String parameter = signalBewertung.getSignalAlgorithmus().toStringParameter();
		StringBuilder sB = new StringBuilder();
		
		sB.append(signalBewertung.getSignalAlgorithmus().toString()+ Util.separatorCSV);
		sB.append(Util.separatorCSV);
		sB.append(Util.separatorCSV);  
		sB.append(Util.separatorCSV);
		sB.append(Util.toStringExcel(this.kaeufe,3) + Util.separatorCSV);
		sB.append(Util.toStringExcel(this.kaeufeKorrekt,3)+ Util.separatorCSV);
		sB.append(Util.separatorCSV);
		sB.append(Util.toStringExcel(this.performanceProKauf, 3)+ Util.separatorCSV);
		sB.append(Util.toStringExcel(this.kaeufePerformanceDelta) + Util.separatorCSV);
    	sB.append(Util.toStringExcel(this.verkaeufe,3) + Util.separatorCSV);
		sB.append(Util.toStringExcel(this.verkaeufeKorrekt,3)+ Util.separatorCSV);
		sB.append(Util.separatorCSV);
		sB.append(Util.toStringExcel(this.performanceProVerkauf, 3)+ Util.separatorCSV);
		sB.append(Util.toStringExcel(this.verkaeufePerformanceDelta) + Util.separatorCSV);
		sB.append("GesamtPerformance" + Util.separatorCSV);
		sB.append(parameter + Util.separatorCSV);
		return sB.toString();
	}

	public String getAktieName() {
		if (this.getSignalBewertungen() != null && !this.getSignalBewertungen().isEmpty()) {
			return this.getSignalBewertungen().get(0).getAktieName();
		}
		return null;
	}

	public static Logger getLog() {
		return log;
	}

	public static short getGruppierungLaufzeit() {
		return GRUPPIERUNG_LAUFZEIT;
	}

	public static short getGruppierungBewertungtage() {
		return GRUPPIERUNG_BEWERTUNGTAGE;
	}

	public static short getGruppierungSAlgoParameter() {
		return GRUPPIERUNG_S_ALGO_PARAMETER;
	}

	public short getGruppierung() {
		return gruppierung;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public List<SignalBewertung> getSignalBewertung() {
		return signalBewertung;
	}

	public float getKaeufe() {
		return kaeufe;
	}

	public float getVerkaeufe() {
		return verkaeufe;
	}

	public float getKaeufeKorrekt() {
		return kaeufeKorrekt;
	}

	public float getVerkaeufeKorrekt() {
		return verkaeufeKorrekt;
	}

	public float getPerformanceProKauf() {
		return performanceProKauf;
	}

	public float getPerformanceProVerkauf() {
		return performanceProVerkauf;
	}

	public float getKaeufePerformanceDelta() {
		return kaeufePerformanceDelta;
	}

	public float getVerkaeufePerformanceDelta() {
		return verkaeufePerformanceDelta;
	}

	public float getGesamtPerformance() {
		return gesamtPerformance;
	}

}
