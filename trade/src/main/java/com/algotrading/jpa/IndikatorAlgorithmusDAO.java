package com.algotrading.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.algotrading.indikator.IndikatorAlgorithmus;

@Component
public class IndikatorAlgorithmusDAO {

	@Autowired
	IndikatorAlgorithmusRepository iAR;

	@Transactional
	public IndikatorAlgorithmus findByUUID(String uuid) {
		Optional<IndikatorAlgorithmus> o = iAR.findById(uuid);
		if (o.isPresent())
			return o.get();
		else
			return null;
	}

	@Transactional
	public List<IndikatorAlgorithmus> findAll() {
		Iterable<IndikatorAlgorithmus> all = iAR.findAll();
		List<IndikatorAlgorithmus> liste = new ArrayList<IndikatorAlgorithmus>();
		for (IndikatorAlgorithmus iA : all) {
			liste.add(iA);
		}
		return liste;
	}

	@Transactional
	public long count() {
		return iAR.count();
	}

}
