package com.algotrading.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algotrading.aktie.Aktie;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.jpa.IndikatorAlgorithmusDAO;
import com.algotrading.jpa.SignalBewertungDAO;
import com.algotrading.jpa.SignalBewertungenDAO;
import com.algotrading.signalbewertung.SignalBewertung;
import com.algotrading.signalbewertung.SignalBewertungen;
import com.algotrading.util.Zeitraum;

@Service
public class Signalverwaltung {

	@Autowired
	private SignalBewertungDAO signalBewertungDAO;

	@Autowired
	private SignalBewertungenDAO sBsDAO;

	@Autowired
	private IndikatorAlgorithmusDAO iADAO;

	/**
	 * Bewertet alle Signale, die an der Aktie hängen
	 * 
	 * @param zeitraum   der Zeitraum in dem die signale auftreten Wenn null, dann
	 *                   maximaler Zeitraum, für den Signale vorliegen.
	 * @param tageVoraus für die Erfolgsmessung in die Zukunft
	 */
	public void bewerteSignale(Aktie aktie, Zeitraum zeitraum, int tage) {
		aktie.bewerteSignale(zeitraum, tage);

	}

	@Transactional
	public SignalBewertung saveSignalBewertung(SignalBewertung sB) {
		return signalBewertungDAO.save(sB);
	}

	@Transactional
	public List<SignalBewertung> saveSignalBewertungListe(List<SignalBewertung> liste) {
		for (SignalBewertung sB : liste) {
			saveSignalBewertung(sB);
		}
		return liste;
	}

	/**
	 * Speichert die ganze Hierarchie: SignalBewertungen - SignalBewertung -
	 * SignalAlgorithmus - IndikatorAlgorithmus
	 */
	@Transactional
	public SignalBewertungen saveSignalBewertungen(SignalBewertungen sBs) {
		sBsDAO.save(sBs);
		return sBs;
	}

	@Transactional
	public void deleteSignalBewertungen(Long id) {
		sBsDAO.deleteByID(id);
	}

	@Transactional
	public SignalBewertungen getSignalBewertungen(Long id) {
		return sBsDAO.find(id);
	}

	/**
	 * Führt signalbewertung durch für Liste Zeiträume und Liste Tage
	 */
	public List<SignalBewertung> bewerteSignalListe(Aktie aktie, List<Zeitraum> zeiten, List<Integer> tage) {
		List<SignalBewertung> bewertungen = new ArrayList<SignalBewertung>();
		// für jede Tage-Betrachtung
		for (Integer tag : tage) {
			bewertungen.addAll(bewerteSignale(aktie, zeiten, tag));
		}
		return bewertungen;
	}

	/**
	 * Führt die SignalBewertung für eine Liste von Zeiträumen durch Reihenfolge und
	 * Dauer spielen keine Rolle
	 */
	public List<SignalBewertung> bewerteSignale(Aktie aktie, List<Zeitraum> zeiten, int tage) {
		List<SignalBewertung> bewertungen = new ArrayList<SignalBewertung>();
		for (Zeitraum zeitraum : zeiten) {
			bewertungen.addAll(aktie.bewerteSignale(zeitraum, tage));
		}
		return bewertungen;
	}

	@Transactional
	public SignalBewertung find(Long id) {
		SignalBewertung result = null;
		result = signalBewertungDAO.find(id);
		// Nach-Laden der Indikator-Algo-Parameter
		if (result != null) {
			List<IndikatorAlgorithmus> indiAlgos = result.getSignalAlgorithmus().getIndikatorAlgorithmen();
			for (IndikatorAlgorithmus iA : indiAlgos) {
				indiAlgos.size();
			}
		} else
			return null;
		return result;
	}

	@Transactional
	public boolean equalsSignalBewertung(SignalBewertung sB1, SignalBewertung sB2) {
		if (sB1.equals(sB2))
			return true;
		else
			return false;
	}

	/**
	 * Prüft, ob es die identische SignalBewertung in der Datenbank gibt Alle
	 * Gefundenen stimmen exakte mit der gesuchten überein.
	 */
	public List<SignalBewertung> existsExact(SignalBewertung sB) {
		// eine Liste aller ähnlichen SB suchen
		List<SignalBewertung> liste = existsInDB(sB);
		// alle Treffer exakt überprüfen
		// wenn nicht identisch, dann aus Liste entfernen
		for (SignalBewertung sB1 : liste) {
			if (!sB.equals(sB1))
				liste.remove(sB1);
		}
		return liste;
	}

	/**
	 * Prüft, ob es zu einer SignalBewertung eine inhaltlich identische in der
	 * Datenbank gibt. Wenn ja, dann wird diese instantiiert und zurück gegeben.
	 */
	public List<SignalBewertung> existsInDB(SignalBewertung sB) {
		// holt sich aus der DB alles was ähnlich aussieht.
		return signalBewertungDAO.findByTypedQuery(sB);
	}

	public long countSignalBewertungen() {
		return sBsDAO.count();
	}

	public long countSignalBewertung() {
		return signalBewertungDAO.count();
	}

	public long countIndikatorAlgorithmus() {
		return iADAO.count();
	}

	public void printCount() {
		System.out.println("SignalBewertungen: " + countSignalBewertungen());
		System.out.println("SignalBewertung: " + countSignalBewertung());
		System.out.println("IndikatorAlgorithmus: " + countIndikatorAlgorithmus());
	}

}
