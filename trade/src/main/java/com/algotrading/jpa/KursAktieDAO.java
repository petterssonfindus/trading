package com.algotrading.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.algotrading.aktie.KursAktie;

@Component
public class KursAktieDAO {

	@Autowired
	private KursAktieRepository kAR;

	public KursAktie saveKursAktie(KursAktie kursAktie) {
		return kAR.save(kursAktie);
	}

}
