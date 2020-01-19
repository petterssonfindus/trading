package com.algotrading.jpa;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

public interface KursRepository extends CrudRepository<Kurs, Long> {

	long countByAktie(Aktie aktie);

	List<Kurs> findByAktie(Aktie aktie, Sort sort);

}
