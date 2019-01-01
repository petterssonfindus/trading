package com.algotrading.aktie;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.data.DBManager;
import com.algotrading.util.Zeitraum;

	/**
	 * Verzeichnis aller Aktien, zu denen Zeitreihen vorhanden sind
	 * Es kann sich auch um einen Index handeln 
	 * Bietet Zugang zu Zeitreihen
	 * Als Singleton verfügbar
	 * Das Verzeichnis wird sofort initialisiert, die Kurse werden erst bei Bedarf den Kursreihen hinzugefügt. 
	 * Derzeit keine DB-Lösung, sondern Programmcode
	 * @author oskar
	 *
	 */
public class Aktien {
	private static final Logger log = LogManager.getLogger(Aktien.class);
	
	public static final byte BOERSEDEPOT = 0;
	public static final byte BOERSEINDEX = 1;
	public static final byte BOERSENYSE = 2;
	public static final byte BOERSENASDAQ = 3;
	public static final byte BOERSEFRANKFURT = 4;
	public static final byte BOERSEXETRA = 5;
	
	public static final String INDEXDAX = "dax";
	public static final String INDEXDOWJONES = "dowjones";
	
	private static Aktien instance;
	// das Verzeichnis aller Aktien 
	private static HashMap<String, Aktie> verzeichnis = new HashMap<String, Aktie>();
	
	private Aktien() {}
	
	/**
	 * Wird benutzt, wenn bestehende Indkator- und Signaldaten an den Kursen weiter verwendet werden
	 * @return eine neue oder eine bestehende Referenz auf Aktien 
	 */
	public static Aktien getInstance() {
		if (instance == null) {
			instance = new Aktien();
			// das Verzeichnis wird versorgt 
			initialisiereVerzeichnis();
		}
		return instance; 
	}
	
	/**
	 * Wird benutzt, wenn ein neuer Durchlauf gestartet wird
	 * bei dem keine Indikator, und Signaldaten an Aktien und Kursen hängen sollen.  
	 * @return immer eine neue Referenz auf Aktien. 
	 */
	public static Aktien newInstance() {
		instance = new Aktien();
		// das Verzeichnis wird versorgt 
		initialisiereVerzeichnis();
		return instance; 
	}

	/**
	 * liest und initialisiert eine Aktie anhand eines WP-Namens
	 * die Kursreihe ist eventuell noch nicht gefüllt. 
	 * Wird beim Zugriff gefüllt. 
	 * @param wertpapier
	 * @return
	 */
	public Aktie getAktie (String wertpapier) {
		if (wertpapier == null) log.error("Inputvariable Wertpapier ist null");
		if (wertpapier == "") log.error("Inputvariable Wertpapier ist leer");
		wertpapier = wertpapier.toLowerCase();
		Aktie aktie = null; 
		// sucht das Wertpapier im Verzeichnis
		if (verzeichnis.containsKey(wertpapier) ) {
			aktie = verzeichnis.get(wertpapier);
			// zu Beginn sind alle Kursreihen vorhanden, aber ohne Kurse 
			if (aktie == null) log.error("Aktie ist null : " + wertpapier);
		}
		else log.error("Aktie im Verzeichnis nicht vorhanden: " + wertpapier);
		return aktie; 
	}
	
	/**
	 * eine Liste aller registrierter Aktien incl. Indizes
	 */
	public ArrayList<Aktie> getAllAktien () {
		ArrayList<Aktie> result = new ArrayList<Aktie>();
		for (Aktie aktie : verzeichnis.values()) {
			result.add(aktie);
		}
		return result;
	}
	/**
	 * Alle Aktien und Indizes, bei denen Kurse innerhalb des gewünschten Zeitraums vorhanden sind 
	 * @param mitIndizes 
	 * @return Liste von Aktien mit Kursreihen
	 */
	public ArrayList<Aktie> getAktien (Zeitraum zeitraum, boolean mitIndizes) {
		ArrayList<Aktie> result = new ArrayList<Aktie>();
		long beginn = zeitraum.beginn.getTimeInMillis();
		long ende = zeitraum.ende.getTimeInMillis();
		for (Aktie aktie : verzeichnis.values()) {
			boolean test = false; 
			if (aktie.getZeitraumKurse().beginn.getTimeInMillis() < beginn &&  
					aktie.getZeitraumKurse().ende.getTimeInMillis() > ende) {
				test = true; 
				if (mitIndizes) {
					String substring = aktie.name.substring(0, 3);
					if (substring == "xxx") {
						test = false;
					}
				}
				result.add(aktie);
			}
		}
		return result;
		
	}
	
	/**
	 * das Verzeichnis wird beim Erstellen initialisiert aus der DB 
	 */
	private static void initialisiereVerzeichnis() {
		verzeichnis = DBManager.getVerzeichnis();
	}

}
