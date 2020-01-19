package com.algotrading.component;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.data.DBManager;
import com.algotrading.jpa.AktieDAO;
import com.algotrading.uimodel.UIAktie2Kurse;
import com.algotrading.uimodel.UIFileText;
import com.algotrading.util.RestApplicationException;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

@Service
public class AktieVerwaltung {

	private static final Logger log = LogManager.getLogger(AktieVerwaltung.class);

	@Autowired
	private AktieDAO aktieDAO;

	@Autowired
	private AktieCache aktieCache;

	public static final int QUELLE_Unbekannt = 0;
	public static final int QUELLE_Yahoo = 1;
	public static final int QUELLE_Finanzen = 2;
	public static final int QUELLE_Ariva = 3;

	/**
	 * Gibt die einzige Instanz des Aktien-Verzeichnisses zurück 
	 * 
	 */
	@Bean
	public AktieCache getCache() {
		return new AktieCache(getAktienAusDB(), this);
	}

	public Aktie updateAktie(Aktie aktie) {
		return aktieDAO.saveAktie(aktie);
	}

	public Aktie createAktie(Aktie aktie) {
		return aktieDAO.saveAktie(aktie);
	}

	/**
	 * Standard-Zugriff auf die Aktie ohne Kurse 
	 */
	public Aktie getAktieLazy(Long id) {
		Aktie aktie = aktieCache.getAktieOhneKurse(id);
		if (aktie == null) {
			throw new RestApplicationException("Aktie nicht gefunden: " + id, HttpStatus.NOT_FOUND);
		}
		aktie.setaV(this);
		return aktie;
	}

	public Aktie getAktieLazy(String name) {
		Aktie aktie = aktieCache.getAktieOhneKurse(name);
		aktie.setaV(this);
		return aktie;
	}

	public Aktie getAktieLazy(Kurs kurs) {
		return this.getAktieLazy(kurs.getAktieName());
	}

	public Aktie getAktieMurKurse(String name) {
		Aktie aktieLazy = getAktieLazy(name);
		return getAktieMitKurse(aktieLazy.getId());
	}

	public Aktie getAktieMitKurse(Long id) {
		Aktie aktie = aktieCache.getAktieMitKurse(id);
		aktie.setaV(this);
		return aktie;
	}

	/**
	 * Nutzt nicht den Cache, sondern geht direkt zur DB.
	 * Wird verwendet aus der Aktie zum Nach-Lesen der Kurse
	 */
	public Aktie getAktieMitKurseFromDB(Long id) {
		return aktieDAO.getAktieFromDB(id);
	}

	public List<Aktie> getAktienListe() {
		return aktieCache.getAllAktien();
	}

	public List<UIAktie2Kurse> getAktienListe2Kurse() {
		List<UIAktie2Kurse> result = new ArrayList<>();
		List<Aktie> liste = getAktienListe();
		for (Aktie aktie : liste) {
			result.add(new UIAktie2Kurse(aktie));
		}
		return result;
	}

	/**
	 * Ermittelt das Ergebnis aus der Datenbank 
	 */
	public Iterable<Aktie> getAktienAusDB() {
		return aktieDAO.findAll();
	}

	public void deleteAktie(Aktie aktie) {
		aktieDAO.deleteAktie(aktie);
	}

	public void deleteAktieByID(Long id) {
		aktieDAO.deleteAktieByID(id);
	}

	public Aktien getAktien() {
		Aktien aktien = new Aktien(this);
		aktien.addIterable(getAktienAusDB());
		return aktien;
	}

	public Aktien createAktien() {
		return new Aktien(this);
	}

	public List<Aktie> getAktien(GregorianCalendar beginn) {
		return aktieCache.getAktien(beginn);
	}

	public void checkKursreiheTage(String name) {
		Aktie aktie = getAktieLazy(name);
		Aktie referenz = getAktieLazy("dow");
		DBManager.checkKursreiheTage(aktie, referenz);
	}

	public GregorianCalendar getDatumLetzterKurs(Long id) {
		return aktieDAO.getDatumLetzterKurs(id);
	}

	public GregorianCalendar getDatumErsterKurs(Long id) {
		return aktieDAO.getDatumErsterKurs(id);
	}

	public Kurs getErsterKurs(Long id) {
		return aktieDAO.getErsterKurs(id);
	}

	public Kurs getLetzterKurs(Long id) {
		return aktieDAO.getLetzterKurs(id);
	}

	public List<Kurs> getErsterLetzterKurs(Long id) {
		List<Kurs> liste = new ArrayList<>();
		liste.add(getErsterKurs(id));
		liste.add(getLetzterKurs(id));
		return liste;
	}

	public int getAnzahlKurse(Long id) {
		Aktie aktie = getAktieMitKurse(id);
		List<Kurs> kurse = aktie.getKurse();
		return kurse.size();
	}

	/**
	 * Rechnet die Performance der Aktie im gewünschten Zeitraum
	 */
	public float rechnePerformance(Aktie aktie, Zeitraum zeitraum) {
		if (zeitraum == null) {
			log.error("Performance-Berechnung mit Zeitraum = null");
		}
		Kurs kursBeginn = aktie.getKurs(zeitraum.beginn);
		if (kursBeginn == null) {
			log.error("Performance-Berechnung mit Kursbeginn = null");
		}
		Kurs kursEnde = aktie.getKurs(zeitraum.ende);
		if (kursEnde == null) {
			log.error("Performance-Berechnung mit Kursende = null");
		}
		return (float) Util.rechnePerformancePA(kursBeginn.getKurs(), kursEnde.getKurs(), zeitraum.getHandestage());
	}

	public UIFileText writeFileKursIndikatorSignal(Aktie aktie) {
		return aktie.writeFileKursIndikatorSignal();
	}

}
