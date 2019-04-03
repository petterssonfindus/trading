package com.algotrading.aktie;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.algotrading.data.DBManager;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.util.DateUtil;
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
	@Autowired
	private AktieRepository aktieRepository;
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
	public List<Aktie> getAllAktien () {
		List<Aktie> result = new ArrayList<Aktie>();
		for (Aktie aktie : verzeichnis.values()) {
			result.add(aktie);
		// ein Versuch, das Aktien-Verzeichnis über JPA zu persistieren wurde verworfen
//			aktieRepository.save(aktie);
		}
		return result;
	}
	/**
	 * Alle Aktien und Indizes, bei denen Kurse rückwirkend vorliegen bis zum Beginn
	 */
	public List<Aktie> getAktien (GregorianCalendar beginn) {
		List<Aktie> result = new ArrayList<Aktie>();
		for (Aktie aktie : verzeichnis.values()) {
			Zeitraum zeitraum = aktie.getZeitraumKurse();
			if (zeitraum != null && zeitraum.beginn != null && zeitraum.beginn.before(beginn)) {
				result.add(aktie);
			}
		}
		return result;
	}
	
	/**
	 * Alle Aktien mit diesen Suchkriterien 
	 */
	public List<Aktie> getAktien (Integer land, Integer waehrung) {
		List<Aktie> result = new ArrayList<Aktie>();
		for (Aktie aktie : verzeichnis.values()) {
			if (land == null || aktie.getLand() == land) {
				if (waehrung == null && aktie.getWaehrung() == waehrung) {
					result.add(aktie);
				}
			}
		}
		return result;
	}
	
	/**
	 * Hängt an jede Aktie einer Aktien-Liste einen Indikator 
	 */
	public static void addIndikatorAlgorithmus (List<Aktie> aktien, IndikatorAlgorithmus iA) {
		for (Aktie aktie : aktien) {
			aktie.addIndikatorAlgorithmus(iA);
		}
	}
	
	
	/**
	 * das Verzeichnis wird beim Erstellen initialisiert aus der DB 
	 */
	private static void initialisiereVerzeichnis() {
		verzeichnis = DBManager.getVerzeichnis();
		
	}

}
