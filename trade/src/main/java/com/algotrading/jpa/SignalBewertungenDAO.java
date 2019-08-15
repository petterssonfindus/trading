package com.algotrading.jpa;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.algotrading.signalbewertung.SignalBewertung;
import com.algotrading.signalbewertung.SignalBewertungen;

@Component
public class SignalBewertungenDAO {

	@Autowired
	SignalBewertungenRepository sBenR;

	@Autowired
	SignalBewertungDAO sBDAO;

	@PersistenceContext
	EntityManager em;

	@Transactional
	public SignalBewertungen save(SignalBewertungen signalBewertungen) {
		List<SignalBewertung> liste = signalBewertungen.getSignalBewertungen();
		// der SignalAlgorithmus muss vor dem Speichern synchronisiert werden.
		for (SignalBewertung sB : liste) {
			sB.getSignalAlgorithmus().synchronizeSAVE();
		}
		return sBenR.save(signalBewertungen);
	}

	@Transactional
	public void deleteByID(Long id) {
		sBenR.deleteById(id);
	}

	@Transactional
	public long count() {
		return sBenR.count();
	}

	@Transactional
	public SignalBewertungen find(Long id) {
		SignalBewertungen result = null;
		Optional<SignalBewertungen> optional = sBenR.findById(id);
		if (optional.isPresent()) {
			result = optional.get();
			sBDAO.loadLazyLoaded(result.getSignalBewertungen());
		}
		return result;
	}
	/*
	 * @Transactional public List<SignalBewertungen> findByAktieName(String aktie) {
	 * List<SignalBewertung> result = sBenR.findByAktieName(aktie);
	 * loadLazyLoaded(result); return result; }
	 */
}
