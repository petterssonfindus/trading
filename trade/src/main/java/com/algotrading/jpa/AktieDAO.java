package com.algotrading.jpa;

import java.util.GregorianCalendar;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

@Component
public class AktieDAO {

	@Autowired
	AktieRepository aR;

	@Transactional
	public Aktie saveAktie(Aktie aktie) {
		return aR.save(aktie);
	}

	@Transactional
	public Aktie getAktie(Long id) {
		Optional<Aktie> result = aR.findById(id);
		if (result.isPresent())
			return result.get();
		return null;
	}

	@Transactional
	public Aktie getAktieMitKurse(Long id) {
		Optional<Aktie> result = aR.findById(id);
		if (result.isPresent()) {
			Aktie aktie = result.get();
			aktie.getKurse().size();
			return aktie;
		}
		return null;
	}

	@Transactional
	public void deleteAktie(Aktie aktie) {
		aR.delete(aktie);
	}

	@Transactional
	public void deleteAktieByID(Long id) {
		aR.deleteById(id);
	}

	public Iterable<Aktie> findAll() {
		return aR.findAll();
	}

	public Aktie findByName(String name) {
		Iterable<Aktie> result = aR.findByName(name);
		if (result != null && result.iterator().hasNext())
			return result.iterator().next();
		return null;
	}

	/**
	 * Aus den vorhandenen Kursen der letzte Kurs
	 * null, wenn keine Kurse existieren 
	 */
	public GregorianCalendar getDatumLetzterKurs(Long id) {
		Aktie aktie = getAktieMitKurse(id);
		if (aktie.getKursListe() != null && aktie.getKursListe().size() > 0) {
			Kurs kurs = aktie.getKursListe().get(aktie.getKursListe().size() - 1);
			return kurs.getDatum();
		}
		return null;
	}

	/**
	 * Aus den vorhandenen Kursen der erste Kurs 
	 * null, wenn keine Kurse existieren 
	 */
	public GregorianCalendar getDatumErsterKurs(Long id) {
		// Aktie mit Kursen laden 
		Aktie aktie = getAktieMitKurse(id);
		if (aktie.getKursListe() != null && aktie.getKursListe().size() > 0) {
			Kurs kurs = aktie.getKursListe().get(0);
			return kurs.getDatum();
		}
		return null;
	}
}
