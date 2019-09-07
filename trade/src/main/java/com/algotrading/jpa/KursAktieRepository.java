package com.algotrading.jpa;

import org.springframework.data.repository.CrudRepository;

import com.algotrading.aktie.KursAktie;

public interface KursAktieRepository extends CrudRepository<KursAktie, Long> {

}
