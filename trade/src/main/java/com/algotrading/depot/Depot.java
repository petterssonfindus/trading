package com.algotrading.depot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.signal.Signal;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Util;
import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.indikator.IndikatorAlgorithmus;

/**
 * Simuliert ein Wertpapierdepot in einem Zeitraum 
 * Verwaltet Wertpapierbestände und die Liste der Orders und Trades. 
 * äberwacht die Ausfährung von limitierten Orders. 
 * Errechnet seinen täglichen Depotwert. 
 * @author oskar
 *
 */
public class Depot {
	private static final Logger log = LogManager.getLogger(Depot.class);
	
	String name; 
	// der aktuelle Tag der Simulation - manche Methoden beziehen sich auf aktuelle Kurse 
	GregorianCalendar heute; 	// ist gleichzeit das Ende der Simulation nach Beendigung
	GregorianCalendar beginn;	// Beginn der Simulation
	float geld;  // Geldbestand 
	float anfangsbestand; // 
	ArrayList<Order> orders = new ArrayList<>();
	// Liste aller Trades, die im Depot erzeugt wurden 
	ArrayList<Trade> trades = new ArrayList<>();
	// Liste der Trades im Zustand "läuft" 
	ConcurrentHashMap<String, Trade> aktuelleTrades = new ConcurrentHashMap<>();
	// eine Zeitreihe mit Tagesend-Bewertungen 
	Aktie depotwert; 
	// aktuelle Liste aller vorhandenen Wertpapiere
	ConcurrentHashMap<String, Wertpapierbestand> wertpapierbestand = new ConcurrentHashMap<>();
	// das Anlage-Universum. Nehmen teil an der Simulation 
	ArrayList<Aktie> aktien; 
	// das Depot kennt seine Strategien
	SignalStrategie signalStrategie; 
	TagesStrategie tagesStrategie; 
	// Bewertung aller Trades des Depot äber eine Laufzeit
	Strategiebewertung strategieBewertung; 
	// verbleibt bim Verkauf ein Restbestand, wir dieser mit verkauft
	public static final float SCHWELLE_VERKAUF_RESTBESTAND = 100; 
	private int signalzaehler = 0;
	private int stoplossZaehler = 0; 
	private BufferedWriter fileWriterHandelstag; 
	
	public Depot (String name, float geld) {
		this.name = name; 
		this.geld = geld;
		this.anfangsbestand = geld; 
		log.debug("neues Depot angelegt: " + name + " - " + geld);
	}

	/**
	 * Durchläuft alle Tage, holt sich alle Signale und handelt
	 * Bewertet die Strategie am Ende 
	 * @param aktie vorbereitet incl. Indikatoren 
	 */
	public void simuliereDepot (SignalStrategie signalStratgie, TagesStrategie tagesStrategie,
			ArrayList<Aktie> aktien, GregorianCalendar beginn, GregorianCalendar ende, boolean writeHandelstag) {
		if (signalStratgie == null) log.error("Inputparameter Kaufstrategie = null");
		if (beginn == null) log.error("Inputparameter Beginn = null");
		if (ende == null) log.error("Inputparameter Ende = null");
		if (aktien == null || aktien.isEmpty()) log.error("Inputparameter Aktien = null");
		if ( ! ende.after(beginn)) log.error("Inputparameter Ende liegt vor Beginn");
		
		this.beginn = beginn;
		this.tagesStrategie = tagesStrategie;
		this.signalStrategie = signalStratgie; 
		// eine Aktie mit Zeitreihe wird angelegt, um die Depotwerte zu speichern 
		this.depotwert = new Aktie(this.name, "Depot " + this.name, Aktien.INDEXDAX,Aktien.BOERSEDEPOT);
		
		// die Berechnungen an den Aktien werden durchgeführt
		for (Aktie aktie : aktien ) {
			aktie.rechneSignale();
		}
		log.info("starte Simulation von bis: " + DateUtil.formatDate(beginn) + " - " + DateUtil.formatDate(ende));
		this.heute = beginn; 
		int tagZaehler = 0;
		// stellt für alle Aktien den Kurs zum Beginn ein 
		for (Aktie aktie : aktien) {
			aktie.setStartkurs(heute);
		}
		// der Ablauf der Simulation für alle Tage innerhalb des Zeitraums
		while (DateUtil.istInZeitraum(this.heute, beginn, ende)) {
			tagZaehler ++;
			if (tagZaehler > 10) {  // die ersten Tage werden ignoriert
				// ein Handelstag läuft ab und wird als Tages-Kurs festgehalten 
				Kurs kurs = simuliereHandelstag(signalStratgie, tagesStrategie);
				// das Ergebnis des Handels wird als Tags-Kurs an eine Kursreihe gehängt. 
				this.depotwert.addKurs(kurs);
				// csv wird ergänzt, wenn dies gewünscht ist 
				if (writeHandelstag) writeHandelstag(kurs);
			}
			this.nextDay();	// dabei wird this.heute weiter gestellt und die Aktienkurse weiter geschaltet
		}
		// am letzten Tag wird alles verkauft und damit der letzte Trade geschlossen 
		if (this.heute.after(ende) && ! this.wertpapierbestand.keySet().isEmpty()) {
			verkaufeGesamtbestand();
		}
		// die Ergebnisse im log protokolliert. 
		log.debug("In Depotsimulation wurden Orders aus Signalen erzeugt: " + signalzaehler);
		log.debug("In Depotsimulation wurden Orders aus SL erzeugt: " + stoplossZaehler);
		log.debug("In Depotsimulation wurden Orders erzeugt: " + orders.size());
		log.debug("In Depotsimulation wurden Trades erzeugt: " + trades.size());
		// das Ergebnis der Strategie aufbereiten 
		this.bewerteStrategie();
		// Aufräumarbeiten: Signale werden gelöscht, File schließen.  
		if (writeHandelstag) this.closeHandelstagFile();
		for (Aktie aktie : aktien ) {
			aktie.clearSignale();
		}
	}
	
	/**
	 * Simuliert einen Handelstag mit Kauf / Verkaufsentscheidungen für alle Aktien 
	 * und einer Depotbewertung 
	 * @param kaufstrategie
	 * @param slStrategie
	 * @param kurs
	 * @return der Tagesendwert des Depot 
	 */
	private Kurs simuliereHandelstag(SignalStrategie kaufstrategie, TagesStrategie tagesStrategie) {
		Order order; 
		// der Kurs für den Depotwert
		Kurs kursDepot = new Kurs();
		kursDepot.datum = this.heute;
		int anzahlOrder = 0;
		
		// Signale von allen Aktien heute werden eingesammelt
		ArrayList<Signal> signale = this.getSignale();
		if (signale != null && ! signale.isEmpty()) {
			// jedes Signal wird weiter geleitet
			for (Signal signal : signale) {
				// die Kaufstrategie bekommt das Signal 
				order = kaufstrategie.entscheideSignal(signal, this);
				if (order != null) {
					signalzaehler ++; 
					anzahlOrder ++;
				}
			}
		}
		// Stop-Loss wird überwacht, falls eines vorhanden ist
		if (tagesStrategie != null) {
			order = tagesStrategie.entscheideTaeglich(this);
			// wenn eine Order entstanden ist 
			if (order != null) {
				stoplossZaehler ++;
				anzahlOrder ++; 
			}
		}
		kursDepot.close = this.bewerteDepotAktuell();
		log.debug("Handelstag: " + DateUtil.formatDate(this.heute) + " Signale: " + signale.size() + " Order: " + anzahlOrder + 
				" Wert: " + kursDepot.close);
		return kursDepot; 
	}
	/**
	 * Holt an einem neuen Handelstag die Kurse der Aktien 
	 * Setzt das aktuelle Datum 
	 * #TODO Präfen, ob das Datum äbereinstimmt, Fehlerbehandlung
	 * @return
	 */
	private ArrayList<Kurs> nextDay () {
		ArrayList<Kurs> kurse = new ArrayList<Kurs>();
		for (Aktie aktie : this.aktien) {
			kurse.add(aktie.nextKurs());
		}
		this.heute = kurse.get(0).datum;
		log.debug("nextDay: " + DateUtil.formatDate(this.heute));
		return kurse; 
	}
	
	private ArrayList<Signal> getSignale () {
		ArrayList<Signal> signale = new ArrayList<Signal>();
		
		for (Aktie aktie : this.aktien) {
			// holt sich den aktuellen Kurs der Aktie
			Kurs kurs = aktie.getAktuellerKurs();
			signale.addAll(kurs.getSignale());
		}
		return signale; 
	}
	
	/**
	 * kauft mit Disposition 
	 * @param aktie
	 * @param betrag
	 */
	protected Order kaufe (float betrag, Aktie aktie) {
		if (aktie == null) log.error("Inputvariable Aktie ist null");
		Order result = null; 
		// Maximum bestehendes Geld
		if (this.geld < betrag) {	// das Geld reicht nicht aus
			betrag = this.geld;		// das vorhandene Geld wird eingesetzt
		}
		// 
		if (betrag > 100) {			// unter 100 macht es keinen Sinn. 
			float kurs = aktie.getAktuellerKurs().getKurs();
			float stueckzahl = betrag / kurs; 
			result = Order.orderAusfuehren(Order.KAUF, aktie.name, stueckzahl, this);
		}
		return result; 
	}
	/**
	 * kauft mit Disposition 
	 * Wenn kein Geld vorhanden, wird nichts gekauft
	 * @param betrag
	 * @param wertpapier
	 * @return die Kauf-Order falls gekauft wurde - ansonsten null 
	 */
	protected Order kaufe (float betrag, String wertpapier) {
		Aktie aktie = Aktien.getInstance().getAktie(wertpapier);
		return this.kaufe(betrag, aktie);
	}

	/**
	 * am Ende einer Simulation wird der Gesamtbestand verkauft, damit alle Trades geschlossen werden. 
	 */
	protected Order verkaufeGesamtbestand () {
		Order order = null; 
		for (Wertpapierbestand wertpapier : this.wertpapierbestand.values()) {
			order = this.verkaufe(wertpapier.getAktie());
		}
		return order;
	}
	
	/**
	 * Verkauft Gesamtbestand des vorhandenen Wertpapiers
	 * Wenn kein Bestand vorhanden, dann null
	 * @return Die ausgefährte Verkaufs-Order, oder null, wenn kein Bestand vorhanden
	 */
	protected Order verkaufe (Aktie aktie) {
		Order order = null; 
		// ermittelt Bestand des Wertpapiers
		Wertpapierbestand wertpapierbestand = this.getWertpapierBestand(aktie.name);
		if (wertpapierbestand != null) {
			float bestand = wertpapierbestand.bestand;
			order = Order.orderAusfuehren(Order.VERKAUF, aktie.name, bestand, this);
		}
		return order; 
	}

	protected Order verkaufe (String wertpapier) {
		Aktie aktie = Aktien.getInstance().getAktie(wertpapier);
		return verkaufe (aktie);
	}
	/**
	 * Beim Verkauf wird gepräft, ob genägend Wertpapiere vorhanden sind. 
	 * Wenn nicht, wird die Order angepasst auf die vorhandenen Stäcke. 
	 * Wenn ein kleiner Restbestand verbleibt, wird der Betrag entsprechend erhäht
	 * @param datum
	 * @param betrag
	 * @param aktie
	 */
	protected Order verkaufe (float betrag, Aktie aktie) {
		if (aktie == null) log.error("Inputvariable Kursreihe ist null");
		if (betrag == 0) log.error("Inputvariable betrag ist 0");
		Order order = null; 
		
		float stueckzahl = 0;
		// Ausfährungskurs festlegen
		float kurs = aktie.getAktuellerKurs().close;
		// aktuellen Bestand ermitteln 
		float wertpapierbestand = this.getWertpapierBestand(aktie.name).bestand;
		if (wertpapierbestand <= 0) log.error("Verkauf ohne Bestand");
		else {	// es ist etwas vorhanden
			// wenn der Bestand kleiner ist als der Verkaufswunsch oder geringer Restbestand 
			if ((wertpapierbestand * kurs - SCHWELLE_VERKAUF_RESTBESTAND) < betrag) {
				// alle vorhandenen Aktien verkaufen
				stueckzahl = wertpapierbestand;
			}
			else {
				stueckzahl = betrag / kurs;
			}
			order = Order.orderAusfuehren(Order.VERKAUF, aktie.name, stueckzahl, this);
		}
		return order; 
	}
	
	/**
	 * geht durch den aktuellen Wertpapierbestand und bewertet mit aktuellen Kursen
	 * @return
	 */
	public float bewerteDepotAktuell () {
		float result = 0;
		if (this.wertpapierbestand.keySet().size() > 0) {
			for (Wertpapierbestand wertpapierbestand : this.wertpapierbestand.values()) {
				float kurs = Aktien.getInstance().getAktie(wertpapierbestand.wertpapier).getAktuellerKurs().getKurs();
				result += kurs * wertpapierbestand.bestand;
			}
		}
		result += this.geld;
		return result; 
	}
	
	/**
	 * Fährt eine Strategiebewertung durch
	 * Ergebnis ist eine Instanz der Strategiebewertung 
	 */
	public void bewerteStrategie() {
		this.strategieBewertung = Strategiebewertung.bewerteStrategie(this);
	}
	
	/**
	 * liefert die aktuelle Stäckzahl im Depotbestand, oder 0
	 * @param name
	 * @return Anzahl Wertpapiere, oder null wenn nicht vorhanden
	 */
	protected Wertpapierbestand getWertpapierBestand (String name) {
		Wertpapierbestand result = null;
		if (this.wertpapierbestand.containsKey(name)) {
			result = this.wertpapierbestand.get(name);
		}
		return result; 
	}
	
	/**
	 * eine ausgefährte Order wird im Orderbuch des Depot eingetragen. 
	 * wird von der Order selbst vorgenommen
	 * Dabei werden die Trades aktualisiert
	 * @param order
	 * @return
	 */
	boolean orderEintragen (Order order) {
		if (order == null) log.error("Inputvariable Order ist null");
		// die Order in die Order-Liste aufnehmen
		this.orders.add(order);
		// das Wertpapier in den Bestand einliefern oder ausliefern 
		this.wertpapiereEinAusliefern(order);
		// die Order einem Trade zuordnen
		addOrderToTrade (order);
		log.debug("neue Order eintragen: " + order.toString());
		
		return true;
	}
	
	/**
	 * kämmert sich um die Ein- und Auslierung der Wertpapier im aktuellen Wertpapierbestand
	 * Falls alle Wertpapiere ausgeliefert wurden, wird der Bestand geläscht 
	 * @param order
	 */
	private void wertpapiereEinAusliefern (Order order) {
		if (order == null) log.error("Inputvariable Order ist null");
		float result = 0;
		if (! this.wertpapierbestand.containsKey(order.wertpapier)) {	// wenn es das Wertpapier noch nicht gibt
			// neues Wertpapier im Bestand anlegen 
			this.wertpapierbestand.put(order.wertpapier, new Wertpapierbestand(order.wertpapier));
		}
		if (order.kaufVerkauf == Order.KAUF) {
		// Wertpapier dem Bestand hinzufägen 
			result = this.wertpapierbestand.get(order.wertpapier).liefereWertpapierEin(order.stueckzahl, order.kurs);
		}
		else {
			result = this.wertpapierbestand.get(order.wertpapier).EntnehmeWertpapier(order.stueckzahl, order.kurs);
		}
		// wenn die Stäckzahl 0 ist, wird der Bestand geläscht 
		if (result > -0.01 && result < 0.01 ) {
			this.wertpapierbestand.remove(order.wertpapier);
		}
	}
	
	
	/**
	 * eine neue Order wird den Trades hinzugefägt. 
	 * Entweder an einen bestehenden Trade angehängt, oder ein neuer Trade eräffnet 
	 * Jede Order gehärt zu einem Trade. 
	 * @param order
	 */
	private void addOrderToTrade (Order order) {
		if (order == null) log.error("Order ist null"); 
		byte status; 
		// wenn es einen aktuellen Trade mit diesem Wertpapier gibt
		if (this.aktuelleTrades.containsKey(order.wertpapier)) {
			// holt sich den Trade 
			Trade trade = this.aktuelleTrades.get(order.wertpapier);
			// fägt die Order dem laufenden Trade hinzu, egal ob Kauf oder Verkauf
			status = trade.addOrder(order);
			log.debug("neue Order an bestehenden Trade: " + this.aktuelleTrades.size() + " - " + trade.toString());
			if (status == Trade.STATUS_GESCHLOSSEN) {  // der Trade ist geschlossen worden 
				// der Trade wird aus der Liste entfernt. 
				this.aktuelleTrades.remove(order.wertpapier);
			}
		}
		else {	// es gibt keinen aktuellen Trade fär diese Order
			if (order.kaufVerkauf == Order.VERKAUF) log.error("Verkauf in's Leere");
			// einen neuen Trade anlegen 
			Trade trade = new Trade (); 
			// die Order hinzufägen
			status = trade.addOrder(order);
			// den neuen Trade einfägen 
			this.aktuelleTrades.put(order.wertpapier, trade);
			this.trades.add(trade);
			log.debug("neue Order an neuen Trade: " + trade.toString());
		}
		
	}
	/**
	 * Schreibt an einem einzigen Tag die relevanten Informationen in die Handels-Tag-CSV
	 * Das File wird zu Beginn erzeugt bleibt während der Simutation geöffnet
	 * Beim Erzeugen des File wird der Header geschrieben. 
	 * Das Schliessen übernimmt die Simulation  
	 * @param depotKurs
	 */
	private void writeHandelstag(Kurs depotKurs) {
		// holt sich das File incl. Header 
		BufferedWriter privateFileWriter = getHandelstagFile();
		// schreibt weitere Einträge in das bestehende File 
		try {
			privateFileWriter.append(toStringHandelstag(depotKurs));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * die Header-Zeile mit den Spaltenäberschriften 
	 * @return
	 */
	private String toStringHandelstagHeader () {
		String result = "Datum" + Util.separatorCSV + 
				"Depotwert"  + Util.separatorCSV;
		for (Aktie aktie : this.aktien) {
			result = result.concat(aktie.name + Util.separatorCSV);
			for (IndikatorAlgorithmus indikator : aktie.getIndikatorAlgorithmen()) {
				result = result.concat(indikator.toString() + Util.separatorCSV);
			}
		}
		result = result.concat("Signal1" + Util.separatorCSV + "Signal2" + Util.separatorCSV + "Signal3" + 
				Util.getLineSeparator());
		return result; 
	}
	
	/**
	 * am Abend eines Handesltages wird der Ablauf protokolliert 
	 * Datum - Depotwert - Aktienkurs / n*Indikator - 3*Signal
	 * @param depotKurs
	 * @return
	 */
	private String toStringHandelstag(Kurs depotKurs) {
		StringBuilder result = new StringBuilder();
		// zu Beginn das Datum einstellen 
		result.append(DateUtil.formatDate(this.heute) + Util.separatorCSV);
		
		result.append(depotKurs.getKurs() + Util.separatorCSV);
		// der Kurs jeder Aktie
		for (Aktie aktie : this.aktien) {
			Kurs kurs = aktie.getAktuellerKurs();

			result.append(aktie.getAktuellerKurs().getKurs() + Util.separatorCSV);
			// fär jede Aktie die Indikatoren
			for (IndikatorAlgorithmus indikator : aktie.getIndikatorAlgorithmen()) {
				// die Indikatoren-Wert am Kurs auslesen
				float wert = kurs.getIndikatorWert(indikator);
				result.append(wert + Util.separatorCSV);
			}
		}
		// fär jede Aktie die Signale 
		for (Aktie aktie : this.aktien) {
			Kurs kurs = aktie.getAktuellerKurs();
			for (Signal signal : kurs.getSignale()) {
				result.append(kurs.wertpapier + " _ " + signal.getKaufVerkauf() + " _ " + signal.getSignalBeschreibung().getSignalTyp() + Util.separatorCSV);
			}
		}
		result.append(Util.getLineSeparator());

		return result.toString(); 
		
	}
	/**
	 * Erzeugt ein neues File, in das die Handelstage protokolliert werden können. 
	 * Zu Beginn wird das File initialisiert und der Header eingetragen. 
	 * @return immer das File, nie null 
	 */
	private BufferedWriter getHandelstagFile () {
		// statische Variable als Speicher 
		if (fileWriterHandelstag == null) {
			String dateiname = "Depothandel" + this.name + Long.toString(System.currentTimeMillis());
			try {
				// das Java File-Objekt erzeugen 
				File file = new File(dateiname + ".csv");
				// mit dem Java-Objekt ein File auf der Platte erzeugen. 
				boolean createFileResult = file.createNewFile();
				if(!createFileResult) {
					// Die Datei konnte nicht erstellt werden. Evtl. gibt es diese Datei schon?
					log.error("Datei konnte nicht erstellt werden:" + dateiname);
				}
				FileWriter fileWriter = new FileWriter(file);
				fileWriterHandelstag = new BufferedWriter(fileWriter); 
				// beim Erzeugen des File wird der Header geschrieben. 
				fileWriterHandelstag.write(toStringHandelstagHeader());

			} catch (Exception e) {log.error("File konnte nicht eröffnet werden: " + dateiname); }
			log.info("File erstellt: " + dateiname);
			};
		return fileWriterHandelstag; 
	}
	
	private void closeHandelstagFile () {
		try {
			fileWriterHandelstag.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * schreibt alle Order des Depot als CSV 
	 */
	public void writeOrders () {
		try {
			String dateiname = "depot" + this.name + Long.toString(System.currentTimeMillis());
			File file = new File(dateiname + ".csv");
			boolean createFileResult = file.createNewFile();
			if(!createFileResult) {
				// Die Datei konnte nicht erstellt werden. Evtl. gibt es diese Datei schon?
				log.info("Die Datei konnte nicht erstellt werden!");
			}
			FileWriter fileWriter = new FileWriter(file);
			writeOrders(fileWriter);
			
			// Zeilenumbruch am Ende der Datei ausgeben
			fileWriter.write(Util.getLineSeparator());
			// Writer schlieäen
			fileWriter.close();
			log.info("Datei geschrieben: " + file.getAbsolutePath() );
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void writeOrders (FileWriter writer) {
		try {
			writer.write("Depot;Wertpapier;KV;Datum;Stäcke;Kurs;Abrechnungsbetrag;Geld;Tradedauer;TradeErfolg");
			writer.write(Util.getLineSeparator());
			for (int i = 0 ; i < this.orders.size(); i++) {
				writer.write(orders.get(i).toString());
				writer.write(Util.getLineSeparator());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
