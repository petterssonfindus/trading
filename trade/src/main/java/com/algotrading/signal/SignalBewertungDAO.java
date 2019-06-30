package com.algotrading.signal;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.algotrading.indikator.IndikatorAlgorithmus;
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
		signalBewertung.setIndikatorAlgorithmen();
		for (IndikatorAlgorithmus iA : signalBewertung.getIndikatorAlgorithmen()) {
			iA.synchronizeSAVE();
		}
		return sBR.save(signalBewertung);
	}
	
	@Transactional
	public SignalBewertung find (Long id) {
		Optional<SignalBewertung> optional = sBR.findById(id);
		SignalBewertung signalBewertung = optional.get();
		
		if (signalBewertung != null) {
			for (IndikatorAlgorithmus iA : signalBewertung.getIndikatorAlgorithmen()) {
				iA.synchronizeLOAD();
			}
		}
		return signalBewertung;
	}
	
	
}
