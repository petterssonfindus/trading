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
		// erst die IAs synchronisieren, damit die IDs generiert werden. 
		for (IndikatorAlgorithmus iA : signalBewertung.getIndikatorAlgorithmen()) {
			iA.synchronizeSAVE();
		}
		// die IDs der IAs werden benötigt um die Paras mit Referenzen zu speichern. 
		signalBewertung.getSignalAlgorithmus().synchronizeSAVE();
		return sBR.save(signalBewertung);
	}
	
	@Transactional
	public SignalBewertung find (Long id) {
		SignalBewertung result = null; 
		Optional<SignalBewertung> optional = sBR.findById(id);
		if (optional.isPresent()) {
			result = optional.get();
		}
		return result;
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
		if (sB == null) return null;
		// es muss immer einen SignalAlgorithmus geben und 0 - n Indikator Algos 
		List<SignalBewertung> result = null;
		List<IndikatorAlgorithmus> indiAlgos = sB.getIndikatorAlgorithmen();
		switch (indiAlgos.size()) {
			case 0: {  // es gibt keinen IndikatorAlgorithmus
				result = sBR.findBySignal(sB.getSignalAlgorithmus().getKurzname(),
						sB.getSignalAlgorithmus().getParameterList().get(0).getName(), 
						sB.getSignalAlgorithmus().getParameterList().get(0).getObject());
				break;
			}
			case 1: { // es gibt 1 Indikator Algorithmus 
				result = sBR.findBySignalAND1Indikator(sB.getSignalAlgorithmus().getKurzname(),
						sB.getSignalAlgorithmus().getParameterList().get(0).getName(), 
						sB.getSignalAlgorithmus().getParameterList().get(0).getObject(),
						sB.getIndikatorAlgorithmen().get(0).getKurzname(), 
						sB.getIndikatorAlgorithmen().get(0).getParameterList().get(0).getName(),
						sB.getIndikatorAlgorithmen().get(0).getParameterList().get(0).getObject());
				break;
			}
			case 2: {  // es gibt 2 Indikator Algorithmen 
				result = sBR.findBySignalAND2Indikator(sB.getSignalAlgorithmus().getKurzname(),
						sB.getSignalAlgorithmus().getParameterList().get(0).getName(), 
						sB.getSignalAlgorithmus().getParameterList().get(0).getObject(),
						sB.getIndikatorAlgorithmen().get(0).getKurzname(), 
						sB.getIndikatorAlgorithmen().get(0).getParameterList().get(0).getName(),
						sB.getIndikatorAlgorithmen().get(0).getParameterList().get(0).getObject(),
						sB.getIndikatorAlgorithmen().get(1).getKurzname(), 
						sB.getIndikatorAlgorithmen().get(1).getParameterList().get(0).getName(),
						sB.getIndikatorAlgorithmen().get(1).getParameterList().get(0).getObject());
				break;
				
			}
		}
		
		return result;
	}
	
	
}
