package com.algotrading.signal;

import java.io.File;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.algotrading.aktie.Aktie;
import com.algotrading.component.SignalVerwaltung;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGD;
import com.algotrading.signalbewertung.SignalBewertung;
import com.algotrading.signalbewertung.SignalBewertungen;
import com.algotrading.util.AbstractTest;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Zeitraum;

public class TestSignalBewertungen extends AbstractTest {

	@Autowired
	SignalVerwaltung sV;

	@Test
	public void testFindAll() {
		List<SignalBewertung> liste = sV.findAllSignalBewertung();
		for (SignalBewertung sB : liste) {
			System.out.println("SB: " + sB.toString());
		}
	}

	@Test
	public void testDeleteAllSignalBewertungen() {
		sV.deleteSignalBewertungenAll();
	}

	@Test
	public void testDeletesignalBewertungen() {
		long signalBewertungen = sV.countSignalBewertungen();
		long signalBewertung = sV.countSignalBewertung();
		long iA = sV.countIndikatorAlgorithmus();
		sV.printCount();
		sV.deleteSignalBewertungen(new Long(133485));
		sV.printCount();
		System.out.println("Signalbewertungen gelöscht: " + (signalBewertungen - sV.countSignalBewertungen()));
		System.out.println("Signalbewertung gelöscht: " + (signalBewertung - sV.countSignalBewertung()));
		System.out.println("IndikatorAlgorithmus gelöscht: " + (iA - sV.countIndikatorAlgorithmus()));
	}

	@Test
	public void testGetSignalBewertungen() {
		SignalBewertungen sB = sV.getSignalBewertungen(134354L);
		System.out.println("Signalbewertung: " + sB.getId() + " Anzahl: " + sB.getSignalBewertungen().size());
		for (SignalBewertung sBew : sB.getSignalBewertungen()) {
			System.out.println(sBew.toCSVString());
		}
	}

	@Test
	public void testWriteFilesignalBewertungen() {
		File file = sV.writeFileSignalBewertungen(134532L);
		System.out.println("File signalbewertungen Pfad: " + file.getAbsolutePath());
		System.out.println("File signalbewertungen Name: " + file.getName());
	}

	@Test
	public void testCreateSignalBewertungenWorking() {
		// Indikator konfigurieren und rechnen
		Aktie aktie = aV.getAktieLazy(48218L);
		IndikatorAlgorithmus iA = new IndikatorGD();
		iA.addParameter("dauer", 60);
		aktie.addIndikatorAlgorithmus(iA);
		aktie.rechneIndikatoren();

		// Signale konfigurieren und rechnen 
		SignalAlgorithmus sA = aktie.addSignalAlgorithmus(new SignalSchwelle());
		sA.addParameter("indikator", iA);
		sA.addParameter("schwelle", 0.025f);
		// dem SA einen IA mitgeben 
		sA.addIndikatorAlgorithmus(iA);
		aktie.rechneSignale();

		// Bewertung konfigurieren und berechnen 
		// Zeiträume bestimmen 
		List<Zeitraum> liste = DateUtil.getJahresZeitraeumeTurnus(aktie.getZeitraumKurse(), 1);
		// Zeitspannen zur Signalbewertung bestimmen 
		List<Integer> tage = DateUtil.getBewertungTage(2);
		// Performance berechnen
		sV.rechneSignalPerformance(aktie, tage);
		// Bewertungen durchführen 
		List<SignalBewertung> bewertungen = sV.bewerteSignalListe(aktie, liste, tage);
		System.out.println("SignalBewertungen berechnet: " + bewertungen.size());

		// SignalBewertungen zusammen fassen und speichern und ausgeben 
		SignalBewertungen sBs = new SignalBewertungen();
		sBs.addSignalBewertungen(bewertungen);
		// alle Kurse mit indikator und Signalen als csv speichern
		aV.writeFileKursIndikatorSignal(aktie);
		sV.saveSignalBewertungen(sBs);
		System.out.println("SignalBewertungen geschrieben: " + sBs.getId());
		// Signal-Ergebnisse als file ausgeben 
		File file = sV.writeFileSignalBewertungen(sBs.getId());
		System.out.println("File signalbewertungen Pfad: " + file.getAbsolutePath());

	}

	@Test
	public void testCreateSignalBewertungen() {
		Aktie aktie = aV.getAktieLazy(48218L);
		assertNotNull(aktie);
		//		List<Aktie> aktien = aV.getVerzeichnis().getAktien(DateUtil.createGregorianCalendar(1, 1, 2000));
		IndikatorAlgorithmus iA = new IndikatorGD();
		iA.addParameter("dauer", 30);
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
		sA2.addParameter("dauer", 60);
		sA2.addParameter("indikator", iA);
		sA2.addParameter("schwelle", 2.5f);
		sA2.addIndikatorAlgorithmus(iA);
		aktie.rechneSignale();

		List<Zeitraum> liste = DateUtil.getJahresZeitraeumeTurnus(aktie.getZeitraumKurse(), 10);
		List<Integer> tage = DateUtil.getBewertungTage(2);
		// für alle Signale Performance berechnen
		sV.rechneSignalPerformance(aktie, tage);
		// Bewertung wird durchgeführt mit den vorbereiteten Zeiträumen
		List<SignalBewertung> bewertungen = sV.bewerteSignalListe(aktie, liste, tage);
		System.out.println("SignalBewertungen berechnet: " + bewertungen.size());

		SignalBewertungen sBs = new SignalBewertungen();
		sBs.addSignalBewertungen(bewertungen);
		sV.saveSignalBewertungen(sBs);
		System.out.println("SignalBewertungen geschrieben: " + sBs.getId());

		File file = sV.writeFileSignalBewertungen(sBs.getId());
		System.out.println("File signalbewertungen Pfad: " + file.getAbsolutePath());
		System.out.println("File signalbewertungen Name: " + file.getName());
		//		aktie.writeFileKursIndikatorSignal();

		// Liste der Bewertungen wird gespeichert
		// sV.saveSignalBewertungListe(bewertungen);
	}

}
