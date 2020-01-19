package com.algotrading.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.algotrading.aktie.Kurs;
import com.algotrading.jpa.KursDAO;

@Component
public class KursVerwaltung {

	@Autowired
	KursDAO kursDAO;

	public Kurs findKurs(Long id) {
		return kursDAO.findByID(id);
	}

	public Kurs save(Kurs kurs) {
		return kursDAO.save(kurs);
	}

}
