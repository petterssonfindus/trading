package com.algotrading.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.repository.CrudRepository;

import com.algotrading.signalbewertung.SignalBewertungen;

public interface SignalBewertungenRepository extends CrudRepository<SignalBewertungen, Long> {

	@EntityGraph(value = "SignalBewertungen.signalBewertung", type = EntityGraphType.LOAD)
	public Optional<SignalBewertungen> findById(Long id);

}
