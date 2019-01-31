package com.algotrading.aktie;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Kurs;
import com.algotrading.data.DBManager;
import com.algotrading.depot.Order;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.signal.Signal;
import com.algotrading.signal.SignalAlgorithmus;
import com.algotrading.signal.SignalBewertung;
import com.algotrading.util.DateUtil;
import com.algotrading.util.FileUtil;
import com.algotrading.util.Parameter;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

/**
 * Repräsentiert eine Aktie am Aktienmarkt
 * Oder auch einen Index oder ein Depot mit täglichen Depotwerten
 * enthält eine Reihe von Kursen mit aufsteigender Sortierung 
 * Erzeugung und Zugang findet über die Klasse Aktien statt 
 */
public class Aktie extends Parameter {
	private static final Logger log = LogManager.getLogger(Aktie.class);

	public String name; 
	public String firmenname; 
	// kein öffentlicher Zugriff auf kurse, weil Initialisierung über DB erfolgt. 
	private ArrayList<Kurs> kurse; 
	// der Kurs, der zum aktuellen Datum des Depot gehört. NextKurs() sorgt für die Aktualisierung
	private Kurs aktuellerKurs; 
	// der Kurs, der zum Start der Simulation gehört
	private Kurs startKurs; 
	// der Zeitraum in dem Kurse vorhanden sind - stammt aus der DB
	private Zeitraum zeitraumKurse; 
	private ArrayList<Kurs> kurseZeitraum; 
	// ein Cache für die aktuell ermittelte Kursreihe 
	private Zeitraum zeitraum; 
	public String indexname;
	private String ISIN; 
	public String getISIN() {
		return ISIN;
	}
	public void setISIN(String iSIN) {
		ISIN = iSIN;
	}
	// die Datenquelle 1=Yahoo
	private int quelle; 
	public int getQuelle() {
		return quelle;
	}
	public void setQuelle(int quelle) {
		this.quelle = quelle;
	}
	public byte boersenplatz; 
	// die Indikator-Algorithmen, die an der Aktie hängen - Zugriff über Getter  
	// die Liste stellt die Reihenfolge sicher
	private List<IndikatorAlgorithmus> indikatorAlgorithmen = new ArrayList<IndikatorAlgorithmus>();
	private boolean indikatorenSindBerechnet = false; 
	// die Signal-Algorithmen werden an der Aktie festgehalten und beim Berechnen an die Kurse gehängt
	private List<SignalAlgorithmus> sAs = new ArrayList<SignalAlgorithmus>();
	private boolean signaleSindBerechnet = false; 
	
	/**
	 * ein Konstruktor mit beschränktem Zugriff für die Klasse Aktien 
	 * enthält alles, außer den Kursen
	 * @param name Kurzname, Kürzel - intern wird immer mit LowerCase gearbeitet
	 * @param firmenname offizieller Firmenname, zur Beschriftung verwendet 
	 * @param indexname zugehöriger Index zu Vergleichszwecken 
	 * @param boersenplatz 
	 */
	public Aktie (String name, String firmenname, String indexname, byte boersenplatz) {
		this.name = name.toLowerCase();
		this.firmenname = firmenname;
		this.indexname = indexname; 
		this.boersenplatz = boersenplatz;
	}
	/**
	 * Zugriff auf die Indikatoren(Beschreibungen), die für eine Aktie existieren. 
	 * Darüber ist ein Zugriff auf die Eindikatoren-Wert am Kurs möglich. 
	 * @return eine Liste der Indikator-Beschreibungen
	 */
	public List<IndikatorAlgorithmus> getIndikatorAlgorithmen() {
		return this.indikatorAlgorithmen;
	}
	
	/**
	 * Gibt den Inhalt der Kurse, ohne diese zu initialisieren 
	 * @return
	 */
	public ArrayList<Kurs> getKurse () {
		if (this.kurse == null) log.error("Kurse sind null");
		return this.kurse;
	}
	
	/**
	 * ermittelt zu einem gegebenen Kurs den Vortageskurs 
	 * wenn es der erste Kurs ist, dann null 
	 * @param kurs
	 * @return Vortageskurs, oder null 
	 */
	public Kurs getVortageskurs (Kurs kurs) {
		int x = kurse.indexOf(kurs);
		if (x > 0) return kurse.get(x - 1);
		else return null; 
	}
	
	public Kurs getStartKurs () {
		return this.startKurs;
	}
	/**
	 * ermittelt den Kurs zu einem bestimmten Datum
	 * Ist der Kurs nicht vorhanden, dann null. 
	 * @param datum
	 * @return der Kurs dieses Tages, oder null wenn nicht vorhanden 
	 */
	public Kurs getKurs (GregorianCalendar datum) {
		ArrayList<Kurs> kurse = this.getBoersenkurse();
		for (Kurs kurs : kurse) {
			if (DateUtil.istGleicherKalendertag(datum, kurs.datum)) {
				return kurs; 
			}
		}
		return null; 
	}
	
	/**
	 * ermittelt und initialisiert eine Kursreihe innerhalb eines Zeitraums
	 * Ein Cache für einen Zeitraum wird verwendet. 
	 * @param beginn
	 * @param ende
	 * @return
	 */
	public ArrayList<Kurs> getKurse(Zeitraum zeitraum) {
		ArrayList<Kurs> result = null;
		if (zeitraum == null) log.error("Inputvariable Zeispanne ist null");
		// wenn es bereits einen Zeitraum gibt und dieser ist identisch mit der angeforderten
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
	
	private ArrayList<Kurs> sucheBoersenkurse (Zeitraum zeitraum) {
		ArrayList<Kurs> kurse = new ArrayList<Kurs>();
		for (Kurs kurs : this.getBoersenkurse()) {
			if (DateUtil.istInZeitraum(kurs.datum, zeitraum)) {
				kurse.add(kurs);
			}
		}
		return kurse; 
	}
	
	/**
	 * ermittelt und initialisiert eine Kursreihe mit allen vorhandenen Kursen
	 * ungeeignet für Depot-Kursreihen 
	 * @param beginn
	 * @param ende
	 * @return
	 */
	public ArrayList<Kurs> getBoersenkurse () {
		if (this.kurse == null) {
			this.kurse = DBManager.getKursreihe(name);
		}
		return kurse;
	}
	/**
	 * der nächste Kurs im Ablauf einer Simulation 
	 * darf/muss für jeden Handelstag genau ein Mal aufgerufen werden. 
	 * @param kurs
	 * @return
	 */
	public Kurs nextKurs () {
		int x = this.kurse.indexOf(this.aktuellerKurs);
		if (x > this.kurse.size()-2) {
			log.error("Kursreihe zu Ende " + 
				this.aktuellerKurs.wertpapier + DateUtil.formatDate(this.aktuellerKurs.datum));
			return this.aktuellerKurs;
		}
		Kurs kurs = this.kurse.get(x + 1);
		this.aktuellerKurs = kurs; 
		return kurs; 
	}
	/**
	 * Der Kurs, der innerhalb einer Simulation aktuell ist 
	 * @return
	 */
	public Kurs getAktuellerKurs () {
		return this.aktuellerKurs;
	}
	
	/**
	 * Zum Beginn der Zeitreihe wird der Startkurs auf 100% gesetzt 
	 * @return
	 */
	public float getIndexierterKurs () {
		float aktuellerKurs = this.getAktuellerKurs().getKurs();
		return (100 * aktuellerKurs / this.startKurs.getKurs());
	}
	
	/**
	 * ermittelt und initialisiert eine Kursreihe ab einem bestimmten Datum. 
	 * @param beginn
	 * @return
	 */
	public ArrayList<Kurs> getKursreihe (GregorianCalendar beginn) {
		if (beginn == null) log.error("Inputvariable Beginn ist null");
		if (this.kurse == null) {
			this.kurse = DBManager.getKursreihe(name, beginn);
		}
		return kurse;
	}
	
	/**
	 * hängt einen Kurs an das Ende der bestehenden Kette an
	 * @param ein beliebiger Kurs 
	 */
	public void addKurs(Kurs kurs) {
		if (kurs == null) log.error("Inputvariable Kurs ist null");
		if (kurse == null) {  // Aktie aus Depobewertung, die noch keine Kursliste besitzt
			this.kurse = new ArrayList<Kurs>();
		}
		kurse.add(kurs);
	}
	
	/**
	 * ein Array mit allen vorhandenen Kursen 
	 * wird fär Rechen-Operationen genutzt, um schnell auf Kurse zugreifen zu können. 
	 * @return
	 */
	public float[] getKursArray () {
		int anzahl = this.getBoersenkurse().size();
		float[] floatKurse = new float[anzahl];
		for (int i = 0 ; i < kurse.size() ; i++) {
			floatKurse[i] = this.kurse.get(i).getKurs();
		}
		return floatKurse;
	}
	/**
	 * einen neuen Indikator hinzufügen 
	 * Nachdem er über Konstruktor erzeugt wurde. 
	 */
	public IndikatorAlgorithmus createIndikatorAlgorithmus (IndikatorAlgorithmus indikator) {
		this.indikatorAlgorithmen.add(indikator);
		return indikator;
	}
	
	/**
	 * Startet die Berechnung der Indikatoren, die als Liste an IndikatorAlgorithmen vorliegen
	 */
	public void rechneIndikatoren () {
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
	 * Bestehende SignalBeschreibugen werden entfernt. 
	 * Indikatoren bleiben erhalten 
	 * Anschließend muss die SignalBerechnung erneut durchgeführt werden. 
	 */
	public void clearSignale () {
		this.deleteSignale();
		this.sAs = new ArrayList<SignalAlgorithmus>();
		this.signaleSindBerechnet = false; 
	}
	
	/**
	 * Ein neuer SignalAlgorithmus, der anschließend berechnet wird
	 * Die Berechnung darf noch nicht durchgeführt sein. 
	 */
	public SignalAlgorithmus createSignalAlgorithmus (SignalAlgorithmus sA) {
		this.sAs.add(sA);
		return sA; 
	}
	
	/**
	 * steuert die Berechnung von allen Signalen einer Aktie auf Basis der vorhandene SignalBeschreibungen 
	 * Die Signalsuche wird in Test-Cases separat/einzeln beauftragt. 
	 * Die Indikatoren müssen bereits berechnet worden sein und hängen am Kurs. 
	 */
	public void rechneSignale () {
		
		if (! this.signaleSindBerechnet && this.sAs.size() > 0) {
			for (SignalAlgorithmus sA : this.sAs) {
				int anzahl = sA.rechne(this);
				log.debug("Signale berechnet: " + sA.getKurzname() + " Aktie: " + this.name + " Anzahl: " + anzahl);
			}
		}
		this.signaleSindBerechnet = true; 
	}
	
	/**
	 * Bewertet alle Signale: Signalstärke * Erfolg (Performance) 
	 * Signalstärke ist positiv für Kauf-_Signale - negativ für Verkauf-Signale 
	 * Erfolg ist positiv bei steigenden Kursen - negativ bei fallenden Kursen. 
	 * ==> Hohe positive Werte bedeuten gute Prognose-Qualität bei Kauf und Verkauf !
	 * ==> Hohe negative Wert bedeuten entgegen gesetzte Prognose-Qualität
	 * Prognose-Quantität: wie viele Signale gehen in die erwartete Richtung. 
	 * Prognose-Qualität:  
	 * @param zeitraum der Zeitraum in dem die signale auftreten
	 * 		Wenn null, dann maximaler Zeitraum, für den Signale vorliegen. 
	 * @param tageVoraus für die Erfolgsmessung in die Zukunft 
	 */
	public void bewerteSignale (Zeitraum zeitraum, int tage) {
		// alle Signal-Typen an der Aktie
		for (SignalAlgorithmus sA : this.sAs) {
			// an der Beschreibung eine neue Gesamt-Bewertung erzeugen 
			SignalBewertung sBW = sA.createBewertung();
			sBW.setTage(tage);
			if (zeitraum == null) {
				// maximaler Zeitraum ermitteln 
				zeitraum = sA.getZeitraumSignale(); 
			}
			
			sBW.setZeitraum(zeitraum);
			// alle zugehörigen Signale 
			// TODO: hier könnte man den Zeitraum bereits berücksichtigen
			List<Signal> signale = this.getSignale(sA);
			
			float staerke = 0;		// die Signal-Stärke eines einzelnen Signals
			int kaufKorrekt = 0;
			int verkaufKorrekt = 0;
			
			// für alle Signale zu dieser SignalBeschreibung
			for (Signal signal : signale) {
				// nur Signale im vorgegebenen Zeitraum 
				if (! DateUtil.istInZeitraum(signal.getKurs().getDatum(), zeitraum)) continue;
				// die Bewertung des Signals: Kursentwicklung in die Zukunft 
				staerke = signal.getStaerke(); 
				// die Bewertung wird ermittelt
				Float bewertung = signal.getBewertung(tage);
				if (bewertung == null) continue; // wenn keine Bewertung vorhanden ist, dann nächstes Signal 
				float b = bewertung;
				sBW.summeBewertungen += b;
				if (signal.getKaufVerkauf() == Order.KAUF) {
					sBW.kauf ++;
					sBW.summeBKauf += b;
					sBW.summeSKauf += staerke;
					if (b > 0) kaufKorrekt ++;
				}
				else {
					sBW.verkauf ++;
					sBW.summeBVerkauf += b;
					sBW.summeSVerkauf += staerke;
					if (b > 0) verkaufKorrekt ++;
				}
			}
			sBW.kaufKorrekt = (float) ((double) kaufKorrekt / sBW.kauf);
			sBW.verkaufKorrekt = (float) ((double) verkaufKorrekt / sBW.verkauf);
			
			System.out.println(this.name + " B:" + tage + Util.separatorCSV + zeitraum.toStringJahre() + Util.separatorCSV + sBW.toString());
		}
	}
	
	public String toSmallString () {
		return this.name;
	}
	
	public String toString () {
		String result;
		result = name + " " + kurse.size() + " Kurse";
		for (int i = 0 ; i < kurse.size(); i++) {
			result = result + (kurse.get(i).toString());
		}
		return result; 
	}

	/**
	 * schreibt eine neue Datei mit Kursen, Indikatoren, Signalen
	 */
	public void writeFileKursIndikatorSignal () {
		String dateiname = "indisig" + this.name + Long.toString(System.currentTimeMillis());
		ArrayList<String> zeilen = this.writeIndikatorenSignale();
		FileUtil.writeCSVFile(zeilen, dateiname);
		log.info("Datei geschrieben: " + dateiname );
	}
	/**
	 * schreibt eine neue Datei mit allen Signalen dieser Kursreihe
	 */
	public void writeFileSignale () {
		String dateiname = "signale" + this.name + Long.toString(System.currentTimeMillis());
		ArrayList<String> zeilen = this.writeSignale();
		FileUtil.writeCSVFile(zeilen, dateiname);
		log.info("Datei geschrieben: " + dateiname );
	}
	/**
	 * ermittelt den Kurs des Vortages des gegebenen Kurses
	 */
	public Kurs ermittleKursVortag (Kurs tk) {
		if (tk == null) log.error("Inputvariable Kurs ist null");
		int stelle = this.kurse.indexOf(tk);
		if (stelle > 0) {
			return this.kurse.get(stelle - 1);
		}
		else return null;
	}
	
	/**
	 * ermittelt und setzt den Kurs zu einem gegebenen Datum 
	 * oder den ersten darauffolgenden Kurs, kann auch mehrere Tage danach sein. 
	 * Zur einmaligen Initialisierung am Beginn der Simulation
	 * @param datum
	 * @return
	 */
	public Kurs setStartkurs (GregorianCalendar datum) {
		if (datum == null) log.error("Inputvariable datum ist null");
		Kurs kurs; 
		for (int i = 0 ; i < this.kurse.size(); i++) {	// von links nach rechts
			// #TODO der Vergleich mässte mit before() oder after() geläst werden, nicht mit Milli-Vergleich
			kurs = this.kurse.get(i);
			if (kurs.datum.getTimeInMillis() >= datum.getTimeInMillis()) {
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
	 * @return
	 */
	public ArrayList<Signal> getSignale() {
		if ( this.kurse == null) log.error("keine Kurse vorhanden in Aktie " + this.name);
		ArrayList<Signal> signale = new ArrayList<Signal>();
		// geht durch alle SignalAlgorithmen und holt alle angehängten Signale
		for (SignalAlgorithmus sA : this.sAs) {
			signale.addAll(sA.getSignale());
		}
		return signale;
	}
	
	/**
	 * alle Signale eines zugehörigen Signal-Algorithmus
	 * @return eine Liste mit 0 - n Signalen 
	 */
	public List<Signal> getSignale(SignalAlgorithmus signalAlgo) {
		if ( this.kurse == null) log.error("keine Kurse vorhanden in Aktie " + this.name);
		List<Signal> signale = new ArrayList<Signal>();
		Signal signal; 
		// geht durch alle Kurse und holt die gewünschten Signale
		for (Kurs kurs : this.getBoersenkurse()) {
			signal = kurs.getSignal(signalAlgo);
			if ( signal != null) {
				signale.add(signal);
			}
		}
		return signale;
	}

	/**
	 * Löscht alle Signale die an Kursen hängen
	 */
	private void deleteSignale () {
		for (Kurs kurs : this.kurse) {
			kurs.clearSignale();
		}
	}
	
	/**
	 * Zugriff auf die Signalbeschreibungen, die zu Beginn an die Aktie gehängt werden. 
	 * @return
	 */
	public List<SignalAlgorithmus> getSignalAlgorithmen() {
		return this.sAs;
	}
	
	public Zeitraum getZeitraumKurse() {
		return zeitraumKurse;
	}
	
	/**
	 * Setzt den Zeitraum, in dem Kurse vorhanden sind. 
	 * Info stammt aus den Stammdaten in der DB
	 * @param zeitraum
	 */
	public void setZeitraumKurse (Zeitraum zeitraum) {
		this.zeitraumKurse = zeitraum;
	}
	
	/**
	 * Setzt den Zeitraum der vorhandenen Kurse anhand der Kurse in der DB 
	 * Liest dazu alle Kurse aus der DB und ermittelt Anfang und Ende, um die Stammdaten zu aktualisieren. 
	 * Normalerweise werden die Daten aus der DB gelesen. 
	 * @param zeitraumKurse
	 */
	public void setZeitraumKurseAusDB() {
		this.zeitraumKurse = DBManager.getZeitraumVorhandeneKurse(this);
	}
	
	/**
	 * schreibt pro Tag alle Kurse, und Indikatoren als Zeilen 
	 */
	private ArrayList<String> writeIndikatorenSignale () {
		ArrayList<String> zeilen = new ArrayList<String>();
		// Header-Zeile
		zeilen.add("Datum;Close" + toStringIndikatorenSignalHeader());
		
		for (int i = 0 ; i < kurse.size(); i++) {
			zeilen.add(kurse.get(i).toString());
		}
		return zeilen; 	
	}
	/**
	 * ein Header als Liste der Indikatoren, die an einer Aktie vorhanden sind
	 */
	private String toStringIndikatorenSignalHeader () {
		String result = "";
		for (IndikatorAlgorithmus iA : this.indikatorAlgorithmen) {
			result = result.concat(";" + iA.getKurzname());
		}
		result = result.concat(";STyp1;KV1;Wert1;STyp2;KV2;Wert2");
		return result; 
	}
	
	/**
	 * schreibt die Signale der Aktie als Zeilen 
	 */
	private ArrayList<String> writeSignale () {
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
}
