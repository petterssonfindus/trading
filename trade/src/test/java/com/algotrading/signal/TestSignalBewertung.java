package com.algotrading.signal;

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
import com.algotrading.aktie.Aktien;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGD;
import com.algotrading.indikator.IndikatorAbweichung;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment=WebEnvironment.RANDOM_PORT)
@DirtiesContext

public class TestSignalBewertung extends TestCase {
	
	@Autowired
	SignalBewertungRepository sBR; 
	
	@Test
	public void testKurswertMinMaxBewertung() {
		Aktie aktie = Aktien.getInstance().getAktie("testaktie");
		assertNotNull(aktie);
		assertTrue(aktie.getBoersenkurse().size() > 1);
		
		// Indikator konfigurieren und an Aktie hängen
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorAbweichung());
		iA.addParameter("typ", 1);  // Typ 1 = open
		IndikatorAlgorithmus iA2 = aktie.addIndikatorAlgorithmus(new IndikatorGD());
		iA2.addParameter("dauer", 30);
		
		// Indikator berechnen und ausgeben 
		aktie.rechneIndikatoren();
		// am ersten Tag 18.03.2015 ist der open-Kurs 19,50 
		assertEquals(19.5f, aktie.getBoersenkurse().get(0).getIndikatorWert(iA));
		// der close-Kurs ist 20.05
		assertEquals(20.05f, aktie.getBoersenkurse().get(0).getKurs());
		
		// Signal konfigurieren und an Aktie hängen 
		SignalAlgorithmus sA = aktie.createSignalAlgorithmus(new SignalMinMax());
		sA.addParameter("indikator", iA);
		sA.addParameter("dauer", 15);		// Min-Max-Berechnung 15 Tage zurück 
		sA.addParameter("schwelle", 1f);		// 1-fache Standardabweichung
		sA.addParameter("durchbruch", 0);	// tägliches Signal in der Extremzone
		
		// Signale berechnen und ausgeben 
		aktie.rechneSignale();
		// Signal-Bewertung aggregieren und ausgeben 
//		Zeitraum zeitraum1 = new Zeitraum(2015, 2015);
		Zeitraum zeitraum2 = new Zeitraum(2016, 2016);
		Zeitraum zeitraum3 = new Zeitraum(2017, 2017);
//		aktie.bewerteSignale(zeitraum1, 10);
		aktie.bewerteSignale(zeitraum2, 10);
		aktie.bewerteSignale(zeitraum3, 10);
		
		List<SignalBewertung> sBs = sA.getBewertungen();
		// die Bewertungen werden gespeichert
		for (SignalBewertung sB : sBs) {
			sBR.save(sB);
			
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
