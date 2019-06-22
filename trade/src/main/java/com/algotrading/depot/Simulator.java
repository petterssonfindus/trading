package com.algotrading.depot;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktien;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

/**
 * Führt Depot-Simulationen durch
 * Speichert die Ergebnisse als .csv
 * @author oskar
 *
 */
public class Simulator {
	private static final Logger log = LogManager.getLogger(Simulator.class);

	ArrayList<Depot> depots = new ArrayList<Depot>();
	/**
	 * Führt eine Reihe von Simulationen durch
	 * An den Aktien hängen die benötigten Indikatoren und die Signalstrategie 
	 * @param aktien Liste von Aktien incl. Indikatoren und Signale 
	 * @param wertpapier
	 * @param beginn
	 * @param ende
	 * @param dauer
	 * @param rhythmus
	 */
	public static void simuliereDepots (
			Aktien aktien, 
			GregorianCalendar beginn, 
			GregorianCalendar ende, 
			int dauer, 
			int rhythmus, 
			SignalStrategie signalStrategie, 
			TagesStrategie tagesStrategie, 
			boolean writeOrders,
			boolean writeHandelstag
			) {
		
		// die Zeitintervalle ermitteln
		ArrayList<Zeitraum> zeitraeume = ermittleZeitraum(beginn, ende, dauer, rhythmus);
		// Alle Indikatoren und Signale an allen Aktien setzen und danach berechnen
		aktien.rechneIndikatorenUndSignale();
		// für jeden Zeitraum wird eine Simulation durchgeführt 
		// dabei werden alle Objekte neu angelegt: Kurse, Aktien, Depot 
		for (Zeitraum zeitraum : zeitraeume) {
			// bereite Depot vor
			Depot depot = new Depot("Oskars", 10000f);
			depot.aktien = aktien; 

			// die Depot-Simulation wird durchgeführt, dabei werden auch Signale berechnet 
			depot.simuliereDepot(signalStrategie, tagesStrategie, aktien, zeitraum.beginn, zeitraum.ende, writeHandelstag);
			
			// auf Wunsch wird pro Simulation csv-Listen erstellt
			if (writeOrders) {
				depot.writeOrders();
			}
			
			// die Ergebnisse der DepotStrategie werden protokolliert 
			log.info(DateUtil.formatDate(zeitraum.beginn) + 
					Util.separatorCSV + DateUtil.formatDate(zeitraum.ende) + 
					depot.strategieBewertung.toString());
			FileWriter fileWriter = openInputOutput();
			writeInputParameter(fileWriter, zeitraum, aktien, signalStrategie, tagesStrategie);
			writeErgebnis(fileWriter, zeitraum, depot);
			closeInputOutput(fileWriter);
		}

	}
	/**
	 * öffnet die StrategieInOut-Datei 
	 */
	private static FileWriter openInputOutput () {
		FileWriter fileWriter = null;
		File file = null; 
		try {
			String dateiname = "C:\\Users\\XK02200\\Documents\\data\\programmierung\\gitgg\\trading\\log\\strategieinout";
			file = new File (dateiname + ".csv");
			if (file.exists()) {
				fileWriter = new FileWriter(file, true);
			}	
			else {
				log.error("Input-OutputFile existiert nicht im Pfad: " + dateiname);
			}
			
		} catch(Exception e) {
			log.error("die InOut-Datei ist vermutlich geäffnet.");
			e.printStackTrace();
		}
		return fileWriter;
	}
	/**
	 * Schliesst die In-Out-Datei 
	 * @param fileWriter
	 */
	private static void closeInputOutput (FileWriter fileWriter) {
		try {
			// Zeilenumbruch am Ende der Datei ausgeben
//			fileWriter.append(Util.getLineSeparator());
			// Writer schlieäen
			fileWriter.close();
			log.info("Datei Strategie-In-Out geschrieben" );
			} catch(Exception e) {
				e.printStackTrace();
		}
	}
	
	private static void writeInputParameter (
			FileWriter fileWriter, 
			Zeitraum zeitraum,
			Aktien aktien,
			SignalStrategie signalStrategie, 
			TagesStrategie tagesStrategie ) {
		String zeile = ""; 
		String indikatoren = aktien.getIndikatoren().toString();
		zeile = zeile.concat(DateUtil.formatDate(zeitraum.beginn) + Util.separatorCSV);
		zeile = zeile.concat(DateUtil.formatDate(zeitraum.ende) + Util.separatorCSV);
		zeile = zeile.concat(Integer.toString(aktien.size()) + Util.separatorCSV);
		zeile = zeile.concat(indikatoren.toString() + Util.separatorCSV);
//		zeile = zeile.concat(signalAlgos.toString() + Util.separatorCSV);
		zeile = zeile.concat(signalStrategie.toString() + Util.separatorCSV);
		// falls eine tagesstrategie vorhanden ist 
		if (tagesStrategie != null) {
			zeile = zeile.concat(tagesStrategie.toString() + Util.separatorCSV);
		}
		zeile = zeile.concat(Util.getLineSeparator());
		try {
			fileWriter.append(zeile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private static void writeErgebnis (FileWriter fileWriter, Zeitraum zeitraum, Depot depot) {
		try {
			fileWriter.append(DateUtil.formatDate(zeitraum.beginn) + 
					Util.separatorCSV + DateUtil.formatDate(zeitraum.ende) + 
					depot.strategieBewertung.toString() + 
					Util.getLineSeparator()
					);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Liefert eine Liste von Zeitraumn anhand der Parameter
	 * @param beginn
	 * @param ende
	 * @param dauer wenn Dauer oder Rhythmus 0 ist, dann gibt es nur 1 Zeitraum 
	 * @param rhythmus
	 * @return
	 */
	static ArrayList<Zeitraum> ermittleZeitraum (
			GregorianCalendar beginn, 
			GregorianCalendar ende, 
			int dauer, 
			int rhythmus
		)
	{
		ArrayList<Zeitraum> result = new ArrayList<Zeitraum>();
		GregorianCalendar neuerBeginn; 
		GregorianCalendar neuesEnde; 
		neuerBeginn = beginn;
		if (dauer == 0 || rhythmus == 0) {
			result.add(new Zeitraum(beginn, ende));
		}
		else {
			do {
				// vom Beginn bis zum Ende liegt die Dauer 
				neuesEnde = DateUtil.addTage(neuerBeginn, dauer); 
				if (neuesEnde.before(ende)) {
					result.add(new Zeitraum(neuerBeginn,neuesEnde));
				}
				neuerBeginn = DateUtil.addTage(neuerBeginn, rhythmus);
			}
			while (neuerBeginn.before(ende));
			
		}
		
		return result; 
	}
}
	
