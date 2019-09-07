package com.algotrading.jpa;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.algotrading.aktie.Aktie;

@Component
public class AktieDAO {

	@Autowired
	AktieRepository aR;

	public Aktie createAktie(Aktie aktie) {
		return aR.save(aktie);
	}

	public Aktie findAktie(Long id) {
		Optional<Aktie> result = aR.findById(id);
		if (result.isPresent())
			return result.get();
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
