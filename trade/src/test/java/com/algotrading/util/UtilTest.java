package com.algotrading.util;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import junit.framework.TestCase;

public class UtilTest extends TestCase {
	private static final Logger log = LogManager.getLogger(UtilTest.class);
	
	public void testUnixTime () {
		long x = 1517094000L;
		long x2 = 567817200L;
		GregorianCalendar y = DateUtil.unixTimeToGregorianCalendar(x);
		GregorianCalendar y2 = DateUtil.unixTimeToGregorianCalendar(x2);
		String d1 = DateUtil.formatDate(y);
		String d2 = DateUtil.formatDate(y2);
		System.out.println("Zeit aus " + x + " ist " + d1);
		System.out.println("Zeit aus " + x2 + " ist " + d2);
		assertEquals("2018-01-28", d1);
		assertEquals("1987-12-30", d2);
	}
	
	public void testUnixTimeTransform () {
		long x = 1000 * 1517094000L;
		long x2 = 1000 * 567817200L;
		GregorianCalendar y = DateUtil.toGregorianCalendar(x);
		GregorianCalendar y2 = DateUtil.toGregorianCalendar(x2);
		String d1 = DateUtil.formatDate(y);
		String d2 = DateUtil.formatDate(y2);
		System.out.println("Zeit aus " + x + " ist " + d1);
		System.out.println("Zeit aus " + x2 + " ist " + d2);
		assertEquals("2018-01-28", d1);
		assertEquals("1987-12-30", d2);
	}
	
	public void testFloatString () {
		// Float rechnet mit 7 signifikanten Stellen 
		float test = 17.838f;
		String testString = Util.toString(test);
		float test2 = 1234567f;
		String testString2 = Util.toString(test2);
		System.out.println("FloatToString: " + testString + "  " + testString2);
		assertEquals("17,838", testString);
		assertEquals("1.234.567", testString2);
	}
	
	public void testGetHeute () {
		GregorianCalendar heute = DateUtil.getHeute();
		assertNotNull(heute);
		log.info("Heute: " + DateUtil.formatDate(heute));
		System.out.println("Heute: " + DateUtil.formatDate(heute));
	}
	
	public void testGetLetzterHandelstag() {
		DateUtil.getLetzterHandelstag();
	}
	
	public void testCreateGregCal () {
		GregorianCalendar test = DateUtil.createGregorianCalendar(01, 01, 2017);
		String testString = DateUtil.formatDate(test, "-", false);
		assertEquals("01-01-2017", testString);
		GregorianCalendar test2 = DateUtil.createGregorianCalendar(31, 12, 2017);
		assertEquals("31-12-2017", DateUtil.formatDate(test2, "-", false));
	}
	
	public void testZeitraum () {
		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		GregorianCalendar ende = new GregorianCalendar(2018,0,2);
		GregorianCalendar test1 = new GregorianCalendar(2017,11,3);
		GregorianCalendar test2 = new GregorianCalendar(2018,11,2);
		Zeitraum zs1 = new Zeitraum(beginn, ende);
		Zeitraum zs2 = new Zeitraum(beginn, ende);
		Zeitraum zs3 = new Zeitraum(beginn, test1);
		Zeitraum zs4 = new Zeitraum(beginn, test2);
		assertTrue(zs1.equals(zs2));
		assertFalse(zs1.equals(zs3));
		assertFalse(zs1.equals(zs4));
		assertFalse(zs2.equals(zs3));
		assertFalse(zs1 == zs2);
		
	}
	
	public void testRundeBetrag () {
		assertEquals(0.11f, Util.rundeBetrag(0.1111f));
		assertEquals(6.67f, Util.rundeBetrag(6.6666f));
		assertEquals(0.01f, Util.rundeBetrag(0.006f));
		assertEquals(0.01f, Util.rundeBetrag(0.005f));
		assertEquals(0f, Util.rundeBetrag(0.004f));
	}
	
	public void testRechnePerformance() {
		float ergebnis = Util.rechnePerformance(100f, 105f);
		assertEquals(0.05F, ergebnis);
		float ergebnis2= Util.rechnePerformance(100f, 200f);
		assertEquals(1F, ergebnis2);
		float ergebnis3 = Util.rechnePerformance(100f, 80f);
		assertEquals(-.2F, ergebnis3);
	}
	
	public void testRechnePerformancePA() {
				float ergebnis = Util.rechnePerformancePA(100f, 105f, 250);
				assertEquals(0.05F, ergebnis);
				float ergebnis2 = Util.rechnePerformancePA(100f, 105f, 125);
				assertEquals(0.1F, ergebnis2);
				float ergebnis3 = Util.rechnePerformancePA(100f, 105f, 30);
				float ergebnis3a = Util.rundeBetrag(ergebnis3);
				assertEquals(0.42F, ergebnis3a);
				float ergebnis4 = Util.rundeBetrag(ergebnis3);
				assertEquals(0.42F, ergebnis4);
				float ergebnis5 = Util.rechnePerformancePA(100f, 200f, 500);
				assertEquals(0.5F, ergebnis5);
			}
	
	public void testDatumFormat() {
//		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		GregorianCalendar beginn = DateUtil.createGregorianCalendar(2,12,2017);
		assertTrue(DateUtil.formatDate(beginn).equalsIgnoreCase("2017-12-02"));
		GregorianCalendar ende = new GregorianCalendar(2018,0,2);
		assertTrue(DateUtil.formatDate(ende).equalsIgnoreCase("2018-01-02"));
		log.info("Beginn: " + DateUtil.formatDate(beginn) + " Ende: "+ DateUtil.formatDate(ende));
	}
	
	public void testDatumFormatTrenner() {
		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		String test1 = DateUtil.formatDate(beginn, ".");
		assertTrue(test1.equalsIgnoreCase("2017.12.02"));
		GregorianCalendar ende = new GregorianCalendar(2018,0,2);
		String test2 = DateUtil.formatDate(ende, ":");
		assertTrue(test2.equalsIgnoreCase("2018:01:02"));
		log.info("Beginn: " + test1 + " Ende: "+ test2);
		
	}
	
	public void testDatumFormatTrennerJahr() {
		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		String test1 = DateUtil.formatDate(beginn, ".", true);
		assertTrue(test1.equalsIgnoreCase("2017.12.02"));
		String test2 = DateUtil.formatDate(beginn, ".", false);
		assertTrue(test2.equalsIgnoreCase("02.12.2017"));
		
	}
	
	public void testParseDatumJJJJ_MM_TT () {
		String testDatum = "2017-12-02";
		GregorianCalendar datum = DateUtil.parseDatum(testDatum);
		assertNotNull(datum);
	}
	
	public void testParseDatumTT_MM_JJJJ () {
		String testDatum = "04.01.2010";
		GregorianCalendar datum = DateUtil.parseDatum(testDatum);
		assertNotNull(datum);
		String test = DateUtil.formatDate(datum, "-", true);
		assertEquals("2010-01-04", test);
	}
	
	public void testParseFloat () {
		String testString = "40.12";
		Float testFloat = Util.parseFloat(testString);
		assertNotNull(testFloat);
		assertEquals(new Float(40.12f), testFloat);
	}
	
	public void testParseFloatKomma() {
		String testString2 = "40,12";
		Float testFloat2 = Util.parseFloat(testString2);
		assertNotNull(testFloat2);
		assertEquals(new Float(40.12f), testFloat2);
	}
	
	public void testAnzahlTage () {
		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		GregorianCalendar ende = new GregorianCalendar(2018,0,2);
		GregorianCalendar datum1 = new GregorianCalendar(2018,0,1);
		GregorianCalendar datum2 = new GregorianCalendar(2017,11,3);
		GregorianCalendar datum3 = new GregorianCalendar(2017,11,31);
		assertEquals(31, DateUtil.anzahlTage(beginn, ende));
		assertEquals(30, DateUtil.anzahlTage(beginn, datum1));
		assertEquals(1, DateUtil.anzahlTage(beginn, datum2));
		assertEquals(29, DateUtil.anzahlTage(beginn, datum3));
	}
	
	public void testAddTage() {
		GregorianCalendar testDatum;
		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		testDatum = DateUtil.addTage(beginn, 1);
		assertEquals(DateUtil.formatDate(testDatum), "2017-12-03");
		testDatum = DateUtil.addTage(beginn, 10);
		assertEquals(DateUtil.formatDate(testDatum), "2017-12-12");
		testDatum = DateUtil.addTage(beginn, 30);
		assertEquals(DateUtil.formatDate(testDatum), "2018-01-01");
		testDatum = DateUtil.addTage(beginn, 60);
		assertEquals(DateUtil.formatDate(testDatum), "2018-01-31");
		testDatum = DateUtil.addTage(beginn, 365);
		assertEquals(DateUtil.formatDate(testDatum), "2018-12-02");
		
	}
	
	public void testIstInZeitraum () {
		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		GregorianCalendar ende = new GregorianCalendar(2018,0,2);
		GregorianCalendar datum1 = new GregorianCalendar(2018,0,1);
		GregorianCalendar datum2 = new GregorianCalendar(2017,11,3);
		GregorianCalendar datum3 = new GregorianCalendar(2017,11,31);
		GregorianCalendar datum4 = new GregorianCalendar(2016,11,31);
		GregorianCalendar datum5 = new GregorianCalendar(2019,11,31);
		assertTrue(DateUtil.istInZeitraum(datum1, beginn, ende));
		assertTrue(DateUtil.istInZeitraum(datum2, beginn, ende));
		assertTrue(DateUtil.istInZeitraum(datum3, beginn, ende));
		assertTrue(DateUtil.istInZeitraum(beginn, beginn, ende));
		assertTrue(DateUtil.istInZeitraum(ende, beginn, ende));
		assertFalse(DateUtil.istInZeitraum(datum4, beginn, ende));
		assertFalse(DateUtil.istInZeitraum(datum5, beginn, ende));
	}
	
	public void testUserDirectory () {
		log.info("User-Country: " + Util.getUserProperty("country"));
		log.info("User-Directory: " + Util.getUserProperty("dir"));
		log.info("User-Home: " + Util.getUserProperty("home"));
		log.info("User-Language: " + Util.getUserProperty("language"));
		log.info("User-Name: " + Util.getUserProperty("name"));
	}
	
	public void testToGregorianCalendar() {
		Date date = new Date(1000000000); 
		GregorianCalendar test = DateUtil.toGregorianCalendar(date);
		assertEquals("1970-01-12", DateUtil.formatDate(test));
				
	}
	

}
