package com.algotrading.signal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
/**
 * Zugriff auf Repository 
 *
 */
@Component
public class SignalBewertungDAO {
	
	@Autowired
	SignalBewertungRepository sBR; 
	
	@Transactional
	public SignalBewertung save(SignalBewertung signalBewertung) {
		return sBR.save(signalBewertung);
	}
	
	
}
