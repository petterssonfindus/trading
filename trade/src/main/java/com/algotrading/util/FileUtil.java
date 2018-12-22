package com.algotrading.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

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
	 */
	public static ArrayList<String> readContent (File file) {
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
	 * Aus einem Filenamen im Standard-Pfad ein bestehendes File öffnen 
	 */
	public static File openFile (String filename) {
		String pfad = getLogPfad() + filename ;
		File file = new File(pfad);
		return file; 
		
	}
	
	/**
	 * Der Pfad zum Log-Verzeichnis 
	 */
	public static String getLogPfad () {
		return "C:\\Users\\XK02200\\Documents\\data\\programmierung\\gittrade\\trade\\log\\";
	}
	
	/**
	 * Schreibt eine Datei mit dem übergebenen Inhalt 
	 * pro String eine Zeile
	 */
	public static File writeFile (ArrayList<String> zeilen, String dateiname, String extension) {
		File file = FileUtil.createFile(dateiname, extension);
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
	static FileWriter createFileWriter (File file) {
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
	 * ermittelt den kompletten Pfad zur angegebenen Datei 
	 */
	public static File createFile (String dateiname, String extension) {
		File file = null; 
		String filename = "";
		try {
			filename =  getLogPfad() + dateiname;
			file = new File (filename + "." + extension);
			
		} catch(Exception e) {
			Util.log.error("das File ist vermutlich geöffnet:" + filename);
			e.printStackTrace();
		}
		return file; 
	}

}
