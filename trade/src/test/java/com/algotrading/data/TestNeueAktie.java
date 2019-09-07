package com.algotrading.data;

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
import com.algotrading.component.AktieVerwaltung;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class TestNeueAktie {

	@Autowired
	private AktieVerwaltung aV;

	@Test
	public void testNeueAktie() {
		String kuerzel = "umlaufrendite-de-oeffentliche-hand";
		String firmenname = "Umlaufrendite Deutschland";
		String indexname = "";
		byte boersenplatz = 0;

		Aktie aktie = new Aktie(kuerzel, firmenname, indexname, boersenplatz);
		aktie.setQuelle(3);
		aktie.setISIN("");

		aV.saveAktie(aktie);
		//		DBManager.neueAktie(aktie);

		// Kurse einlesen
		if (aktie.getQuelle() == 1) {
			ReadDataYahoo.YahooWSController(kuerzel);
		} else if (aktie.getQuelle() == 2) {
			System.out.println("Kurse für Finanzen müssen manuell Beginndatum vorgegeben werden.");

		}

	}

}
