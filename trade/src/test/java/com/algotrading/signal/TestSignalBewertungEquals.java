package com.algotrading.signal;

import java.util.List;

import org.junit.Test;

import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.indikator.IndikatorAbweichung;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGD;
import com.algotrading.util.AbstractTest;
import com.algotrading.util.Zeitraum;

public class TestSignalBewertungEquals extends AbstractTest {

	@Test
	public void testSignalbewertungEquals() {
		System.out.println("TestSignalBewertungEquals ausgeführt");
		SignalBewertung sB1 = createSignalBewertungDurchBerechnung();
		SignalBewertung sB2 = createSignalBewertungDurchDB(21l);

		boolean result = sV.equalsSignalBewertung(sB1, sB2);

		assertTrue(sB1.equals(sB2));
		// Anzahl Käufe verändern
		sB2.setKauf(sB2.getKauf() + 1);
		// jetzt ist Differenz vorhanden
		assertFalse(sB1.equals(sB2));
		// Anzahl Käufe wieder zurück stellen
		sB2.setKauf(sB1.getKauf());
		sB2.setKaufKorrekt(sB2.getKaufKorrekt() * 1.11f);
		assertFalse(sB1.equals(sB2));
		sB2.setKaufKorrekt(sB1.getKaufKorrekt());

		SignalAlgorithmus sA = sB1.getSignalAlgorithmus();
		SignalAlgorithmus sA2 = sB2.getSignalAlgorithmus();
		assertTrue(sA.getKurzname().matches(sA2.getKurzname()));

		sA.getAllParameter().remove("dauer");
		assertFalse(sB1.equals(sB2));
		// Parameter mit 14 Tage wieder hinzufügen
		sA.addParameter("dauer", 14);
		assertFalse(sB1.equals(sB2));
		sA.getAllParameter().remove("dauer");
		sA.addParameter("dauer", 15);
		assertTrue(sA.getKurzname().matches(sA2.getKurzname()));

	}

	private SignalBewertung createSignalBewertungDurchDB(long x) {
		SignalBewertung test = sV.findWithIA(Long.valueOf(x));
		return test;
	}

	private SignalBewertung createSignalBewertungDurchBerechnung() {
		aktie = AktieVerzeichnis.newInstance().getAktieMitKurse("testaktie");
		// Indikator konfigurieren und an Aktie hängen
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorAbweichung());
		iA.addParameter("typ", 1); // Typ 1 = open
		IndikatorAlgorithmus iA2 = aktie.addIndikatorAlgorithmus(new IndikatorGD());
		iA2.addParameter("dauer", 30);

		// Indikator berechnen und ausgeben
		aktie.rechneIndikatoren();

		// Signal konfigurieren und an Aktie hängen
		SignalAlgorithmus sA;
//		if (aktie.getSignalAlgorithmen().size() == 0) {
		sA = aktie.addSignalAlgorithmus(new SignalMinMax());
		sA.addParameter("indikator", iA);
		sA.addParameter("dauer", 15); // Min-Max-Berechnung 15 Tage zurück
		sA.addParameter("schwelle", 1f); // 1-fache Standardabweichung
		sA.addParameter("durchbruch", 0); // tägliches Signal in der Extremzone
//		}
//		else sA = aktie.getSignalAlgorithmen().get(0);

		// Signale berechnen und ausgeben
		aktie.rechneSignale();
		// Signal-Bewertung aggregieren und ausgeben
		Zeitraum zeitraum3 = new Zeitraum(2017, 2017);
		aktie.bewerteSignale(zeitraum3, 10);

		List<SignalBewertung> sBs = sA.getBewertungen();
		// die letzte Bewertung wird zurück gegeben
		// wenn 2 orhanden sind, wird die letzte zurück gegeben.
		SignalBewertung result = sBs.get(sBs.size() - 1);
		sV.save(result);
		return result;

	}

}