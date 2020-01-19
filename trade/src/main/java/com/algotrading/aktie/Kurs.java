package com.algotrading.aktie;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.component.AktieVerwaltung;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.signal.Signal;
import com.algotrading.signal.SignalAlgorithmus;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Util;

/**
 * ein Kurs gilt an einem Tag. 
 * Er enthält Kursdaten, Indikatoren, und Signale
 * Dummer Datenbehälter
 * Kursdaten stammen aus der Datenbank, Indikatoren und Signale werden berechnet. 
 * @author oskar
 *
 */
@Entity
@Service
public class Kurs {

	@Autowired
	@Transient
	private AktieVerwaltung aV;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Aktie aktie;

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@Column(name = "datum", nullable = false)
	@Temporal(TemporalType.DATE)
	private Calendar datum;

	@Transient
	public String datumString;

	@Column(name = "close")
	public float close;

	@Column(name = "open")
	public float open;

	@Column(name = "high")
	public float high;

	@Column(name = "low")
	public float low;

	@Column(name = "adjclose")
	public float adjClose;

	@Column(name = "volume")
	public int volume;

	// Map der Indikatoren, die am Kurs hängen. 
	@Transient
	private HashMap<IndikatorAlgorithmus, Float> indikatoren = new HashMap<IndikatorAlgorithmus, Float>();

	// Liste der Indikatoren in Reihenfolge des Einfügens. 
	@Transient
	private List<IndikatorAlgorithmus> indikatorenListe = new ArrayList<IndikatorAlgorithmus>();

	// Liste aller Signale - öffentlicher Zugriff nur über add() und get()
	@Transient
	private List<Signal> signale = new ArrayList<Signal>();

	@Transient
	private static final Logger log = LogManager.getLogger(Kurs.class);

	/**
	 * nur für JPA 
	 */
	protected Kurs() {
	}

	public Kurs(Aktie aktie) {
		this.aktie = aktie;
	}

	// beim Laden aus DB (ProPersist)
	@PostLoad
	public void fillDatumString() {
		this.datumString = DateUtil.formatDate(datum);
	}

	/**
	 * hängt an einen Kurs ein Signal an
	 * @param signal
	 */
	public void addSignal(Signal signal) {
		if (signal == null) {
			log.info("leeres Signal bei Kurs: " + this.toString());
		} else {
			if (this.signale == null)
				this.signale = new ArrayList<Signal>();
			this.signale.add(signal);
		}
	}

	/**
	 * Zugriff auf die Signale eines Kurses 
	 * @return eine Liste mit Signalen, oder null
	 */
	public List<Signal> getSignale() {
		return this.signale;
	}

	public Signal getSignal(SignalAlgorithmus sA) {
		if (this.signale != null) {
			for (Signal signal : this.signale) {
				if (signal.getSignalAlgorithmus().equals(sA))
					return signal;
			}
		}
		return null;
	}

	public void clearSignale() {
		this.signale = new ArrayList<Signal>();
	}

	public String getClose() {
		return Util.toStringExcel(close);
	}

	/**
	 * ein Indikator wurde berechnet und wird dem Kurs hinzugefügt
	 * Die Map dient als Assoziativer Speicher
	 * Die List als Reihenfolge
	 */
	public void addIndikator(IndikatorAlgorithmus indikator, float wert) {
		this.indikatoren.put(indikator, wert);
		this.indikatorenListe.add(indikator);
	}

	/**
	 * Ersetzt einen Indikator-Wert durch einen anderen
	 */
	public void replaceIndikatorWert(IndikatorAlgorithmus iA, float wert) {
		if (this.indikatoren.containsKey(iA)) {
			this.indikatoren.replace(iA, wert);
		}
	}

	/**
	 * Entfernt einen Indikator
	 * z.B. wenn der Indikator transformiert wird mit Standardabw. und die ersten Kurse leer bleiben. 
	 */
	public void removeIndikator(IndikatorAlgorithmus iA) {
		if (this.indikatoren.containsKey(iA))
			this.indikatoren.remove(iA);
	}

	/**
	 * ermittelt den Wert eines bestimmten Indikators dieses Kurses. 
	 * null - wenn Indikator an diesem Kurs nicht vorhanden
	 * @param indikator die Indikator-Beschreibung 
	 * @return der float-Wert zu diesem Kurs
	 */
	public Float getIndikatorWert(IndikatorAlgorithmus indikator) {
		if (this.indikatoren.containsKey(indikator)) {
			return this.indikatoren.get(indikator);
		} else {
			return null;
		}
	}

	/**
	 * gibt den Kurs eines Tages zurück - i.d.R. der Close-Kurs
	 * @return
	 */
	public float getKurs() {
		return close;
	}

	/**
	 * Ermittelt den Kurs in die Zukunft oder Vergangenheit um n-Tage
	 * wenn nicht vorhanden, dann -null-
	 * @param vorzurueck Anzahl Tage: positiv = Zukunft - negativ = Vergangenheit
	 */
	public Kurs getKursTage(AktieVerwaltung aV, int n) {
		if (aV == null)
			return null;

		List<Kurs> kurse = aV.getAktieMitKurse(this.getAktieID()).getKurse();
		// an welcher Stelle ist der aktuelle Kurs ?
		int x = kurse.indexOf(this);
		try {
			return kurse.get(x + n);
		} catch (IndexOutOfBoundsException e) {
			return null;
		}
	}

	public void setKurswert(float kurs) {
		this.close = kurs;
	}

	/**
	 * Bietet Zugriff auf die Aktie über eine Referenz (derzeit über den Namen) 
	 * @return die Aktie, zu dem der Kurs gehört 
	 */
	public Aktie getAktie() {
		return this.aktie;
	}

	public long getAktieID() {
		return this.aktie.getId();
	}

	/**
	 * das Datum mit einem GregCalendar setzen
	 */
	public void setDatum(Calendar datum) {
		this.datum = datum;
		this.datumString = DateUtil.formatDate(datum);
	}

	public GregorianCalendar getDatum() {
		GregorianCalendar result = new GregorianCalendar();
		result.setTimeInMillis(this.datum.getTimeInMillis());
		return result;
	}

	/**
	 * erzeugt einen String ohne Line-Separator
	 * Datum ; Close-Kurs ;  n-Indikatoren ; n-Signale ; n-Bewertungen
	 */
	// @formatter:off
	public String toString() {
		return String.format("%s%s%s%s%s", 
				DateUtil.formatDate(this.getDatum(), "-", true), 
				Util.separatorCSV ,
				Util.toStringExcel(close), 
				toStringIndikatoren(), 
				toStringSignale());
	}
	// @formatter:on

	/**
	 * Datum ; Close-Kurs ;  n-Indikatoren 
	 */
	// @formatter:off
	public String toStringShort() {
		return String.format("%s%s%s", 
				DateUtil.formatDate(this.getDatum(), "-", true), 
				Util.separatorCSV ,
				Util.toStringExcel(close));
	}
	// @formatter:on

	/**
	 * Liefert einen String mit allen Indikatoren im Format ';Typ - Wert'
	 * In der Reihenfolge des Einfügens der Indikatoren 
	 */
	private String toStringIndikatoren() {
		String result = "";
		for (IndikatorAlgorithmus iA : this.indikatorenListe) {
			result += String.format("%s%s", Util.separatorCSV, Util.toStringExcel(this.getIndikatorWert(iA)));
		}
		return result;
	}

	/**
	 * Liefert einen String mit allen Signalen im Format 
	 * Typ - Kauf/Verkauf - Wert
	 */
	private String toStringSignale() {
		StringBuilder sb = new StringBuilder();
		for (Signal signal : this.signale) {
			sb.append(String.format("%s%s", Util.separatorCSV, signal.toStringCSV()));
		}
		return sb.toString();
	}

	/**
	 * Signal-Erzeugung am Kurs 
	 * @param signalBeschreibung TODO
	 * @param kaufVerkauf TODO
	 * @param staerke TODO
	 */
	public Signal createSignal(SignalAlgorithmus sA, byte kaufVerkauf, float staerke) {
		Signal signal = new Signal(sA, this, kaufVerkauf, staerke);
		sA.addSignal(signal);  // das Signal hängt am Signal-Algorithmus
		addSignal(signal); // das Signal hängt am Kurs 
		log.debug("neues Signal: " + signal.toStringShort());
		return signal;
	}

	public String getAktieName() {
		return this.aktie.getName();
	}

	protected void setAktie(Aktie aktie) {
		this.aktie = aktie;
	}

}
