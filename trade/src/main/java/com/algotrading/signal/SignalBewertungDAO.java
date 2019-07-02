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
}
