package com.algotrading.aktie;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Kurs;
import com.algotrading.data.DBManager;
import com.algotrading.depot.Order;
import com.algotrading.indikator.IndikatorBeschreibung;
import com.algotrading.indikator.Indikatoren;
import com.algotrading.signal.Signal;
import com.algotrading.signal.SignalBeschreibung;
import com.algotrading.signal.SignalBewertung;
import com.algotrading.signal.Signalsuche;
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
	// die Indikatoren-Beschreibungen, die an der Aktie hängen - Zugriff über Getter  
	ArrayList<IndikatorBeschreibung> indikatorBeschreibungen = new ArrayList<IndikatorBeschreibung>();
	private boolean indikatorenSindBerechnet = false; 
	// die Signal-Beschreibungen werden an der Aktie festgehalten und beim Berechnen an die Kurse gehängt
	private ArrayList<SignalBeschreibung> signalbeschreibungen = new ArrayList<SignalBeschreibung>();
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
	public ArrayList<IndikatorBeschreibung> getIndikatorBeschreibungen() {
		return indikatorBeschreibungen;
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
	 * wird fär Rechen-Operationen genutzt, um schnell auf Kurse zugreifen zu kännen. 
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
	 * einen neuen Indikator hinzufägen 
	 * @param typ
	 * @return der neue Indikator
	 */
	public void addIndikator (IndikatorBeschreibung indikator) {
		this.indikatorBeschreibungen.add(indikator);
	}
	
	/**
	 * Startet die Berechnung der Indikatoren, die als Indikator-Beschreibungen an der Aktie hängen
	 */
	public void rechneIndikatoren () {
		if (this.indikatorenSindBerechnet) {
			log.warn("Berechnung angestossen, obwohl bereits berechnet wurde");
		} else { 
			Indikatoren.rechneIndikatoren(this);
			this.indikatorenSindBerechnet = true;
		}
		
	}
	/**
	 * Bestehende SignalBeschreibugen werden entfernt. 
	 * Indikatoren bleiben erhalten 
	 * Anschlieäend muss die SignalBerechnung erneut durchgefährt werden. 
	 */
	public void clearSignale () {
		this.deleteSignale();
		this.signalbeschreibungen = new ArrayList<SignalBeschreibung>();
		this.signaleSindBerechnet = false; 
	}
	
	/**
	 * Eine neue SignalBeschreibung, die anschließend berechnet wird
	 * Die Berechnung darf noch nicht durchgeführt sein. 
	 */
	public SignalBeschreibung createSignalBeschreibung (short typ) {
		SignalBeschreibung result = new SignalBeschreibung(this, typ);
		this.signalbeschreibungen.add(result);
		return result; 
	}
	
	/**
	 * Berechnet alle Signale für alle Kurse anhand der SignalBeschreibungen 
	 */
	public void rechneSignale () {
		if (! this.signaleSindBerechnet && this.signalbeschreibungen.size() > 0) {
			Signalsuche.rechneSignale(this);
			this.signaleSindBerechnet = true; 
		}
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
	 * @param tageVoraus für die Erfolgsmessung in die Zukunft 
	 */
	public void bewerteSignale (Zeitraum zeitraum, int tage) {
		// alle Signal-Typen an der Aktie
		for (SignalBeschreibung sB : this.signalbeschreibungen) {
			// an der Beschreibung eine neue Gesamt-Bewertung erzeugen 
			SignalBewertung sBW = sB.addBewertung();
			sBW.setTage(tage);
			sBW.setZeitraum(zeitraum);
			// alle zugehörigen Signale 
			// TODO: hier könnte man den Zeitraum bereits berücksichtigen
			ArrayList<Signal> signale = this.getSignale(sB);
			
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
	 * veranlasst das Schreiben on 2 Dateien und Kursen und Signalen
	 */
	public void writeIndikatorenSignale () {
		writeFileIndikatoren();
		writeFileSignale();
	}
	/**
	 * schreibt eine neue Datei mit allen Kursen, Indikatoren
	 */
	public void writeFileIndikatoren () {
		String dateiname = "kurse" + this.name + Long.toString(System.currentTimeMillis());
		ArrayList<String> zeilen = this.writeIndikatoren();
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
		// geht durch alle Kurse und holt alle angehängten Signale
		for (Kurs kurs : this.kurse) {
			signale.addAll(kurs.getSignale());
		}
		return signale;
	}
	
	/**
	 * alle Signale einer zugehörigen Signal-Beschreibung 
	 * von allen Tageskursen nach Datum aufsteigend
	 * @return eine Liste mit 0 - n Signalen 
	 */
	public ArrayList<Signal> getSignale(SignalBeschreibung signalBeschreibung) {
		if ( this.kurse == null) log.error("keine Kurse vorhanden in Aktie " + this.name);
		ArrayList<Signal> signale = new ArrayList<Signal>();
		Signal signal = null; 
		// geht durch alle Kurse und holt die gewünschten Signale
		for (Kurs kurs : this.kurse) {
			signal = kurs.getSignal(signalBeschreibung);
			if (signal != null) {
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
	public ArrayList<SignalBeschreibung> getSignalbeschreibungen() {
		return signalbeschreibungen;
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
	private ArrayList<String> writeIndikatoren () {
		ArrayList<String> zeilen = new ArrayList<String>();
		// Header-Zeile
		zeilen.add("Datum ; Close ; " + toStringIndikatoren());
		
		for (int i = 0 ; i < kurse.size(); i++) {
			zeilen.add(kurse.get(i).toString());
		}
		return zeilen; 	
	}
	/**
	 * ein Header als Liste der Indikatoren, die an einer Aktie vorhanden sind
	 */
	private String toStringIndikatoren () {
		String result = "";
		for (IndikatorBeschreibung iB : this.indikatorBeschreibungen) {
			result = result.concat(Short.toString(iB.getTyp()));
		}
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
		for (int i = 0 ; i < signale.size() ; i++) {
			zeilen.add(signale.get(i).toString());
		}
		return zeilen; 
	}
	public String getName() {
		return name;
	}
}
