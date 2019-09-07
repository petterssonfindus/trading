package com.algotrading.jpa;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.algotrading.aktie.Aktie;

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

	public Iterable<Aktie> findAll() {
		return aR.findAll();
	}

	public Aktie findByName(String name) {
		Iterable<Aktie> result = aR.findByName(name);
		if (result != null)
			return result.iterator().next();
		return null;
	}

}
