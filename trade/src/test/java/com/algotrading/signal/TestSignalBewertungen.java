package com.algotrading.signal;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.algotrading.aktie.Aktie;
import com.algotrading.component.Signalverwaltung;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGD;
import com.algotrading.signalbewertung.SignalBewertung;
import com.algotrading.signalbewertung.SignalBewertungen;
import com.algotrading.util.AbstractTest;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Zeitraum;

public class TestSignalBewertungen extends AbstractTest {

	@Autowired
	Signalverwaltung sV;

	@Test
	public void testDeletesignalBewertungen() {
		sV.printCount();
		sV.deleteSignalBewertungen(new Long(2537));
		sV.printCount();
	}

	@Test
	public void testGetSignalBewertungen() {
		SignalBewertungen sB = sV.getSignalBewertungen(2537l);
		System.out.println("Signalbewertung: " + sB.getId() + " Anzahl: " + sB.getSignalBewertungen().size());
		for (SignalBewertung sBew : sB.getSignalBewertungen()) {
			System.out.println(sBew.toCSVString());
		}
	}

	@Test
	public void testCreateSignalBewertungen() {
		Aktie aktie = aV.getAktieLazy(48218L);
		//		List<Aktie> aktien = aV.getVerzeichnis().getAktien(DateUtil.createGregorianCalendar(1, 1, 2000));
		IndikatorAlgorithmus iA = new IndikatorGD();
		iA.addParameter("dauer", 10);
		// AktieVerzeichnis.addIndikatorAlgorithmus(aktien, iA);
		aktie.addIndikatorAlgorithmus(iA);
		/*
				IndikatorAlgorithmus iA2 = aktie.addIndikatorAlgorithmus(new IndikatorOBV());
				iA2.addParameter("dauer", 10);
				iA2.addParameter("stabw", 10);
				iA2.addParameter("faktor", 1);
		*/
		aktie.rechneIndikatoren();

		//		SignalBeschreibung sB = aktie.createSignalBeschreibung(Signal.MinMax);
		//		sB.addParameter("dauer", 30);
		//		sB.addParameter("indikator" , iA);
		//		sB.addParameter("schwelle" , 2f);
		SignalAlgorithmus sA2 = aktie.addSignalAlgorithmus(new SignalMinMax());
		sA2.addParameter("dauer", 30);
		sA2.addParameter("indikator", iA);
		sA2.addParameter("schwelle", 1.5f);
		sA2.addIndikatorAlgorithmus(iA);
		aktie.rechneSignale();

		List<Zeitraum> liste = DateUtil.getJahresZeitraeumeTurnus(aktie.getZeitraumKurse(), 10);
		List<Integer> tage = DateUtil.getBewertungTage(1);
		// Bewertung wird durchgeführt mit den vorbereiteten Zeiträumen
		List<SignalBewertung> bewertungen = sV.bewerteSignalListe(aktie, liste, tage);

		SignalBewertungen sBs = new SignalBewertungen();
		sBs.addSignalBewertungen(bewertungen);
		sV.saveSignalBewertungen(sBs);

		aktie.writeFileKursIndikatorSignal();

		// Liste der Bewertungen wird gespeichert
		// sV.saveSignalBewertungListe(bewertungen);
	}

}
