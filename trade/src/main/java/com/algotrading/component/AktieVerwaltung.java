package com.algotrading.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.aktie.KursAktie;
import com.algotrading.data.DBManager;
import com.algotrading.data.ReadDataFinanzen;
import com.algotrading.jpa.AktieDAO;
import com.algotrading.jpa.KursAktieDAO;

@Service
public class AktieVerwaltung {

	@Autowired
	private AktieDAO aktieDAO;

	@Autowired
	private KursAktieDAO kADAO;

	private AktieVerzeichnis aktieVerzeichnis;

	public Aktie createAktie(Aktie aktie) {
		return aktieDAO.createAktie(aktie);
	}

	public Aktie getAktie(Long id) {
		return aktieDAO.findAktie(id);
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

	public KursAktie saveKursAktie(KursAktie kursAktie) {
		return kADAO.saveKursAktie(kursAktie);
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
