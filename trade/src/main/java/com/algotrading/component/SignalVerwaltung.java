package com.algotrading.component;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algotrading.aktie.Aktie;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.jpa.IndikatorAlgorithmusDAO;
import com.algotrading.jpa.SignalBewertungDAO;
import com.algotrading.jpa.SignalBewertungenDAO;
import com.algotrading.signal.SignalAlgorithmus;
import com.algotrading.signalbewertung.SignalBewertung;
import com.algotrading.signalbewertung.SignalBewertungen;
import com.algotrading.uimodel.UIFileText;
import com.algotrading.util.RestApplicationException;
import com.algotrading.util.Zeitraum;

@Service
public class SignalVerwaltung {
	@Transient
	private static final Logger log = LogManager.getLogger(SignalVerwaltung.class);

	@Autowired
	private SignalBewertungDAO signalBewertungDAO;

	@Autowired
	private SignalBewertungenDAO signalBewertungenDAO;

	@Autowired
	private IndikatorAlgorithmusDAO iADAO;

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
		signalBewertungenDAO.save(sBs);
		return sBs;
	}

	@Transactional
	public void deleteSignalBewertungenAll() {
		List<SignalBewertung> liste = findAllSignalBewertung();
		for (SignalBewertung sB : liste) {
			deleteSignalBewertungen(sB.getId());
		}
	}

	@Transactional
	public void deleteSignalBewertungen(Long id) {
		signalBewertungenDAO.deleteByID(id);
	}

	public Zeitraum getZeitraumSignale(SignalAlgorithmus sA) {
		return sA.getZeitraumSignale();
	}

	@Transactional
	public SignalBewertungen getSignalBewertungen(Long id) {
		return signalBewertungenDAO.find(id);
	}

	public void rechneSignalPerformance(Aktie aktie, List<Integer> tage) {
		aktie.rechneSignalPerformance(tage);
	}

	/**
	 * Führt signalbewertung durch für Liste Zeiträume und Liste Tage
	 */
	public List<SignalBewertung> bewerteSignalListe(Aktie aktie, List<Zeitraum> zeiten, List<Integer> tage) {
		List<SignalBewertung> bewertungen = new ArrayList<>();
		// die Liste der Tage-Auswertungen wird an der Aktie vermerkt
		// Dadurch kann die csv-Tabelle die Spalten korrekt schreiben 
		aktie.setPerformanceTage(tage);
		// für jede Tage-Betrachtung
		for (Integer tag : tage) {
			bewertungen.addAll(bewerteSignale(aktie, zeiten, tag));
		}
		return bewertungen;
	}

	/**
	 * Führt die SignalBewertung für eine Liste von Zeiträumen durch 
	 * Reihenfolge und Dauer spielen keine Rolle
	 */
	public List<SignalBewertung> bewerteSignale(Aktie aktie, List<Zeitraum> zeiten, int tage) {
		List<SignalBewertung> bewertungen = new ArrayList<>();
		for (Zeitraum zeitraum : zeiten) {
			// für alle SignalAlgorithmen, die an der Aktie hängen
			for (SignalAlgorithmus sA : aktie.getSignalAlgorithmen()) {
				// eine neue Bewertung erstellen und berechnen 
				bewertungen.add(sA.createBewertung().bewerteSignale(zeitraum, tage));
			}

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
		} else {
			throw new RestApplicationException("SignalBewertung nicht gefunden ID: " + id, HttpStatus.NOT_FOUND);
		}

		return result;
	}

	@Transactional
	public List<SignalBewertung> findAllSignalBewertung() {
		return signalBewertungDAO.findAll();
	}

	@Transactional
	public Iterable<SignalBewertungen> findAllSignalBewertungen() {
		return signalBewertungenDAO.findAll();
	}

	public UIFileText writeFileSignalBewertungen(long id) {
		SignalBewertungen sB = getSignalBewertungen(id);
		return sB.writeFileSignalBewertungen(id);
	}

	public List<String> writeStringSignalBewertungen(long id) {
		SignalBewertungen sB = getSignalBewertungen(id);
		return sB.writeStringSignalBewertungen(id);
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
		return signalBewertungenDAO.count();
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
