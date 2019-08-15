package com.algotrading.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.algotrading.indikator.IndikatorAlgorithmus;

@Component
public class IndikatorAlgorithmusDAO {

	@Autowired
	IndikatorAlgorithmusRepository iAR;
	
	public IndikatorAlgorithmus findByUUID ( String uuid) {
		Optional<IndikatorAlgorithmus> o = iAR.findById(uuid);
		if (o.isPresent()) return o.get();
		else return null;
	}
	
	public List<IndikatorAlgorithmus> findAll () {
		Iterable<IndikatorAlgorithmus> all = iAR.findAll();
		List<IndikatorAlgorithmus> liste = new ArrayList<IndikatorAlgorithmus>();
			for (IndikatorAlgorithmus iA : all) {
				liste.add(iA);
			}
		return liste;
	}
	
}
