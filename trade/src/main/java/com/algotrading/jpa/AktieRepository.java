package com.algotrading.jpa;

import org.springframework.data.repository.CrudRepository;

import com.algotrading.aktie.Aktie;

public interface AktieRepository extends CrudRepository<Aktie, Long> {

	public Iterable<Aktie> findByName(String name);

}
