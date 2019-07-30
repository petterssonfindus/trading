package com.algotrading.signal;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorAlgorithmusDAO;
import com.algotrading.util.Parameter.Para;

@Service
public class Signalverwaltung {

	@Autowired
	SignalBewertungDAO signalBewertungDAO;

	@Autowired
	IndikatorAlgorithmusDAO iADAO;

	@Transactional
	public SignalBewertung save(SignalBewertung sB) {
		return signalBewertungDAO.save(sB);
	}

	@Transactional
	public SignalBewertung find(Long id) {
		SignalBewertung result = null;
		result = signalBewertungDAO.find(id);
		// Nach-Laden der Lazy-Load Liste innerhalb einer Transaction
		if (result != null)
			result.getIndikatorAlgorithmen().size();
		return result;
	}

	@Transactional
	public SignalBewertung findWithIA(Long id) {
		// zuerst die SignalBewertung aus der DB lesen ohne IAs
		SignalBewertung sB = find(id);
		// dann die UUID der IndikatorAlgos lesen
		return instanciateIA(sB);
	}

	/**
	 * Durchsucht alle Parameter der SignalBewertung Und Lädt (instanziiert) die
	 * anhängenden IndikatorAlgorithmen
	 */
	public SignalBewertung instanciateIA(SignalBewertung sB) {
		// geht durch alle Parameter des SignalAlgorithmus
		List<Para> paraList = sB.getSignalAlgorithmus().findIAParameter();
		for (Para para : paraList) {
			// aus der UUID einen IndikatorAlgorithmus machen.
			IndikatorAlgorithmus iA = iADAO.findByUUID((String) para.getObject());
			// den iA als Parameter-Objekt speichern
			sB.getSignalAlgorithmus().replace(para.getName(), iA);
		}
		return sB;
	}

	@Transactional
	public boolean equalsSignalBewertung(SignalBewertung sB1, SignalBewertung sB2) {
		if (sB1.equals(sB2))
			return true;
		else
			return false;
	}

}
