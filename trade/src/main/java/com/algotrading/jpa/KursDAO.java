package com.algotrading.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

/**
 * Für direkten Zugriff auf Kurse. 
 */
@Component
public class KursDAO {

	@Autowired
	KursRepository kursRepository;

	public Kurs findByID(Long id) {
		Optional<Kurs> kurs = kursRepository.findById(id);
		if (kurs.isPresent())
			return kurs.get();
		return null;
	}

	public Kurs save(Kurs kurs) {
		return kursRepository.save(kurs);
	}

	public long countByAktie(Aktie aktie) {
		return kursRepository.countByAktie(aktie);
	}

	public List<Kurs> findByAktie(Aktie aktie) {
		// Sortierung festlegen 
		Sort sort = Sort.by("datum").ascending();
		return kursRepository.findByAktie(aktie, sort);
	}

}
