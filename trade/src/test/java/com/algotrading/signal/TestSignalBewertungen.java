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
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.component.Signalverwaltung;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGDalt;
import com.algotrading.indikator.IndikatorOBV;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Zeitraum;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext

public class TestSignalBewertungen {

	@Autowired
	Signalverwaltung sV;

	@Test
	public void testCreateSignalBewertungen() {
		Aktie aktie = AktieVerzeichnis.getInstance().getAktieOhneKurse("^gdaxi");
//		List<Aktie> aktien = AktieVerzeichnis.getInstance().getAktien(DateUtil.createGregorianCalendar(1, 1, 2000));
		IndikatorAlgorithmus iA = new IndikatorGDalt();
		iA.addParameter("dauer", 10);
		// AktieVerzeichnis.addIndikatorAlgorithmus(aktien, iA);
		aktie.addIndikatorAlgorithmus(iA);

		IndikatorAlgorithmus iA2 = aktie.addIndikatorAlgorithmus(new IndikatorOBV());
		iA2.addParameter("dauer", 10);
		iA2.addParameter("stabw", 10);
		iA2.addParameter("faktor", 1);

		aktie.rechneIndikatoren();

//		SignalBeschreibung sB = aktie.createSignalBeschreibung(Signal.MinMax);
//		sB.addParameter("dauer", 30);
//		sB.addParameter("indikator" , iA);
//		sB.addParameter("schwelle" , 2f);
		SignalAlgorithmus sA2 = aktie.addSignalAlgorithmus(new SignalMinMax());
		sA2.addParameter("dauer", 30);
		sA2.addParameter("indikator", iA2);
		sA2.addParameter("schwelle", 1.5f);
		sA2.addIndikatorAlgorithmus(iA2);
		aktie.rechneSignale();

		List<Zeitraum> liste = DateUtil.getJahresZeitraeumeTurnus(aktie.getZeitraumKurse(), 10);
		for (Zeitraum zeitraumL : liste) {
			System.out.println(
					"Zeitraum:" + DateUtil.getJahr(zeitraumL.getBeginn()) + DateUtil.getJahr(zeitraumL.getEnde()));
		}
		List<Integer> tage = DateUtil.getBewertungTage(1);
		// Bewertung wird durchgeführt mit den vorbereiteten Zeiträumen
		@SuppressWarnings("unused")
		List<SignalBewertung> bewertungen = sV.bewerteSignalListeAndSave(aktie, liste, tage);

	}

}
