package com.algotrading.aktie;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.data.DBManager;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.util.Zeitraum;

/**
 * Verzeichnis aller Aktien, zu denen Zeitreihen vorhanden sind Es kann sich
 * auch um einen Index handeln Bietet Zugang zu Zeitreihen Als Singleton
 * verfügbar Das Verzeichnis wird sofort initialisiert, die Kurse werden erst
 * bei Bedarf den Kursreihen hinzugefügt. Derzeit keine DB-Lösung, sondern
 * Programmcode
 * 
 * @author oskar
 *
 */
public class AktieVerzeichnis {
	private static final Logger log = LogManager.getLogger(AktieVerzeichnis.class);
	public static final byte BOERSEDEPOT = 0;
	public static final byte BOERSEINDEX = 1;
	public static final byte BOERSENYSE = 2;
	public static final byte BOERSENASDAQ = 3;
	public static final byte BOERSEFRANKFURT = 4;
	public static final byte BOERSEXETRA = 5;

	public static final String INDEXDAX = "dax";
	public static final String INDEXDOWJONES = "dowjones";

	private static AktieVerzeichnis instance;
	// das Verzeichnis aller Aktien
	private static HashMap<String, Aktie> verzeichnis = new HashMap<String, Aktie>();

	private AktieVerzeichnis() {
	}

	/**
	 * Wird benutzt, wenn bestehende Indkator- und Signaldaten an den Kursen weiter
	 * verwendet werden
	 * 
	 * @return eine neue oder eine bestehende Referenz auf Aktien
	 */
	public static AktieVerzeichnis getInstance() {
		if (instance == null) {
			instance = new AktieVerzeichnis();
			// das Verzeichnis wird versorgt
			initialisiereVerzeichnis();
		}
		return instance;
	}

	/**
	 * Wird benutzt, wenn ein neuer Durchlauf gestartet wird bei dem keine
	 * Indikator, und Signaldaten an Aktien und Kursen hängen sollen.
	 * 
	 * @return immer eine neue Referenz auf Aktien.
	 */
	public static AktieVerzeichnis newInstance() {
		instance = new AktieVerzeichnis();
		// das Verzeichnis wird versorgt
		initialisiereVerzeichnis();
		return instance;
	}

	/**
	 * liest und initialisiert eine Aktie anhand eines WP-Namens die Kursreihe ist
	 * eventuell noch nicht gefüllt. Wird beim Zugriff gefüllt.
	 * 
	 * @param name
	 * @return
	 */
	public Aktie getAktieOhneKurse(String name) {
		if (name == null)
			log.error("Inputvariable Wertpapier ist null");
		if (name == "")
			log.error("Inputvariable Wertpapier ist leer");
		name = name.toLowerCase();
		Aktie aktie = null;
		// sucht das Wertpapier im Verzeichnis
		if (verzeichnis.containsKey(name)) {
			aktie = verzeichnis.get(name);
			// zu Beginn sind alle Kursreihen vorhanden, aber ohne Kurse
			if (aktie == null)
				log.error("Aktie ist null : " + name);
		} else
			log.error("Aktie im Verzeichnis nicht vorhanden: " + name);
		return aktie;
	}

	public Aktie getAktieMitKurse(String name) {
		// erst die Aktie ohne Kurse holen
		Aktie aktie = getAktieOhneKurse(name);
		// Kurse laden - bei Bedarf aus DB lesen
		aktie.getKursListe();
		return aktie;
	}

	/**
	 * eine Liste aller registrierter Aktien incl. Indizes
	 */
	public List<Aktie> getAllAktien() {
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
	public List<Aktie> getAktien(GregorianCalendar beginn) {
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
	public List<Aktie> getAktien(Integer land, Integer waehrung) {
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
	public static void addIndikatorAlgorithmus(List<Aktie> aktien, IndikatorAlgorithmus iA) {
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
