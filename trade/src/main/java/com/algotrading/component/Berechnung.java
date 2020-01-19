package com.algotrading.component;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.aktie.Aktie;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.signal.SignalAlgorithmus;
import com.algotrading.signalbewertung.SignalBewertung;
import com.algotrading.signalbewertung.SignalBewertungen;
import com.algotrading.uimodel.UIAlgorithmus;
import com.algotrading.uimodel.UICreateSignalBewertung;
import com.algotrading.uimodel.UIIndikatorAlgorithmus;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Zeitraum;

@Service
public class Berechnung {

	@Autowired
	AktieVerwaltung aV;

	@Autowired
	SignalVerwaltung sV;

	public SignalBewertungen rechneSignale(UICreateSignalBewertung input) {
		System.out.println("UICreateSignalBewertung: " + input.toString());
		// zuerst die Aktie holen 
		Aktie aktie = aV.getAktieMitKurse(Long.parseLong(input.getAktieId()));
		// iteriert über alle Algorithmen 
		for (UIAlgorithmus uiAlgo : input.getAlgorithmen()) {
			// in jedem Algorithmus gibt es genau einen SignalAlgorithmus
			SignalAlgorithmus sA = SignalAlgorithmus.getObject(uiAlgo.getSignalAlgorithmusName());
			sA.setParameterString(uiAlgo.getSignalParameter());
			aktie.addSignalAlgorithmus(sA);
			System.out.println("BerechnungSAParameter: " + sA.toString());
			// in jedem Algorithmus gibt es n-IndikatorAlgorithmen
			for (UIIndikatorAlgorithmus uiIndiAlgo : uiAlgo.getIndikatorAlgorithmen()) {
				// den IndikatorAlgorithmus instantiieren 
				IndikatorAlgorithmus iA = IndikatorAlgorithmus.getObject(uiIndiAlgo.getIndikatorName());
				// dem IA die Parameter hinzufügen
				if (iA != null && sA != null) {
					sA.addIndikatorAlgorithmus(iA);
					iA.setParameterString(uiIndiAlgo.getIndikatorParameter());
					System.out.println("BerechnungIAParameter: " + iA.toString());
					aktie.addIndikatorAlgorithmus(iA);
				}
			}
		}
		aktie.rechneIndikatoren();
		aktie.rechneSignale();

		List<Zeitraum> listeZeitraum = DateUtil.getJahresZeitraeumeTurnus(aktie.getZeitraumKurse(), input.getBewertungTurnus());
		// Zeitspannen zur Signalbewertung bestimmen 
		List<Integer> tage = DateUtil.getBewertungTage(input.getBewertungTage());

		sV.rechneSignalPerformance(aktie, tage);
		// Bewertungen durchführen 
		List<SignalBewertung> bewertungen = sV.bewerteSignalListe(aktie, listeZeitraum, tage);
		System.out.println("SignalBewertungen berechnet: " + bewertungen.size());

		// SignalBewertungen zusammen fassen und speichern und ausgeben 
		SignalBewertungen sBs = new SignalBewertungen();
		sBs.addSignalBewertungen(bewertungen);

		// alle Kurse mit indikator und Signalen als csv speichern
		//		aV.writeFileKursIndikatorSignal(aktie);
		sV.saveSignalBewertungen(sBs);
		System.out.println("SignalBewertungen geschrieben: " + sBs.getId());
		// Signal-Ergebnisse als file ausgeben 
		//		File file = sV.writeFileSignalBewertungen(sBs.getId());
		// System.out.println("File signalbewertungen Pfad: " + file.getAbsolutePath());
		return sBs;
	}

}
