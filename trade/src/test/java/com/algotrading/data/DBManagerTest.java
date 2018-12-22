package com.algotrading.data;

import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;

import com.algotrading.indikator.Indikatoren;
import com.algotrading.signal.Signal;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

public class DBManagerTest extends TestCase {
	private static final Logger log = LogManager.getLogger(DBManagerTest.class);

	GregorianCalendar cal = new GregorianCalendar(2018,01,01);

/*	
	public void testAddTageskurs() {
		GregorianCalendar datum1 = new GregorianCalendar(2017,1,1);
		Tageskurs kurs = new Tageskurs();
		kurs.close = 111.22f;
		kurs.name = "appl";
		kurs.datum = datum1;
		DBManager.addTageskurs(kurs);
		log.info("AddTageskurs erfolgreich getestet");
	}
*/	

/*	
	public void testGetTageskurs() {
		GregorianCalendar cal = new GregorianCalendar(2018,01,01);
		Tageskurs kurs = DBManager.getTageskurs("appl", cal);
		assertNotNull(kurs);
		log.info("Tageskurs: " + kurs.toString());
		
	}
*/
/*
	public void testRechneTageskurs() {
		GregorianCalendar cal = new GregorianCalendar();
		Kursreihe kursreihe = DBManager.getKursreihe("appl", cal);
		assertNotNull(kursreihe);
		assertTrue(kursreihe.kurse.size() > 1);
		log.info("Kursreihe hat Kurse: " + kursreihe.kurse.size());
		Statistik.rechneIndikatoren(kursreihe);
		Signal.rechneSignale(kursreihe);
		
		Tageskurs tageskurs = kursreihe.kurse.get(1);
		assertNotNull(tageskurs);
		kursreihe.writeFile();
//		DBManager.schreibeTageskurse(kursreihe);
		
	}
*/	
/*
	public void testGetKursreihe() {
		ArrayList<Kurs> kursreihe = DBManager.getKursreihe("appl");
		assertNotNull(kursreihe);
		assertTrue(kursreihe.size() > 1);
		log.info("Kursreihe appl hat Kurse: " + kursreihe.size());
		
	}
	public void testGetKursreiheBeginn() {
		GregorianCalendar cal = new GregorianCalendar(2017,06,01);
		ArrayList<Kurs> kursreihe = DBManager.getKursreihe("dax", cal);
		assertNotNull(kursreihe);
		assertTrue(kursreihe.size() > 50);
		log.info("DAX ab " + Util.formatDate(cal) + " hat Kurse: " + kursreihe.size());
		
	}
*/
/*	
	public void testTrageStammdatenEin () {
		for (Aktie aktie : Aktien.getInstance().getAllAktien()) {
			DBManager.trageNeueAktieInStammdatenEin(aktie);
		}
		
	}
*/
	public void testGetVerzeichnis() {
		
		HashMap<String, Aktie> test = DBManager.getVerzeichnis();
		assertNotNull(test);
		assertTrue(test.size() > 30);
		
	}
	
	public void testGetZeitraumAktie () {
		Zeitraum zeitraum = DBManager.getZeitraumVorhandeneKurse(Aktien.getInstance().getAktie("testaktie"));
		assertNotNull(zeitraum);
		assertEquals(1426633200000L, zeitraum.beginn.getTimeInMillis());
		assertEquals(1518130800000L, zeitraum.ende.getTimeInMillis());
		
	}
	
	/**
	 * Der letzte gespeicherte Kurs
	 */
	public void testGetLastKurs () {
		GregorianCalendar test = DBManager.getLastKurs(Aktien.getInstance().getAktie("testaktie"));
		assertNotNull(test);
		assertEquals("2018-02-09", Util.formatDate(test));
	}
	
	
}
