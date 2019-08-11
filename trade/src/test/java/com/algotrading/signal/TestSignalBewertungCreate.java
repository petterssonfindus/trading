package com.algotrading.signal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.algotrading.Application;
import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerwaltung;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.indikator.IndikatorAbweichung;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGD;
import com.algotrading.util.Zeitraum;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class TestSignalBewertungCreate {

	@Autowired
	AktieVerwaltung aV;

	@Autowired
	Signalverwaltung sV;

	@Autowired
	SignalBewertungDAO sBDAO;

	/**
	 * Erzeugt 2 SignalBewertungen ohne IndikatorAlgorithmen unterschiedliche
	 * Zeiträume, jweils 10 Tage
	 */
	@Test
	public void test1SA() {
		Aktie aktie = AktieVerzeichnis.getInstance().getAktieOhneKurse("testaktie");
		assertNotNull(aktie);
		assertTrue(aktie.getKursListe().size() > 1);

		// Signal konfigurieren und an Aktie hängen
		SignalAlgorithmus sA = aktie.addSignalAlgorithmus(new SignalMinMax());
		sA.addParameter("dauer", 15); // Min-Max-Berechnung 15 Tage zurück
		sA.addParameter("schwelle", 1f); // 1-fache Standardabweichung
		sA.addParameter("durchbruch", 0); // tägliches Signal in der Extremzone

		// Signale berechnen und ausgeben
		aktie.rechneSignale();
		// Signal-Bewertung aggregieren und ausgeben
//		Zeitraum zeitraum1 = new Zeitraum(2015, 2015);
		Zeitraum zeitraum2 = new Zeitraum(2016, 2016);
		Zeitraum zeitraum3 = new Zeitraum(2017, 2017);
//		aktie.bewerteSignale(zeitraum1, 10);
		aV.bewerteSignale(aktie, zeitraum2, 10);
		aV.bewerteSignale(aktie, zeitraum3, 10);

		List<SignalBewertung> sBs = sA.getBewertungen();
		// die Bewertungen werden gespeichert
		for (SignalBewertung sB : sBs) {
			sV.save(sB);

		}
		assertTrue(true);

		for (SignalBewertung sb : sBs) {
			System.out.println("Sbs: " + sb);
		}
//		assertEquals(expected, actual);

//		aktie.writeFileKursIndikatorSignal();
//		aktie.writeFileSignale();
	}

	/**
	 * Erzeugt 1 SignalBewertung mit 1 IndikatorAlgorithmus (GD, 30 Tage) als
	 * Referenz
	 */
	@Test
	public void test1SA1IA() {
		SignalBewertung sB = createSignalBewertung();
		sV.save(sB);

	}

	public SignalBewertung createSignalBewertung() {
		Aktie aktie = AktieVerzeichnis.newInstance().getAktieOhneKurse("testaktie");

		// Indikator konfigurieren und an Aktie hängen
		IndikatorAlgorithmus iA2 = aktie.addIndikatorAlgorithmus(new IndikatorGD());
		iA2.addParameter("dauer", 30);

		// Indikator berechnen und ausgeben
		aktie.rechneIndikatoren();
		// der close-Kurs ist 20.05
		assertThat(20.05f == aktie.getKursListe().get(0).getKurs());

		// Signal konfigurieren und an Aktie hängen
		SignalAlgorithmus sA = aktie.addSignalAlgorithmus(new SignalMinMax());
		// dem SignalAlgorithmus einen IndikatorAlgorithmus beifügen
		sA.addIndikatorAlgorithmus(iA2);
		sA.addParameter("dauer", 14); // Min-Max-Berechnung 15 Tage zurück
		sA.addParameter("schwelle", 1f); // 1-fache Standardabweichung
		sA.addParameter("durchbruch", 0); // tägliches Signal in der Extremzone

		// Signale berechnen und ausgeben
		aktie.rechneSignale();
		// Signal-Bewertung aggregieren und ausgeben
		Zeitraum zeitraum3 = new Zeitraum(2017, 2017);
		aV.bewerteSignale(aktie, zeitraum3, 10);

		List<SignalBewertung> sBs = sA.getBewertungen();
		return sBs.get(0);
	}

	@Test
	public void test1SA2IA() {
		Aktie aktie = AktieVerzeichnis.getInstance().getAktieOhneKurse("testaktie");
		assertNotNull(aktie);
		assertTrue(aktie.getKursListe().size() > 1);

		// Indikator konfigurieren und an Aktie hängen
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorAbweichung());
		iA.addParameter("typ", 1); // Typ 1 = open
		IndikatorAlgorithmus iA2 = aktie.addIndikatorAlgorithmus(new IndikatorGD());
		iA2.addParameter("dauer", 30);

		// Indikator berechnen und ausgeben
		aktie.rechneIndikatoren();
		// am ersten Tag 18.03.2015 ist der open-Kurs 19,50
		assertThat(19.5f == aktie.getKursListe().get(0).getIndikatorWert(iA));
		// der close-Kurs ist 20.05
		assertThat(20.05f == aktie.getKursListe().get(0).getKurs());

		// Signal konfigurieren und an Aktie hängen
		SignalAlgorithmus sA = aktie.addSignalAlgorithmus(new SignalMinMax());
		sA.addParameter("indikator", iA);
		sA.addParameter("dauer", 15); // Min-Max-Berechnung 15 Tage zurück
		sA.addParameter("schwelle", 1f); // 1-fache Standardabweichung
		sA.addParameter("durchbruch", 0); // tägliches Signal in der Extremzone

		// Signale berechnen und ausgeben
		aktie.rechneSignale();
		// Signal-Bewertung aggregieren und ausgeben
//		Zeitraum zeitraum1 = new Zeitraum(2015, 2015);
		Zeitraum zeitraum2 = new Zeitraum(2016, 2016);
		Zeitraum zeitraum3 = new Zeitraum(2017, 2017);
//		aktie.bewerteSignale(zeitraum1, 10);
		aV.bewerteSignale(aktie, zeitraum2, 10);
		aV.bewerteSignale(aktie, zeitraum3, 10);

		List<SignalBewertung> sBs = sA.getBewertungen();
		// die Bewertungen werden gespeichert
		for (SignalBewertung sB : sBs) {
			sV.save(sB);

		}
		assertTrue(true);

		for (SignalBewertung sb : sBs) {
			System.out.println("Sbs: " + sb);
		}
//		assertEquals(expected, actual);

//		aktie.writeFileKursIndikatorSignal();
//		aktie.writeFileSignale();
	}

}
