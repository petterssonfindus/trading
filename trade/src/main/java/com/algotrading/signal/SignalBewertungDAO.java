package com.algotrading.signal;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

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

	@PersistenceContext
	EntityManager em;

	@Transactional
	public SignalBewertung save(SignalBewertung signalBewertung) {
		signalBewertung.getSignalAlgorithmus().synchronizeSAVE();
		return sBR.save(signalBewertung);
	}

	@Transactional
	public SignalBewertung find(Long id) {
		SignalBewertung result = null;
		Optional<SignalBewertung> optional = sBR.findById(id);
		if (optional.isPresent()) {
			result = optional.get();
			loadLazyLoaded(result);
		}
		return result;
	}

	@Transactional
	public List<SignalBewertung> findByAktieName(String aktie) {
		List<SignalBewertung> result = sBR.findByAktieName(aktie);
		loadLazyLoaded(result);
		return result;
	}

	@Transactional
	public List<SignalBewertung> findByZeitraumBeginn(GregorianCalendar beginn) {
		List<SignalBewertung> result = sBR.findByZeitraumBeginn(beginn);
		loadLazyLoaded(result);
		return result;
	}

	@Transactional
	public List<SignalBewertung> findByAktieNameAndTage(String aktie, int tage) {
		List<SignalBewertung> result = sBR.findByAktieNameAndTage(aktie, tage);
		loadLazyLoaded(result);
		return result;
	}

	@Transactional
	private boolean loadLazyLoaded(SignalBewertung sB) {
		sB.getSignalAlgorithmus().getIndikatorAlgorithmen().size();
		return true;
	}

	/**
	 * Eine Liste geladener SignalBewertungen wird Nach-Geladen
	 */
	@Transactional
	private boolean loadLazyLoaded(List<SignalBewertung> sBs) {
		if (sBs == null
				|| sBs.size() == 0)
			return false;
		for (SignalBewertung sB : sBs) {
			sB.getSignalAlgorithmus().getIndikatorAlgorithmen().size();
		}
		return true;
	}

	@Transactional
	public List<SignalBewertung> findByAktieNameAndTageAndZeitraumBeginn(String aktie, int tage,
			GregorianCalendar beginn) {
		List<SignalBewertung> result = sBR.findByAktieNameAndTageAndZeitraumBeginn(aktie, tage, beginn);
		result.size();
		return result;
	}

	/**
	 * Durchsucht die Datenbank, ob die identische SignalBewertung existiert
	 * Berücksichtigt SignalBewertung, SignalAlgo und 1 IndikatorAlgo
	 */
	@Transactional
	public List<SignalBewertung> findByTypedQuery(SignalBewertung sB) {
		List<SignalBewertung> result = null;

		String queryStr = "SELECT DISTINCT sb FROM SignalBewertung sb, SignalAlgorithmus sa JOIN sa.indikatorAlgorithmen ia "
				+ "WHERE sb.signalAlgorithmus = sa "
				+ makeQueryStringForSB(sB)
				+ makeQueryStringForSA(sB.getSignalAlgorithmus())
				+ makeQueryStringForIA("", sB.getSignalAlgorithmus().getIndikatorAlgorithmen().get(0));

		TypedQuery<SignalBewertung> query = em.createQuery(queryStr, SignalBewertung.class)
				.setParameter("beginn", new Date(), TemporalType.DATE);
		result = query.getResultList();
		if (result == null
				|| result.size() == 0)
			return result;
		// Nach-Laden mit ID
		loadLazyLoaded(result);
		return result;
	}

	private static String makeQueryStringForSB(SignalBewertung sB) {
		String result = "AND sb.aktieName=".concat(surroundHochkomma(sB.getAktieName()))
				+ "AND sb.tage = 10 "
				+ "AND sb.zeitraumBeginn <= :beginn ";
		return result;
	}

	private static String makeQueryStringForSA(SignalAlgorithmus sA) {
		String result = "AND sa.name = ".concat(surroundHochkomma(sA.getKurzname()))
				+ " AND sa.aktieName=".concat(surroundHochkomma(sA.getAktieName()));
		if (sA.getP1name() != null)
			result = result.concat(
					" AND sa.p1name=".concat(surroundHochkomma(sA.getP1name()))
							+ " AND sa.p1wert=".concat(surroundHochkomma(sA.getP1wert())));
		if (sA.getP2name() != null)
			result = result.concat(
					" AND sa.p2name=".concat(surroundHochkomma(sA.getP2name()))
							+ " AND sa.p2wert=".concat(surroundHochkomma(sA.getP2wert())));
		if (sA.getP3name() != null)
			result = result.concat(
					" AND sa.p3name=".concat(surroundHochkomma(sA.getP3name()))
							+ " AND sa.p3wert=".concat(surroundHochkomma(sA.getP3wert())));
		if (sA.getP4name() != null)
			result = result.concat(
					" AND sa.p4name=".concat(surroundHochkomma(sA.getP4name()))
							+ " AND sa.p4wert=".concat(surroundHochkomma(sA.getP4wert())));
		if (sA.getP5name() != null)
			result = result.concat(
					" AND sa.p5name=".concat(surroundHochkomma(sA.getP5name()))
							+ " AND sa.p5wert=".concat(surroundHochkomma(sA.getP5wert())));

		return result;
	}

	private static String makeQueryStringForIA(String stelle, IndikatorAlgorithmus iA) {
		String result = "AND ia.name = ".concat(surroundHochkomma(iA.getName()));
		if (iA.getP1name() != null)
			result = result.concat(
					" AND ia.p1name=".concat(surroundHochkomma(iA.getP1name()))
							+ " AND ia.p1wert=".concat(surroundHochkomma(iA.getP1wert())));
		if (iA.getP2name() != null)
			result = result.concat(
					" AND ia.p2name=".concat(surroundHochkomma(iA.getP2name()))
							+ " AND ia.p2wert=".concat(surroundHochkomma(iA.getP2wert())));
		if (iA.getP3name() != null)
			result = result.concat(
					" AND ia.p3name=".concat(surroundHochkomma(iA.getP3name()))
							+ " AND ia.p3wert=".concat(surroundHochkomma(iA.getP3wert())));
		if (iA.getP4name() != null)
			result = result.concat(
					" AND ia.p4name=".concat(surroundHochkomma(iA.getP4name()))
							+ " AND ia.p4wert=".concat(surroundHochkomma(iA.getP4wert())));
		if (iA.getP5name() != null)
			result = result.concat(
					" AND ia.p5name=".concat(surroundHochkomma(iA.getP5name()))
							+ " AND ia.p5wert=".concat(surroundHochkomma(iA.getP5wert())));

		return result;
	}

	private static String surroundHochkomma(String input) {
		return "'".concat(input).concat("'");
	}

	/**
	 * Sucht im Datenbestand eine SignalBewertung, die identisch ist zum Objekt
	 * Incl. Signal-Parameter und Indikator-Parameter
	 * 
	 * @param sB
	 * @return
	 */

	/*
	 * @Transactional public List<SignalBewertung> findEquals(SignalBewertung sB) {
	 * if (sB == null) return null; // es muss immer einen SignalAlgorithmus geben
	 * List<SignalBewertung> result = null; List<IndikatorAlgorithmus> indiAlgos =
	 * sB.getSignalAlgorithmus().getIndikatorAlgorithmen(); switch
	 * (indiAlgos.size()) { case 0: { // es gibt keinen IndikatorAlgorithmus
	 * System.out.println( "SB-Suchparameter:" + sB.getAktieName() +
	 * sB.getSignalAlgorithmus().getKurzname() + sB.getTage() +
	 * sB.getSignalAlgorithmus().getParameterList().get(0).getName() +
	 * sB.getSignalAlgorithmus().getParameterList().get(0).getObject().toString());
	 * result = sBR.findBySignal( sB.getAktieName(),
	 * sB.getSignalAlgorithmus().getKurzname(), sB.getTage(),
	 * sB.getSignalAlgorithmus().getParameterList().get(0).getName(),
	 * sB.getSignalAlgorithmus().getParameterList().get(0).getObject().toString());
	 * System.out.println("Ergebnis: " + result.size()); break; } case 1: { // es
	 * gibt 1 Indikator Algorithmus result = sBR.findBySignalAND1Indikator(
	 * sB.getAktieName(), sB.getSignalAlgorithmus().getKurzname(), sB.getTage(),
	 * sB.getSignalAlgorithmus().getParameterList().get(0).getName(),
	 * sB.getSignalAlgorithmus().getParameterList().get(0).getObject().toString(),
	 * sB.getSignalAlgorithmus().getIndikatorAlgorithmen().get(0).getKurzname(),
	 * sB.getSignalAlgorithmus().getIndikatorAlgorithmen().get(0).getParameterList()
	 * .get(0).getName(),
	 * sB.getSignalAlgorithmus().getIndikatorAlgorithmen().get(0).getParameterList()
	 * .get(0).getObject() .toString()); break; } case 2: { // es gibt 2 Indikator
	 * Algorithmen result = sBR.findBySignalAND2Indikator(
	 * sB.getSignalAlgorithmus().getKurzname(),
	 * sB.getSignalAlgorithmus().getParameterList().get(0).getName(),
	 * sB.getSignalAlgorithmus().getParameterList().get(0).getObject(),
	 * sB.getSignalAlgorithmus().getIndikatorAlgorithmen().get(0).getKurzname(),
	 * sB.getSignalAlgorithmus().getIndikatorAlgorithmen().get(0).getParameterList()
	 * .get(0).getName(),
	 * sB.getSignalAlgorithmus().getIndikatorAlgorithmen().get(0).getParameterList()
	 * .get(0).getObject(),
	 * sB.getSignalAlgorithmus().getIndikatorAlgorithmen().get(1).getKurzname(),
	 * sB.getSignalAlgorithmus().getIndikatorAlgorithmen().get(1).getParameterList()
	 * .get(0).getName(),
	 * sB.getSignalAlgorithmus().getIndikatorAlgorithmen().get(1).getParameterList()
	 * .get(0).getObject()); break;
	 * 
	 * } } return result; }
	 */
}
