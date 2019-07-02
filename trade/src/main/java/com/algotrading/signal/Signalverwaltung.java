package com.algotrading.signal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class Signalverwaltung {

	@Autowired SignalBewertungDAO signalBewertungDAO; 
	
	@Transactional
	public SignalBewertung save (SignalBewertung sB) {
		return signalBewertungDAO.save(sB);
	}
	
	
	@Transactional
	public SignalBewertung find (Long id) {
		SignalBewertung result = null;
		result = signalBewertungDAO.find(id);
		// Nach-Laden der Lazy-Load Liste innerhalb einer Transaction
		result.getIndikatorAlgorithmen().size();
		return result; 
	}
	
}
