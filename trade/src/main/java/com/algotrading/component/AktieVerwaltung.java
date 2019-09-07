package com.algotrading.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.data.DBManager;
import com.algotrading.data.ReadDataFinanzen;
import com.algotrading.jpa.AktieDAO;

@Service
public class AktieVerwaltung {

	@Autowired
	private AktieDAO aktieDAO;

	private AktieVerzeichnis aktieVerzeichnis;

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
		return aktieDAO.findByName(name);
	}

	public Aktie getAktie(Kurs kurs) {
		return getAktie(kurs.getWertpapier());
	}

	/**
	 * Ermittelt das Ergebnis aus der Datenbank 
	 */
	public Iterable<Aktie> getAll() {
		return aktieDAO.findAll();
	}

	public Aktien getAktien() {
		Aktien aktien = new Aktien(this);
		aktien.addIterable(getAll());
		return aktien;
	}

	public Aktien createAktien() {
		return new Aktien(this);
	}

	/**
	 * Gibt die einzige Instanz des Aktienverzeichnis zurück 
	 * Wenn sie bisher null war, wird sie befüllt 
	 */
	public AktieVerzeichnis getVerzeichnis() {
		if (aktieVerzeichnis == null) {
			aktieVerzeichnis = AktieVerzeichnis.newInstance();
			aktieVerzeichnis.setAktien(getAll());
		}
		return aktieVerzeichnis;
	}

	public void checkKursreiheTage(String name) {
		Aktie aktie = getAktie(name);
		Aktie referenz = getAktie("dow");
		DBManager.checkKursreiheTage(aktie, referenz);
	}

	public void FinanzenWSAktienController() {
		ReadDataFinanzen.FinanzenWSAktienController(getAktien());
	}

}
