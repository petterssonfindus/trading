package com.algotrading.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.component.AktieVerwaltung;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Util;

/**
 * Importiert CSV-Dateien mit Kursdaten 
 * @author oskar
 */
@Service
public class ImportCSV {
	private static final Logger log = LogManager.getLogger(ImportCSV.class);

	@Autowired
	private AktieVerwaltung aV;

	//	static String pfad = "/home/oskar/Documents/finance/DAXkurz1.csv";
	//	static String pfad = "/home/oskar/Documents/finance/";

	/**
	 * C:\\Users\\xk02200\\Aktien\\nysekurse\\
	 * @return
	 */
	private static String getPfadCSV() {
		return Util.getUserProperty("home") + Util.getFileSeparator() + "Aktien" + Util
				.getFileSeparator() + "nysekurse" + Util.getFileSeparator();
	}

	private static String getPfadCSVAriva() {
//		@formatter:off
		return Util.getUserProperty("home") + Util.getFileSeparator() + 
				"Documents" + Util.getFileSeparator() + 
				"data" + Util.getFileSeparator() + 
				"programmierung" + Util.getFileSeparator() + 
				"gittrade" + Util.getFileSeparator() + 
				"trade" + Util.getFileSeparator() + 
				"csv" + Util.getFileSeparator() + 
				"kurse" + Util.getFileSeparator();
		//		C:\Users\XK02200\Documents\data\programmierung\gittrade\trade\csv\kurse
//		@formatter:on
	}

	private static File getCSVFileAriva(String dateiName) {
		String pfad = getPfadCSVAriva() + dateiName + ".csv";
		File file = new File(pfad);
		return file;
	}

	/**
	 * Steuert das Einlesen aller CSV-Dateien, die sich im o.g. Pfad befinden. 
	 * Erzeugt Tabellen mit dem Dateinamen als Kürzel mit allen enthaltenen Kursen. 
	 * Es dürfen nur csv-Files enthalten sein #TODO die anderen Files ignorieren
	 * Ist die Tabelle vorhanden, geschieht nichts. 
	 */
	public void readPfadKurseYahooCSV() {
		// holt sich alle Dateien im o.g. Verzeichnis 
		File[] directoryListing = getCSVFilesInPath();
		ImportKursreihe importkursreihe;
		if (directoryListing != null) {
			// Iteriert über alle enthaltenen Dateien. 
			for (File child : directoryListing) {
				log.info("File: " + child.getName());
				// erzeugt eine ImportKursreihe aus den CSV-Einträgen.
				importkursreihe = readKurseYahooCSV(child);
				if (importkursreihe != null) {
					// erzeugt eine neue Tabelle mit dem Kürzel, falls noch keine vorhanden ist
					if (DBManager.schreibeNeueAktieTabelle(importkursreihe.kuerzel)) {
						// schreibt die Kurse in die neue Tabelle
						DBManager.schreibeKurse(importkursreihe);
					}
				} else
					log.error("ImportKursreihe fehlerhaft: " + importkursreihe.kuerzel);
			}
		} else {
			log.error("Pfad ist leer ");
		}
	}

	/**
	 * Liest eine einzelne Datei aus dem Standard-csv-Pfad. 
	 */
	public static File getCSVFile(String name) {
		String pfad = getPfadCSV() + Util.getFileSeparator() + name + ".csv";
		File file = new File(pfad);
		return file;
	}

	protected static File[] getCSVFilesInPath() {
		// holt sich den Pfad
		File dir = new File(getPfadCSV());
		// die Liste aller Files 
		return dir.listFiles();

	}

	/**
	 * Liest eine csv-Datei mit Kursen von Ariva ein 
	 * Die Kurse werden an die Aktie angehängt 
	 * @param file die csv-Datei 
	 * @param name Name der Aktie in den Kurse-Stammdaten
	 * @return die Kursreihe aus der man DB-Einträge erzeugen kann 
	 */
	@Transactional
	public Aktie readKurseArivaCSV(String fileName, Aktie aktie) {
		String line = "";
		String cvsSplitBy = ";";
		String[] zeile = null;

		File file = getCSVFileAriva(fileName);

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			log.info("CSV-Datei einlesen: " + file.getName());
			// erste Zeile enthält die Überschriften
			br.readLine();

			while ((line = br.readLine()) != null) {
				if (line.length() < 5)
					continue;
				// erst die Tausender-Punkte entfernen 
				line = line.replace(".", "");
				// dann die Dezimal-Trennung "," durch Punkt ersetzen 
				line = line.replaceAll(",", ".");
				// die Zeile aufteilen anhand der Trennzeichen 
				zeile = line.split(cvsSplitBy);
				// wenn die erste Spalte "null" enthält wird die Zeile ignoriert 
				if (!zeile[1].contains("null")) {
					Kurs kurs = new Kurs();
					aktie.addKurs(kurs);

					try {
						kurs.setDatum(DateUtil.parseDatum(zeile[0]));
					} catch (Exception e1) {
						continue;
					}

					try {
						kurs.open = parseFloat(zeile[1]);
					} catch (Exception e1) {

					}

					try {
						kurs.high = parseFloat(zeile[2]);
					} catch (Exception e1) {
					}

					try {
						kurs.low = parseFloat(zeile[3]);
					} catch (Exception e1) {
					}

					try {
						kurs.close = parseFloat(zeile[4]);
					} catch (Exception e1) {
						continue;
					}

					// nicht immer sind die Spalten AdjClose und Volume vorhanden 
					if (zeile.length > 5) {
						try {
							kurs.adjClose = parseFloat(zeile[5]);
						} catch (Exception e) {
							// kein Problem, wenn das nicht funktioniert 
						}
						// Indizes haben als Volume Gleitkommazahlen, deshalb wird gecasted 
						/*
						try {
							kurs.volume = (int) Float.parseFloat(zeile[6]);
						} catch (NumberFormatException e) {
							// keine Problem, wenn das nicht funktioniert 
						}
						*/
					}
				}  // end if zeile.contains(null) 
			}  // end try Buffered Reader 

		} catch (IOException e) {
			log.error("Feher beim Einlesen Datei: " + file.getAbsolutePath());
			e.printStackTrace();
		} catch (NumberFormatException e) {
			log.error("NumberFormatException beim String: " + zeile.toString());
			e.printStackTrace();
		}
		aV.createAktie(aktie);
		return aktie;
	}

	private static Float parseFloat(String input) {
		// Prüfung auf leeren Inhalt
		if (input == null || input.length() == 0 || input.compareTo(" ") == 0)
			return null;
		Float result = null;

		try {
			result = Float.parseFloat(input);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * liest eine csv-Datei von Yahoo-Finance ein
	 * Zeilen mit 'null'-Werten werden ignoriert 
	 * Aus jeder Zeile wird ein Kurs erzeugt
	 * @return
	 */
	@Transactional
	public Aktie readKurseYahooCSV(String fileName, Aktie aktie) {
		String line = "";
		String cvsSplitBy = ",";
		File file = getCSVFile(fileName);

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			log.info("CSV-Datei einlesen: " + file.getName());
			// erste Zeile enthält die Überschriften
			br.readLine();

			while ((line = br.readLine()) != null) {

				String[] zeile = line.split(cvsSplitBy);
				// wenn der erste Kurs "null" enthält wird die Zeile ignoriert 
				if (!zeile[1].contains("null")) {
					Kurs kurs = new Kurs();
					kurs.setDatum(DateUtil.parseDatum(zeile[0]));
					kurs.open = Float.parseFloat(zeile[1]);
					kurs.high = Float.parseFloat(zeile[2]);
					kurs.low = Float.parseFloat(zeile[3]);
					kurs.close = Float.parseFloat(zeile[4]);
					// nicht immer sind die Spalten AdjClose und Volume vorhanden 
					if (zeile.length > 5) {
						try {
							kurs.adjClose = Float.parseFloat(zeile[5]);
						} catch (NumberFormatException e) {
							// kein Problem, wenn das nicht funktioniert 
						}
						// Indizes haben als Volume Gleitkommazahlen, deshalb wird gecasted 
						try {
							kurs.volume = (int) Float.parseFloat(zeile[6]);
						} catch (NumberFormatException e) {
							// keine Problem, wenn das nicht funktioniert 
						}
					}
					// wenn alles funktioniert hat, wird der Kurs an die Aktie gehängt
					aktie.addKurs(kurs);
				}
			}

		} catch (IOException e) {
			log.error("Feher beim Einlesen Datei: " + file.getAbsolutePath());
			e.printStackTrace();
		}
		aV.createAktie(aktie);
		System.out.println("Kurs eingelesen " + aktie.getName() + " - " + aktie.getKursListe().size());
		return aktie;
	}

}
