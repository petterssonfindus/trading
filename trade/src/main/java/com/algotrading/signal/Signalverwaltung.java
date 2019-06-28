package com.algotrading.signal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Signalverwaltung {

	@Autowired SignalBewertungDAO signalBewertungDAO; 
	
	public SignalBewertung signalBewertungSave (SignalBewertung sB) {
		return signalBewertungDAO.save(sB);
	}
	
}
