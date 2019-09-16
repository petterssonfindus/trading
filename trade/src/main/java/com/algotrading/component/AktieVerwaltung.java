package com.algotrading.component;

import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.data.DBManager;
import com.algotrading.jpa.AktieDAO;

@Service
public class AktieVerwaltung {

	@Autowired
	private AktieDAO aktieDAO;

	private AktieVerzeichnis aktieVerzeichnis;

	public static final int QUELLE_Unbekannt = 0;
	public static final int QUELLE_Yahoo = 1;
	public static final int QUELLE_Finanzen = 2;
	public static final int QUELLE_Ariva = 3;

	public Aktie saveAktie(Aktie aktie) {
		return aktieDAO.saveAktie(aktie);
	}

	public Aktie getAktie(Long id) {
		Aktie aktie = aktieDAO.getAktie(id);
		aktie.setaV(this);
		return aktie;
	}

	public Aktie getAktieMitKurse(Long id) {
		Aktie aktie = aktieDAO.getAktieMitKurse(id);
		aktie.setaV(this);
		return aktie;
	}

	public Aktie getAktie(String name) {
		Aktie aktie = aktieDAO.findByName(name);
		aktie.setaV(this);
		return aktie;
	}

	public Aktie getAktie(Kurs kurs) {
		return getAktie(kurs.getWertpapier());
	}

	public List<Aktie> getAktienListe() {
		return this.getVerzeichnis().getAllAktien();
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

	/**
	 * Gibt die einzige Instanz des Aktienverzeichnis zurück 
	 * Wenn sie bisher null war, wird sie befüllt 
	 */
	private AktieVerzeichnis getVerzeichnis() {
		if (aktieVerzeichnis == null) {
			aktieVerzeichnis = new AktieVerzeichnis();
			aktieVerzeichnis.setVerzeichnis(getAktienAusDB(), this);
		}
		return aktieVerzeichnis;
	}

	public List<Aktie> getAktien(GregorianCalendar beginn) {
		return this.getVerzeichnis().getAktien(beginn);
	}

	public void checkKursreiheTage(String name) {
		Aktie aktie = getAktie(name);
		Aktie referenz = getAktie("dow");
		DBManager.checkKursreiheTage(aktie, referenz);
	}

	public GregorianCalendar getDatumLetzterKurs(Long id) {
		return aktieDAO.getDatumLetzterKurs(id);
	}

	public GregorianCalendar getDatumErsterKurs(Long id) {
		return aktieDAO.getDatumErsterKurs(id);
	}

}
