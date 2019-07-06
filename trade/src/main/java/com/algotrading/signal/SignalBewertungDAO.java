package com.algotrading.signal;

import java.util.GregorianCalendar;
import java.util.List;
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
		signalBewertung.getSignalAlgorithmus().synchronizeSAVE();
		for (IndikatorAlgorithmus iA : signalBewertung.getIndikatorAlgorithmen()) {
			iA.synchronizeSAVE();
		}
		return sBR.save(signalBewertung);
	}
	
	@Transactional
	public SignalBewertung find (Long id) {
		Optional<SignalBewertung> optional = sBR.findById(id);
		SignalBewertung signalBewertung = optional.get();
		signalBewertung.getSignalAlgorithmus().synchronizeLOAD();
		if (signalBewertung != null) {
			for (IndikatorAlgorithmus iA : signalBewertung.getIndikatorAlgorithmen()) {
				iA.synchronizeLOAD();
			}
		}
		return signalBewertung;
	}
	
	@Transactional
	public List<SignalBewertung> findByAktieName (String aktie) {
		List<SignalBewertung> result = sBR.findByAktieName(aktie);
		return result;
	}
	@Transactional
	public List<SignalBewertung> findByZeitraumBeginn (GregorianCalendar beginn) {
		List<SignalBewertung> result = sBR.findByZeitraumBeginn(beginn);
		return result;
	}
	@Transactional
	public List<SignalBewertung> findByAktieNameAndTage (String aktie, int tage) {
		List<SignalBewertung> result = sBR.findByAktieNameAndTage(aktie,tage);
		return result;
	}
	@Transactional
	public List<SignalBewertung> findByAktieNameAndTageAndZeitraumBeginn (String aktie, int tage, GregorianCalendar beginn) {
		List<SignalBewertung> result = sBR.findByAktieNameAndTageAndZeitraumBeginn(aktie,tage, beginn);
		return result;
	}
	
	/**
	 * Sucht im Datenbestand eine SignalBewertung, die identisch ist zum Objekt 
	 * Incl. Signal-Parameter und Indikator-Parameter
	 * @param sB
	 * @return
	 */
	@Transactional
	public List<SignalBewertung> findEquals (SignalBewertung sB) {
		// es muss immer einen SignalAlgorithmus geben und 0 - n Indikator Algos 
		List<SignalBewertung> result = null;
		List<IndikatorAlgorithmus> indiAlgos = sB.getIndikatorAlgorithmen();
		switch (indiAlgos.size()) {
			case 0: {  // es gibt keinen IndikatorAlgorithmus
				result = sBR.findBySignalAND1Indikator(sB.getSignalAlgorithmus().getKurzname(),
						sB.getSignalAlgorithmus().getParameterList().get(0).getName(), 
						sB.getSignalAlgorithmus().getParameterList().get(0).getObject(),
						sB.getIndikatorAlgorithmen().get(0).getKurzname(), 
						sB.getIndikatorAlgorithmen().get(0).getParameterList().get(0).getName(),
						sB.getIndikatorAlgorithmen().get(0).getParameterList().get(0).getObject());
				
			}
			case 1: { // es gibt 1 Indikator Algorithmus 
				
			}
			case 2: {  // es gibt 2 Indikator Algorithmen 
				
			}
		}
		
		return result;
	}
	
	
}
