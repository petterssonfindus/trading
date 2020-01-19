package com.algotrading.component;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.algotrading.aktie.Aktie;
import com.algotrading.jpa.AktieDAO;
import com.algotrading.util.Zeitraum;

/**
 * Dient als Zugang und Cache zu allen Aktien im jeweiligen Zustand
 * Erreichbarkeit nur über die AktieVerwaltung
 * @author oskar
 */
public class AktieCache {

	private static final Logger log = LogManager.getLogger(AktieCache.class);
	public static final byte BOERSEDEPOT = 0;
	public static final byte BOERSEINDEX = 1;
	public static final byte BOERSENYSE = 2;
	public static final byte BOERSENASDAQ = 3;
	public static final byte BOERSEFRANKFURT = 4;
	public static final byte BOERSEXETRA = 5;

	public static final String INDEXDAX = "dax";
	public static final String INDEXDOWJONES = "dowjones";

	// Aktien-Cache mit Name 
	private HashMap<String, Aktie> cacheName = new HashMap<String, Aktie>();

	// Aktien-Cache mit ID
	private HashMap<Long, Aktie> cache = new HashMap<Long, Aktie>();

	@Autowired
	private AktieVerwaltung aV;

	@Autowired
	private AktieDAO aktieDAO;

	/**
	 * Initialisiert den Cache mit Aktien-Stammdaten 
	 */
	protected AktieCache(Iterable<Aktie> aktien, AktieVerwaltung aV) {
		this.setaV(aV);
		for (Aktie aktie : aktien) {
			this.cacheName.put(aktie.getName(), aktie);
			this.cache.put(aktie.getId(), aktie);
			aktie.setaV(aV);
		}
	}

	/**
	 * liest und initialisiert eine Aktie anhand eines WP-Namens die Kursreihe ist
	 * eventuell noch nicht gefüllt. Wird beim Zugriff gefüllt.
	 */
	protected Aktie getAktieOhneKurse(String name) {
		if (name == null)
			log.error("Inputvariable Wertpapier ist null");
		if (name == "")
			log.error("Inputvariable Wertpapier ist leer");

		name = name.toLowerCase();
		Aktie aktie = null;
		// sucht das Wertpapier im Verzeichnis
		if (this.getCacheName().containsKey(name)) {
			aktie = cacheName.get(name);
			if (aktie == null)
				log.error("Aktie ist null : " + name);
		} else
			log.error("Aktie im Verzeichnis nicht vorhanden: " + name);
		// die Aktie bekommt Zugriff auf das AktieVerzeichnis. Darüber können die Kurse nach-gelesen werden. 
		aktie.setaV(this.getaV());
		return aktie;
	}

	protected Aktie getAktieOhneKurse(long id) {
		Aktie aktie = null;
		// sucht das Wertpapier im Verzeichnis
		if (this.getCache().containsKey(id)) {
			aktie = cache.get(id);
			if (aktie == null)
				log.error("Aktie ist null : " + id);
		} else
			log.error("Aktie im Verzeichnis nicht vorhanden: " + id);
		return aktie;
	}

	protected Aktie getAktieMitKurse(long id) {
		// erst die Aktie ohne Kurse holen
		Aktie aktie = this.getAktieOhneKurse(id);
		// Kurse laden - bei Bedarf aus DB lesen
		aktie.getKursListe();
		return aktie;
	}

	protected Aktie getAktieMitKurse(String name) {
		// erst die Aktie ohne Kurse holen
		Aktie aktie = getAktieOhneKurse(name);
		// Kurse laden - bei Bedarf aus DB lesen
		aktie.getKursListe();
		return aktie;
	}

	/**
	 * eine Liste aller registrierter Aktien aus dem Cache
	 */
	protected List<Aktie> getAllAktien() {
		List<Aktie> result = new ArrayList<Aktie>();
		for (Aktie aktie : cacheName.values()) {
			result.add(aktie);
		}
		return result;
	}

	/**
	 * Alle Aktien und Indizes, bei denen Kurse rückwirkend vorliegen bis zum Beginn
	 */
	protected List<Aktie> getAktien(GregorianCalendar beginn) {
		List<Aktie> result = new ArrayList<Aktie>();
		for (Aktie aktie : cacheName.values()) {
			Zeitraum zeitraum = aktie.getZeitraumKurse();
			if (zeitraum != null && zeitraum.beginn != null && zeitraum.beginn.before(beginn)) {
				result.add(aktie);
			}
		}
		return result;
	}

	private AktieVerwaltung getaV() {
		return aV;
	}

	private void setaV(AktieVerwaltung aV) {
		this.aV = aV;
	}

	private HashMap<String, Aktie> getCacheName() {
		return cacheName;
	}

	private HashMap<Long, Aktie> getCache() {
		return cache;
	}

}
