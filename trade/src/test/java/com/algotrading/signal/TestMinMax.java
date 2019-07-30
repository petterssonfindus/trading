package com.algotrading.signal;

import java.util.List;

import com.algotrading.indikator.IndikatorAbweichung;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.util.AbstractTest;
import com.algotrading.util.Zeitraum;

public class TestMinMax extends AbstractTest {

	/*
	 * public void testIndikatorMinMax() { // Kursreihe erzeugen appl, dax Aktie
	 * aktie = Aktien.getInstance().getAktie("testaktie"); assertNotNull(aktie);
	 * assertTrue(aktie.getBoersenkurse().size() > 1);
	 * 
	 * // Indikator konfigurieren und an Aktie hängen IndikatorAlgorithmus iB =
	 * aktie.addIndikator(new IndikatorGD()); iB.addParameter("dauer", 10);
	 * 
	 * // Indikator berechnen und ausgeben aktie.rechneIndikatoren(); //
	 * aktie.writeFileIndikatoren();
	 * 
	 * // Signal konfigurieren an der Aktie SignalBeschreibung sB =
	 * aktie.createSignalBeschreibung(Signal.MinMax); sB.addParameter("indikator",
	 * iB); sB.addParameter("dauer", 15); // 15 Tage zurück
	 * sB.addParameter("schwelle", 1f); // 1-fache Standardabweichung
	 * sB.addParameter("durchbruch", 0); // tägliches Signal in der Extremzone
	 * 
	 * // Signale berechnen und ausgeben aktie.rechneSignale(); //
	 * aktie.writeFileSignale(); }
	 */

	public void testKurswertMinMax() {
// 		Aktie aktie = AktieVerzeichnis.getInstance().getAktie("testaktie");
		assertNotNull(aktie);
		assertTrue(aktie.getKursListe().size() > 1);

		// Indikator konfigurieren und an Aktie hängen
		IndikatorAlgorithmus iB = aktie.addIndikatorAlgorithmus(new IndikatorAbweichung());
		iB.addParameter("typ", 1); // Typ 1 = open

		// Indikator berechnen und ausgeben
		aktie.rechneIndikatoren();
		// am ersten Tag 18.03.2015 ist der open-Kurs 19,50
		assertEquals(19.5f, aktie.getKursListe().get(0).getIndikatorWert(iB).floatValue());
		// der close-Kurs ist 20.05
		assertEquals(20.05f, aktie.getKursListe().get(0).getKurs());

		// Signal konfigurieren und an Aktie hängen
		SignalAlgorithmus sA = aktie.addSignalAlgorithmus(new SignalMinMax());
		sA.addParameter("indikator", iB);
		sA.addParameter("dauer", 15); // Min-Max-Berechnung 15 Tage zurück
		sA.addParameter("schwelle", 1f); // 1-fache Standardabweichung
		sA.addParameter("durchbruch", 0); // tägliches Signal in der Extremzone

		// Signale berechnen und ausgeben
		aktie.rechneSignale();
		// Signal-Bewertung aggregieren und ausgeben
		Zeitraum zeitraum2 = new Zeitraum(2016, 2016);
		Zeitraum zeitraum3 = new Zeitraum(2017, 2017);
		aktie.bewerteSignale(zeitraum2, 10);
		aktie.bewerteSignale(zeitraum3, 10);
		List<SignalBewertung> sbs = sA.getBewertungen();
		for (SignalBewertung sb : sbs) {
			System.out.println("Sbs: " + sb);
		}
//		assertEquals(expected, actual);

//		aktie.writeFileIndikatoren();
//		aktie.writeFileSignale();
	}

}
