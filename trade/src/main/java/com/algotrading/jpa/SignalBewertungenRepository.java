package com.algotrading.jpa;

import org.springframework.data.repository.CrudRepository;

import com.algotrading.signalbewertung.SignalBewertungen;

public interface SignalBewertungenRepository extends CrudRepository<SignalBewertungen, Long> {

}
