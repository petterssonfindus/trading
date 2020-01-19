package com.algotrading.aktie;

import java.io.File;
import java.util.ArrayList;
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
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.LazyInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.component.AktieVerwaltung;
import com.algotrading.data.DBManager;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.signal.Signal;
import com.algotrading.signal.SignalAlgorithmus;
import com.algotrading.uimodel.UIFileText;
import com.algotrading.util.DateUtil;
import com.algotrading.util.FileUtil;
import com.algotrading.util.Parameter;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

/**
 * Repräsentiert eine Aktie am Aktienmarkt Oder auch einen Index oder ein Depot
 * mit täglichen Depotwerten enthält eine Reihe von Kursen mit aufsteigender
 * Sortierung Erzeugung und Zugang findet über die Klasse Aktien statt Enthält
 * auch Berechnungsvorschriften zu Indikatoren und Signalen
 * 
 */
@Service
@Entity
@Table(name = "aktiestamm")
// @formatter:off
@NamedEntityGraph(name = "aktie.kurs", attributeNodes = @NamedAttributeNode("kurse"))
// @formatter:on
public class Aktie extends Parameter {
	@Transient
	private static final Logger log = LogManager.getLogger(Aktie.class);

	@Autowired
	@Transient
	private AktieVerwaltung aV;

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "name", length = 64, unique = true, nullable = false)
	private String name;

	@Column(name = "kuerzel", length = 64, unique = true, nullable = true)
	private String kuerzel;

	@Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	@Column(name = "firmenname", length = 64)
	public String firmenname;

	@Column(name = "indexname", length = 64)
	public String indexname;

	@Column(name = "land")
	private int land = 1; // default = Deutschland

	@Column(name = "waehrung")
	private int waehrung = 1;  // default = Euro

	@Column(name = "isin", length = 12)
	private String ISIN;

	@Column(name = "wkn", length = 6)
	private String wkn;

	// die Datenquelle 1=Finanzen 2=Yahoo 3=Ariva
	@Column(name = "quelle")
	private int quelle = 1;  // default = Finanzen

	// 2 = Xetra
	@Column(name = "boersenplatz")
	public byte boersenplatz = 0;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "aktie")
	@OrderBy("datum")
	private List<Kurs> kurse = new ArrayList<Kurs>();

	// der Kurs, der zum aktuellen Datum des Depot gehört. NextKurs() sorgt für die Aktualisierung
	@Transient
	private Kurs aktuellerKurs;

	// der Kurs, der zum Start der Simulation gehört
	@Transient
	private Kurs startKurs;
	// der Zeitraum in dem Kurse vorhanden sind - stammt aus der DB
	@Transient
	private Zeitraum zeitraumKurse;
	@Transient
	private ArrayList<Kurs> kurseZeitraum;
	// ein Cache für die aktuell ermittelte Kursreihe
	@Transient
	private Zeitraum zeitraum;

	/*
	 * die Indikator-Algorithmen, die an der Aktie hängen - Zugriff über Getter die
	 * Liste stellt die Reihenfolge sicher
	 */
	@Transient
	private List<IndikatorAlgorithmus> indikatorAlgorithmen = new ArrayList<IndikatorAlgorithmus>();

	// eine Liste Tage, die für die Performance-Berechnung genutzt werden. 
	@Transient
	private List<Integer> performanceTage;

	@Transient
	private boolean indikatorenSindBerechnet = false;
	// die Signal-Algorithmen werden an der Aktie festgehalten und beim Berechnen an
	// die Kurse gehängt
	@Transient
	private List<SignalAlgorithmus> signalAlgorithmen = new ArrayList<SignalAlgorithmus>();
	@Transient
	private boolean signaleSindBerechnet = false;

	Aktie() {
	}

	/**
	 * 
	 */
	public Aktie(String name) {
		this.name = name.toLowerCase();
	}

	/**
	 * Zugriff auf die Indikatoren(Beschreibungen), die für eine Aktie existieren.
	 * Darüber ist ein Zugriff auf die Eindikatoren-Wert am Kurs möglich.
	 * 
	 * @return eine Liste der Indikator-Beschreibungen
	 */
	public List<IndikatorAlgorithmus> getIndikatorAlgorithmen() {
		return this.indikatorAlgorithmen;
	}

	/**
	 * ermittelt zu einem gegebenen Kurs den Vortageskurs wenn es der erste Kurs
	 * ist, dann null
	 * 
	 * @param kurs
	 * @return Vortageskurs, oder null
	 */
	public Kurs getVortageskurs(Kurs kurs) {
		int x = kurse.indexOf(kurs);
		if (x > 0)
			return kurse.get(x - 1);
		else
			return null;
	}

	public Kurs getStartKurs() {
		return this.startKurs;
	}

	/**
	 * ermittelt den Kurs zu einem bestimmten Datum Ist die Tage-Gleichheit nicht
	 * vorhanden, dann der nächste Kurs nach Tage-Gleichheit Liegt das gewünschte
	 * Datum vor dem ersten Kurs, -> null
	 * 
	 * @param datum
	 * @return der Kurs dieses Tages, oder null wenn nicht vorhanden
	 */
	public Kurs getKurs(GregorianCalendar datum) {
		String testDatum = DateUtil.formatDate(datum);
		// wenn das gewünschte Datum vor dem ersten Kurs liegt
		if (datum.before(kurse.get(0).getDatum())) {
			log.error("gewünschtes Kursdatum: " + testDatum + " liegt vor erstem vorhandenen Kurs: " + kurse.get(0));
			return null;
		}
		List<Kurs> kurse = this.getKursListe();
		// TODO hier könnte Performance gespart werden, wenn nicht von vorne iteriert
		// wird, sondern intelligent gesucht wird
		for (Kurs kurs : kurse) {
			// wenn die Tage exakt passen oder die Tage-Gleichheit übersprungen wurde
			if (DateUtil.istGleicherKalendertag(datum, kurs.getDatum()) || datum.before(kurs.getDatum())) {
				return kurs;
			}

		}
		log.error("Kurs nicht gefunden zu Datum: " + DateUtil.formatDate(datum));
		return null;
	}

	/**
	 * ermittelt und initialisiert eine Kursreihe innerhalb eines Zeitraums ist
	 * Zeitraum = null, werden alle Kurse zurück gegeben. Ein Cache für einen
	 * Zeitraum wird verwendet.
	 * 
	 * @param beginn
	 * @param ende
	 * @return
	 */
	public List<Kurs> getKurse(Zeitraum zeitraum) {
		ArrayList<Kurs> result = null;
		if (zeitraum == null)
			return this.getKursListe();
		// wenn es bereits einen Zeitraum gibt und dieser ist identisch mit der
		// angeforderten
		if (this.zeitraum != null && this.zeitraum.equals(zeitraum)) {
			result = this.kurseZeitraum;
		}
		// der Cache muss neu gefüllt werden
		else {
			result = this.sucheBoersenkurse(zeitraum);
		}
		this.zeitraum = zeitraum;
		this.kurseZeitraum = result;
		return result;
	}

	/**
	 * Eine Liste von Kursen, die innerhalb des gewünschten Zeitraums liegen 
	 */
	private ArrayList<Kurs> sucheBoersenkurse(Zeitraum zeitraum) {
		ArrayList<Kurs> kurse = new ArrayList<>();
		this.getKursListe().stream()
				.filter(kurs -> DateUtil.istInZeitraum(kurs.getDatum(), zeitraum))
				.forEach(kurs -> kurse.add(kurs));

		//		for (Kurs kurs : this.getKursListe()) {
		//			if (DateUtil.istInZeitraum(kurs.getDatum(), zeitraum)) {
		//				kurse.add(kurs);
		//			}
		//		}
		return kurse;
	}

	/**
	 * ermittelt und initialisiert eine Kursreihe mit allen vorhandenen Kursen
	 * ungeeignet für Depot-Kursreihen 
	 */
	public List<Kurs> getKursListe() {
		try {
			this.kurse.size();
		} catch (LazyInitializationException e) {
			this.initializeKurse();
		}
		return this.kurse;
	}

	/**
	 * Liest Kurse aus der DB und hängt sie an die Aktie
	 */
	private void initializeKurse() {
		// eine vollständige Aktie in einer Transaktion laden 
		Aktie aktie = aV.getAktieMitKurseFromDB(getId());
		// die Kurse übernehmen 
		this.setKurse(aktie.getKurse());
	}

	/**
	 * der nächste Kurs im Ablauf einer Simulation darf/muss für jeden Handelstag
	 * genau ein Mal aufgerufen werden.
	 * 
	 * @param kurs
	 * @return
	 */
	public Kurs nextKurs() {
		int x = this.kurse.indexOf(this.aktuellerKurs);
		if (x > this.kurse.size() - 2) {
			log.error(
					"Kursreihe zu Ende " + this.aktuellerKurs.getAktieName() + DateUtil
							.formatDate(this.aktuellerKurs.getDatum()));
			return this.aktuellerKurs;
		}
		Kurs kurs = this.kurse.get(x + 1);
		this.aktuellerKurs = kurs;
		return kurs;
	}

	/**
	 * Der Kurs, der innerhalb einer Simulation aktuell ist
	 * 
	 * @return
	 */
	public Kurs getAktuellerKurs() {
		return this.aktuellerKurs;
	}

	/**
	 * Zum Beginn der Zeitreihe wird der Startkurs auf 100% gesetzt
	 * 
	 * @return
	 */
	public float getIndexierterKurs() {
		float aktuellerKurs = this.getAktuellerKurs().getKurs();
		return (100 * aktuellerKurs / this.startKurs.getKurs());
	}

	/**
	 * ermittelt und initialisiert eine Kursreihe ab einem bestimmten Datum.
	 * 
	 * @param beginn
	 * @return
	 */
	public ArrayList<Kurs> getKursreihe(GregorianCalendar beginn) {
		if (beginn == null)
			log.error("Inputvariable Beginn ist null");
		if (this.kurse == null) {
			this.kurse = DBManager.getKursreihe(name, beginn);
		}
		return (ArrayList<Kurs>) kurse;
	}

	/**
	 * hängt einen Kurs an das Ende der bestehenden Kette an
	 */
	public void addKurs(Kurs kurs) {
		if (kurs == null)
			log.error("Inputvariable Kurs ist null");
		if (kurse == null) { // Aktie aus Depobewertung, die noch keine Kursliste besitzt
			this.kurse = new ArrayList<Kurs>();
		}
		Aktie newAktie = aV.getAktieMitKurse(this.getId());
		this.kurse = newAktie.getKursListe();
		this.kurse.add(kurs);
	}

	/**
	 * ein Array mit allen vorhandenen Kursen wird fär Rechen-Operationen genutzt,
	 * um schnell auf Kurse zugreifen zu können.
	 * 
	 * @return
	 */
	public float[] getKursArray() {
		int anzahl = this.getKursListe().size();
		float[] floatKurse = new float[anzahl];
		for (int i = 0; i < kurse.size(); i++) {
			floatKurse[i] = this.kurse.get(i).getKurs();
		}
		return floatKurse;
	}

	/**
	 * einen neuen Indikator hinzufügen Nachdem er über Konstruktor erzeugt wurde.
	 */
	public IndikatorAlgorithmus addIndikatorAlgorithmus(IndikatorAlgorithmus indikator) {
		this.indikatorAlgorithmen.add(indikator);
		return indikator;
	}

	/**
	 * Startet die Berechnung der Indikatoren, die als Liste an IndikatorAlgorithmen
	 * vorliegen
	 */
	public void rechneIndikatoren() {
		if (this.indikatorenSindBerechnet) {
			log.warn("Berechnung angestossen, obwohl bereits berechnet wurde");
		} else {
			for (IndikatorAlgorithmus iA : this.indikatorAlgorithmen) {
				// die abstrakte Methode, die jeder Algorithmus implmentieren muss
				iA.rechne(this);
				iA.berechnet();
			}
			this.indikatorenSindBerechnet = true;
		}

	}

	/**
	 * rechnet die Signal-Performance für alle Signale an der Aktie mit allen Laufzeit-Tagen 
	 */
	public void rechneSignalPerformance(List<Integer> tage) {
		this.setPerformanceTage(tage);
		for (SignalAlgorithmus sa : this.signalAlgorithmen) {
			sa.setaV(aV);
			for (int i : tage) {
				for (Signal signal : sa.getSignale()) {
					signal.getPerformance(i);
				}
			}
		}
	}

	/**
	 * Bestehende SignalBeschreibugen werden entfernt. Indikatoren bleiben erhalten
	 * Anschließend muss die SignalBerechnung erneut durchgeführt werden.
	 */
	public void clearSignale() {
		this.deleteSignale();
		this.signalAlgorithmen = new ArrayList<SignalAlgorithmus>();
		this.signaleSindBerechnet = false;
	}

	/**
	 * Ein neuer SignalAlgorithmus wird an der Aktie registriert Der
	 * SignalAlgorithmus wird anschließend berechnet Die Berechnung darf noch nicht
	 * durchgeführt sein.
	 */
	public SignalAlgorithmus addSignalAlgorithmus(SignalAlgorithmus sA) {
		sA.setAktie(this);
		this.signalAlgorithmen.add(sA);
		return sA;
	}

	/**
	 * steuert die Berechnung von allen Signalen einer Aktie auf Basis der
	 * vorhandene SignalBeschreibungen Die Signalsuche wird in Test-Cases
	 * separat/einzeln beauftragt. Die Indikatoren müssen bereits berechnet worden
	 * sein und hängen am Kurs.
	 */
	public void rechneSignale() {

		if (!this.signaleSindBerechnet && this.signalAlgorithmen.size() > 0) {
			for (SignalAlgorithmus sA : this.signalAlgorithmen) {
				int anzahl = sA.rechne(this);
				log.debug("Signale berechnet: " + sA.getKurzname() + " Aktie: " + this.name + " Anzahl: " + anzahl);
			}
		}
		this.signaleSindBerechnet = true;
	}

	public String toSmallString() {
		return this.name;
	}

	public String toString() {
		return this.name;
	}

	/**
	 * schreibt eine neue Datei mit Kursen, Indikatoren, Signalen, SignalPerformance
	 */
	public UIFileText writeFileKursIndikatorSignal() {
		String dateiname = "indisig" + this.name + Long.toString(System.currentTimeMillis());
		List<String> zeilen = this.writeIndikatorenSignale();
		File file = FileUtil.writeCSVFile(zeilen, dateiname);
		UIFileText result = new UIFileText(file.getAbsolutePath(), zeilen);
		log.info("Datei geschrieben: " + dateiname);
		return result;
	}

	/**
	 * schreibt eine neue Datei mit allen Signalen dieser Kursreihe
	 */
	public void writeFileSignale() {
		String dateiname = "signale" + this.name + Long.toString(System.currentTimeMillis());
		ArrayList<String> zeilen = this.writeSignale();
		FileUtil.writeCSVFile(zeilen, dateiname);
		log.info("Datei geschrieben: " + dateiname);
	}

	/**
	 * ermittelt den Kurs des Vortages des gegebenen Kurses
	 */
	public Kurs ermittleKursVortag(Kurs tk) {
		if (tk == null)
			log.error("Inputvariable Kurs ist null");
		int stelle = this.kurse.indexOf(tk);
		if (stelle > 0) {
			return this.kurse.get(stelle - 1);
		} else
			return null;
	}

	/**
	 * ermittelt und setzt den Kurs zu einem gegebenen Datum oder den ersten
	 * darauffolgenden Kurs, kann auch mehrere Tage danach sein. Zur einmaligen
	 * Initialisierung am Beginn der Simulation
	 * 
	 * @param datum
	 * @return
	 */
	public Kurs setStartkurs(GregorianCalendar datum) {
		if (datum == null)
			log.error("Inputvariable datum ist null");
		Kurs kurs;
		for (int i = 0; i < this.kurse.size(); i++) { // von links nach rechts
			// #TODO der Vergleich mässte mit before() oder after() geläst werden, nicht mit
			// Milli-Vergleich
			kurs = this.kurse.get(i);
			if (kurs.getDatum().getTimeInMillis() >= datum.getTimeInMillis()) {
				log.debug("Kurs gefunden: " + kurs);
				this.aktuellerKurs = kurs;
				this.startKurs = kurs;
				return kurs;
			}
		}
		log.info("Kurs nicht gefunden: " + DateUtil.formatDate(datum));
		return null;
	}

	/**
	 * alle Signale von allen Kursen nach Datum aufsteigend
	 * 
	 * @return
	 */
	public ArrayList<Signal> getSignale() {
		if (this.kurse == null)
			log.error("keine Kurse vorhanden in Aktie " + this.name);
		ArrayList<Signal> signale = new ArrayList<Signal>();
		// geht durch alle SignalAlgorithmen und holt alle angehängten Signale
		for (SignalAlgorithmus sA : this.signalAlgorithmen) {
			signale.addAll(sA.getSignale());
		}
		return signale;
	}

	/**
	 * alle Signale eines zugehörigen Signal-Algorithmus
	 * 
	 * @return eine Liste mit 0 - n Signalen
	 */
	public List<Signal> getSignale(SignalAlgorithmus signalAlgo) {
		if (this.kurse == null)
			log.error("keine Kurse vorhanden in Aktie " + this.name);
		List<Signal> signale = new ArrayList<Signal>();
		Signal signal;
		// geht durch alle Kurse und holt die gewünschten Signale
		for (Kurs kurs : this.getKursListe()) {
			signal = kurs.getSignal(signalAlgo);
			if (signal != null) {
				signale.add(signal);
			}
		}
		return signale;
	}

	/**
	 * Löscht alle Signale die an Kursen hängen
	 */
	private void deleteSignale() {
		for (Kurs kurs : this.kurse) {
			kurs.clearSignale();
		}
	}

	/**
	 * Zugriff auf die Signalbeschreibungen, die zu Beginn an die Aktie gehängt
	 * werden.
	 * 
	 * @return
	 */
	public List<SignalAlgorithmus> getSignalAlgorithmen() {
		return this.signalAlgorithmen;
	}

	/**
	 * Ermittelt den Zeitraum, in dem Kurse vorhanden sind. Entweder ist er in der
	 * DB gesetzt. Oder er wird über die Kurs-Liste ermittelt. Annahme ist, dass die
	 * Kurse nach Datum sortiert sind
	 * 
	 * @return der Zeitraum in dem Kurse vorhanden sind
	 */
	public Zeitraum getZeitraumKurse() {
		Zeitraum result = null;
		if (this.zeitraumKurse != null)
			result = this.zeitraumKurse;
		else
			result = new Zeitraum(this.kurse.get(0).getDatum(), this.kurse.get(this.kurse.size() - 1).getDatum());
		return result;
	}

	/**
	 * Setzt den Zeitraum, in dem Kurse vorhanden sind. Info stammt aus den
	 * Stammdaten in der DB
	 * 
	 * @param zeitraum
	 */
	public void setZeitraumKurse(Zeitraum zeitraum) {
		this.zeitraumKurse = zeitraum;
	}

	/**
	 * Setzt den Zeitraum der vorhandenen Kurse anhand der Kurse in der DB Liest
	 * dazu alle Kurse aus der DB und ermittelt Anfang und Ende, um die Stammdaten
	 * zu aktualisieren. Normalerweise werden die Daten aus der DB gelesen.
	 * 
	 * @param zeitraumKurse
	 */
	public void setZeitraumKurseAusDB() {
		this.zeitraumKurse = DBManager.getZeitraumVorhandeneKurse(this);
	}

	/**
	 * schreibt pro Tag alle Kurse, und Indikatoren als Zeilen
	 */
	private List<String> writeIndikatorenSignale() {
		List<String> zeilen = new ArrayList<>();
		// Header-Zeile
		zeilen.add("Datum;Close" + toStringIndikatorenSignalHeader());
		// für jeden Kurs eine Zeile
		for (Kurs kurs : kurse) {
			zeilen.add(kurs.toString());
		}
		return zeilen;
	}

	/**
	 * ein Header als Liste der Indikatoren, die an einer Aktie vorhanden sind
	 */
	// @formatter:off
	private String toStringIndikatorenSignalHeader() {
		StringBuilder header = new StringBuilder();
		// erst die Indikatoren 
		for (IndikatorAlgorithmus iA : this.indikatorAlgorithmen) {
			header.append(";" + iA.getKurzname());
		}
		// dann die Signale 
		for (SignalAlgorithmus sA : this.signalAlgorithmen) {
			header.append(String.format("%s%s%s%s%s%s", 
				Util.separatorCSV,
				sA.getKurzname(), 
				Util.separatorCSV,
				"Kauf",
				Util.separatorCSV,
				"Verkauf"));
			for (Integer i : this.getPerformanceTage()) {
				header.append(String.format("%s%s%s", Util.separatorCSV, i, "-Tage Performance"));
			}
		}
		return header.toString();
	}
	// @formatter:on

	/**
	 * schreibt die Signale der Aktie als Zeilen
	 */
	private ArrayList<String> writeSignale() {
		ArrayList<String> zeilen = new ArrayList<String>();
		zeilen.add("Name;Datum;KaufVerkauf;Typ;Staerke;Bewertung");
		// mit allen Kursen mit allen Signalen
		ArrayList<Signal> signale = getSignale();
		for (Signal signal : signale) {
			zeilen.add(signal.toString());
		}
		return zeilen;
	}

	public String getName() {
		return name;
	}

	public int getLand() {
		return land;
	}

	public void setLand(int land) {
		this.land = land;
	}

	public int getWaehrung() {
		return waehrung;
	}

	public void setWaehrung(int waehrung) {
		this.waehrung = waehrung;
	}

	public String getISIN() {
		return ISIN;
	}

	public void setISIN(String iSIN) {
		ISIN = iSIN;
	}

	public int getQuelle() {
		return quelle;
	}

	public void setQuelle(int quelle) {
		this.quelle = quelle;
	}

	public Long getId() {
		return id;
	}

	public String getFirmenname() {
		return firmenname;
	}

	public void setFirmenname(String firmenname) {
		this.firmenname = firmenname;
	}

	public String getIndexname() {
		return indexname;
	}

	public void setIndexname(String indexname) {
		this.indexname = indexname;
	}

	public byte getBoersenplatz() {
		return boersenplatz;
	}

	public void setBoersenplatz(byte boersenplatz) {
		this.boersenplatz = boersenplatz;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setaV(AktieVerwaltung aV) {
		this.aV = aV;
	}

	public void setKurse(List<Kurs> kurse) {
		for (Kurs kurs : kurse) {
			kurs.setAktie(this);
		}
		this.kurse = kurse;
	}

	public void setName(String name) {
		this.name = name.toLowerCase();
	}

	public String getWkn() {
		return wkn;
	}

	public void setWkn(String wkn) {
		this.wkn = wkn;
	}

	public String getKuerzel() {
		return kuerzel;
	}

	public void setKuerzel(String kuerzel) {
		this.kuerzel = kuerzel;
	}

	public List<Kurs> getKurse() {
		return kurse;
	}

	public List<Integer> getPerformanceTage() {
		return performanceTage;
	}

	public void setPerformanceTage(List<Integer> performanceTage) {
		this.performanceTage = performanceTage;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
