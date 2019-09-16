package com.algotrading.signal;

import java.util.GregorianCalendar;
import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.component.AktieVerzeichnis;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGDalt;
import com.algotrading.util.AbstractTest;
import com.algotrading.util.Zeitraum;

public class TestGDDurchbruch extends AbstractTest {

	private static Aktie aktie;
	private static IndikatorAlgorithmus indikator10;
	private static IndikatorAlgorithmus indikator30;
	private static SignalAlgorithmus signal;
	private static SignalAlgorithmus signal2;
	private static SignalAlgorithmus signal3;
	private static SignalAlgorithmus signal4;
	GregorianCalendar beginn = new GregorianCalendar(2015, 4, 1);
	GregorianCalendar ende = new GregorianCalendar(2018, 1, 1);

	public void setUp() {
		// TODO Auto-generated method stub
		super.setUp();

		aktie = aV.getAktie("testaktie");
		assertNotNull(aktie);
		assertTrue(aktie.getKursListe().size() > 1);

		// Indikator1 konfigurieren und an aktie hängen
		indikator10 = aktie.addIndikatorAlgorithmus(new IndikatorGDalt());
		aktie.addIndikatorAlgorithmus(indikator10);
		indikator10.addParameter("dauer", 10);

		// Indikator3 konfigurieren
		indikator30 = aktie.addIndikatorAlgorithmus(new IndikatorGDalt());
		aktie.addIndikatorAlgorithmus(indikator30);
		indikator30.addParameter("dauer", 30);

		// Indikatoren berechnen
		aktie.rechneIndikatoren();

		// Signal1 Schwelle 0.01
		signal = aktie.addSignalAlgorithmus(new SignalGDDurchbruch());
		signal.addParameter("indikator", indikator10);
		signal.addParameter("zeitraum", new Zeitraum(beginn, ende));
		signal.addParameter("schwelle", 0.01f);
		// Signal2 Schwelle 0.02
		signal2 = aktie.addSignalAlgorithmus(new SignalGDDurchbruch());
		signal2.addParameter("indikator", indikator10);
		signal2.addParameter("zeitraum", new Zeitraum(beginn, ende));
		signal2.addParameter("schwelle", 0.02f);
		// Signal3 Schwelle 0.03
		signal3 = aktie.addSignalAlgorithmus(new SignalGDDurchbruch());
		signal3.addParameter("indikator", indikator10);
		signal3.addParameter("zeitraum", new Zeitraum(beginn, ende));
		signal3.addParameter("schwelle", 0.03f);
		// Signal4 Dauer 30 Tage
		signal4 = aktie.addSignalAlgorithmus(new SignalGDDurchbruch());
		signal4.addParameter("indikator", indikator30);
		signal4.addParameter("zeitraum", new Zeitraum(beginn, ende));
		signal4.addParameter("schwelle", 0.01f);

		// Signale berechnen
		aktie.rechneSignale();
	}

	public void testGDSchnittSchwelle() {
		List<Signal> signale = aktie.getSignale(signal);
		List<Signal> signale2 = aktie.getSignale(signal2);
		List<Signal> signale3 = aktie.getSignale(signal3);

		assertTrue(signale.size() == 96);
		assertTrue(signale2.size() == 58);
		assertTrue(signale3.size() == 26);
	}

	public void testGDSchnitt30Tage() {
		List<Signal> signale4 = aktie.getSignale(signal4);
		assertTrue(signale4.size() == 60);

	}

}
