package com.algotrading.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtil {
	static final Logger log = LogManager.getLogger(FileUtil.class);

	/**
	 * Öffnet das File und liest den Inhalt zeilenweise 
	 */
	public static ArrayList<String> readContent(String filename) {
		File file = FileUtil.openFile(filename);
		return FileUtil.readContent(file);
	}

	/**
	 * Liest anhand eines Files den Inhalt zeilenweise 
	 * Zum Einlesen der Kurse aus verschiedenen Quellen 
	 */
	public static ArrayList<String> readContent(File file) {
		ArrayList<String> result = new ArrayList<String>();
		FileReader fr = null;
		// ein File-Reader erzeugen 
		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			log.error("Datei konnte nicht geöffnet werden: " + file.getName());
		}
		// mit dem BufferedReader kann man zeilenweise lesen 
		BufferedReader br = new BufferedReader(fr);
		{
			log.info("File einlesen: " + file.getName());
			String zeile;
			try {
				while ((zeile = br.readLine()) != null) {
					result.add(zeile);
				}
			} catch (IOException e) {
				log.error("Fehler beim Lesen der Datei: " + file.getName());
			}
		}
		return result;

	}

	/**
	 * Aus einem Filenamen im Kurs-Pfad ein bestehendes File öffnen 
	 */
	public static File openFile(String filename) {
		String pfad = getPfadKurs() + filename;
		File file = new File(pfad);
		return file;

	}

	/**
	 * Der Pfad zum Log-Verzeichnis 
	 */
	private static String getPfadLog() {
		return "C:\\Users\\XK02200\\Documents\\data\\programmierung\\gittrade\\trade\\log\\";
	}

	/**
	 * Der Pfad zum csv-Verzeichnis 
	 */
	private static String getPfadCSV() {
		return "C:\\Users\\XK02200\\Documents\\data\\programmierung\\gittrade\\trade\\csv\\";
	}

	/**
	 * Der Pfad zum Kurs-Verzeichnis für die Datei-Speicherung von Kursdaten 
	 */
	private static String getPfadKurs() {
		return "C:\\Users\\XK02200\\Documents\\data\\programmierung\\gittrade\\trade\\csv\\";
	}

	/**
	 * Schreibt eine Datei mit dem übergebenen Inhalt 
	 * pro String eine Zeile
	 */
	public static File writeCSVFile(List<String> zeilen, String dateiname) {
		File file = FileUtil.createCSVFile(dateiname);
		FileWriter fileWriter = FileUtil.createFileWriter(file);
		for (String zeile : zeilen) {
			try {
				fileWriter.write(zeile + Util.getLineSeparator());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file;
	}

	/**
	 * erzeugt einen FileWriter im Standard-Ausgabeverzeichnis
	 * wenn etwas schief geht, dann return = null
	 */
	static FileWriter createFileWriter(File file) {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fileWriter;
	}

	/**
	 * ermittelt den kompletten Pfad zur angegebenen csv-Datei 
	 */
	public static File createCSVFile(String dateiname) {
		File file = null;
		String filename = "";
		try {
			filename = getPfadCSV() + dateiname;
			file = new File(filename + ".csv");

		} catch (Exception e) {
			Util.log.error("das File ist vermutlich geöffnet:" + filename);
			e.printStackTrace();
		}
		return file;
	}

}
