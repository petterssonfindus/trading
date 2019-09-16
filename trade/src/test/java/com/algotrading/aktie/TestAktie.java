package com.algotrading.aktie;

import java.util.GregorianCalendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.algotrading.data.ImportCSV;
import com.algotrading.data.ReadDataYahoo;
import com.algotrading.util.AbstractTest;
import com.algotrading.util.DateUtil;

public class TestAktie extends AbstractTest {
	private static final Logger log = LogManager.getLogger(TestAktie.class);

	@Autowired
	private ReadDataYahoo readDataYahoo;

	@Autowired
	private ImportCSV importCSV;

	@Test
	public void testNewAktieMinimal() {
		Aktie aktie = new Aktie("VDAX_NEW");
		aV.saveAktie(aktie);
	}

	@Test
	public void testNewAktieAriva() {
		Aktie aktie = new Aktie("Umlaufrendite");
		aktie.setISIN(null);
		aktie.setQuelle(aV.QUELLE_Ariva);
		aktie.setWkn(null);
		aktie.setKuerzel(null);
		aV.saveAktie(aktie);
		aktie.setaV(aV);
		Aktie aktieNew = importCSV.readKurseArivaCSV("wkn_umlaufrendite__historic", aktie);
	}

	@Test
	public void testChangeAktie() {
		Aktie aktie = aV.getAktie("VDAX_NEW1M");
		aktie.setQuelle(1);
		aV.saveAktie(aktie);
		Aktie aktie2 = aV.getAktie(aktie.getId());
	}

	@Test
	public void testGetAktieByName() {
		Aktie aktie = aV.getAktie("umalauf1567762383227");
		assertNotNull(aktie);
		Aktie aktie2 = aV.getAktie("umal");
		assertNull(aktie2);

	}

	@Test
	public void testGetAktieMitKurse() {
		long beginn = new GregorianCalendar().getTimeInMillis();
		aktie = aV.getAktieMitKurse(8112l);
		long ende = new GregorianCalendar().getTimeInMillis();
		System.out.println("Auswertung dauerte Sekunden: " + ((ende - beginn) / 1000));
		System.out.println("Kurs0: " + DateUtil.formatDate(aktie.getKurse().get(0).getDatum()));
		System.out
				.println("Kursn: " + DateUtil.formatDate(aktie.getKurse().get(aktie.getKurse().size() - 1).getDatum()));
	}

	@Test
	public void testDelete() {
		aV.deleteAktie(aV.getAktie("dax"));
	}

	@Test
	public void testDeleteByID() {
		aV.deleteAktie(aV.getAktie(33L));
	}

	@Test
	public void testKursAktie() {
		Aktie aktie = aV.getAktie(257l);
		// ein neuer Kurs
		Kurs kurs = new Kurs();
		kurs.setDatum(new GregorianCalendar());
		kurs.close = 100;
		aktie.addKurs(kurs);
		// die Kursliste speichern
		aV.saveAktie(aktie);
	}

	public void testGetKursZukunft() {

		Aktie aktie = aV.getAktie("dax");
		List<Kurs> kursreihe = aktie.getKurse();

		assertNotNull(kursreihe);
		assertTrue(kursreihe.size() > 1);
		Kurs kurs = kursreihe.get(100);
		assertNotNull(kurs);
		Kurs kurs2 = kurs.getKursTage(10);
		assertEquals(22.5f, kurs2.getKurs());
		Kurs kurs3 = kurs.getKursTage(-10);
		assertEquals(23.03f, kurs3.getKurs());
		// 
	}

	public void testGetKurse() {
		Aktie aktie = aV.getAktie("dax");
		List<Kurs> kursreihe = aktie.getKursListe();
		assertNotNull(kursreihe);
		assertTrue(kursreihe.size() > 1);
		log.info("Kursreihe hat Kurse: " + kursreihe.size());
		Kurs kurs = kursreihe.get(100);
		assertEquals(5.41476f, kurs.open);
		assertEquals(5.30662f, kurs.close);
		assertEquals(5.30662f, kurs.low);
		assertEquals(5.4468f, kurs.high);
		assertEquals(45900, kurs.volume);
	}

	/*
		public void testKursreihe() {
			log.info("Start AktieTest");
			Aktie aktie = Aktien.getInstance().getAktie("AA");
			ArrayList<Kurs> kursreihe = aktie.getBoersenkurse(); 
			
			Statistik.rechneVola(aktie, 10);
			Statistik.rechneVola(aktie, 30);
			Statistik.rechneVola(aktie, 100);
			log.info("Kursreihe hat Kurse: " + kursreihe.size());
			Statistik.rechneIndikatoren(aktie);
			log.info("Schreibe File " );
			
			aktie.writeFileIndikatoren();
	//		Statistik.rechneIndikatoren(kursreihe);
	//		Signalsuche.rechneSignale(kursreihe);
	//		kursreihe.writeIndikatorenSignale();
		}
	 */

}
