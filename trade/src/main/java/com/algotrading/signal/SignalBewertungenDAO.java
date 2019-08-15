package com.algotrading.signal;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SignalBewertungenDAO {

	@Autowired
	SignalBewertungenRepository sBenR;

	@Autowired
	SignalBewertungDAO sBDAO;

	@PersistenceContext
	EntityManager em;

// 	@Transactional
	public SignalBewertungen save(SignalBewertungen signalBewertungen) {
		List<SignalBewertung> liste = signalBewertungen.getSignalBewertungen();
		// der SignalAlgorithmus muss vor dem Speichern synchronisiert werden.
		for (SignalBewertung sB : liste) {
			sB.getSignalAlgorithmus().synchronizeSAVE();
		}
		return sBenR.save(signalBewertungen);
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
