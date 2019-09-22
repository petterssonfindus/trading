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

	public Aktie getAktieLazy(Long id) {
		Aktie aktie = this.getVerzeichnis().getAktieOhneKurse(id);
		aktie.setaV(this);
		return aktie;
	}

	public Aktie getAktieLazy(String name) {
		Aktie aktie = this.getVerzeichnis().getAktieOhneKurse(name);
		aktie.setaV(this);
		return aktie;
	}

	public Aktie getAktieLazy(Kurs kurs) {
		return getAktieLazy(kurs.getAktieName());
	}

	public Aktie getAktieMitKurse(Long id) {
		Aktie aktie = this.getVerzeichnis().getAktieMitKurse(id);
		aktie.setaV(this);
		return aktie;
	}

	public Aktie getAktieMitKurseNew(Long id) {
		return aktieDAO.getAktieMitKurse(id);
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

}
