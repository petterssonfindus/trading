package com.algotrading.jpa;

import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.util.RestApplicationException;

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
	public Aktie getAktieFromDB(Long id) {
		Optional<Aktie> optional = aR.findById(id);
		if (optional.isPresent())
			return optional.get();
		return null;
	}

	@Transactional
	@EntityGraph(value = "aktie.kurs")
	public Aktie getAktieMitKurse(Long id) {
		Optional<Aktie> result = aR.findById(id);
		if (result.isPresent()) {
			return result.get();
		}
		return null;
	}

	@Transactional
	public void deleteAktie(Aktie aktie) {
		aR.delete(aktie);
	}

	@Transactional
	public void deleteAktieByID(Long id) {
		try {
			aR.deleteById(id);
		} catch (Exception e) {
			throw new RestApplicationException("Delete: Aktie existiert nicht: " + id, HttpStatus.NOT_FOUND);
		}
	}

	@Transactional
	public Iterable<Aktie> findAll() {
		return aR.findAll();
	}

	@Transactional
	public List<Aktie> findByName(String name) {
		return aR.findByNameLikeIgnoreCase("%" + name + "%");
	}

	@Transactional
	public List<Aktie> findByQuelle(int quelle) {
		return aR.findByQuelle(quelle);
	}

	/**
	 * Aus den vorhandenen Kursen der letzte Kurs
	 * null, wenn keine Kurse existieren 
	 */
	@Transactional
	public GregorianCalendar getDatumLetzterKurs(Long id) {
		Aktie aktie = getAktieMitKurse(id);
		if (aktie.getKursListe() != null && !aktie.getKursListe().isEmpty()) {
			Kurs kurs = aktie.getKursListe().get(aktie.getKursListe().size() - 1);
			return kurs.getDatum();
		}
		return null;
	}

	/**
	 * Aus den vorhandenen Kursen der erste Kurs 
	 * null, wenn keine Kurse existieren 
	 */
	@Transactional
	public GregorianCalendar getDatumErsterKurs(Long id) {
		// Aktie mit Kursen laden 
		Aktie aktie = getAktieMitKurse(id);
		if (aktie.getKursListe() != null && aktie.getKursListe().size() > 0) {
			Kurs kurs = aktie.getKursListe().get(0);
			return kurs.getDatum();
		}
		return null;
	}

	@Transactional
	public Kurs getErsterKurs(Long id) {
		// Aktie mit Kursen laden 
		Aktie aktie = getAktieMitKurse(id);
		if (aktie == null) {
			throw new RestApplicationException("Aktie nicht vorhanden", HttpStatus.NOT_FOUND);
		}
		if (aktie.getKursListe() == null || aktie.getKursListe().isEmpty()) {
			throw new RestApplicationException("Aktie hat keine Kurse", HttpStatus.NOT_FOUND);
		}
		return aktie.getKursListe().get(0);
	}

	@Transactional
	public Kurs getLetzterKurs(Long id) {
		// Aktie mit Kursen laden 
		Aktie aktie = getAktieMitKurse(id);
		if (aktie == null) {
			throw new RestApplicationException("Aktie nicht vorhanden", HttpStatus.NOT_FOUND);
		}
		if (aktie.getKursListe() == null || aktie.getKursListe().isEmpty()) {
			throw new RestApplicationException("Aktie hat keine Kurse", HttpStatus.NOT_FOUND);
		}
		return aktie.getKursListe().get(aktie.getKursListe().size() - 1);
	}
}
