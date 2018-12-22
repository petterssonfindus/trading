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
		GregorianCalendar y = Util.unixTimeToGregorianCalendar(x);
		GregorianCalendar y2 = Util.unixTimeToGregorianCalendar(x2);
		String d1 = Util.formatDate(y);
		String d2 = Util.formatDate(y2);
		System.out.println("Zeit aus " + x + " ist " + d1);
		System.out.println("Zeit aus " + x2 + " ist " + d2);
		assertEquals("2018-01-28", d1);
		assertEquals("1987-12-30", d2);
	}
	
	public void testUnixTimeTransform () {
		long x = 1000 * 1517094000L;
		long x2 = 1000 * 567817200L;
		GregorianCalendar y = Util.toGregorianCalendar(x);
		GregorianCalendar y2 = Util.toGregorianCalendar(x2);
		String d1 = Util.formatDate(y);
		String d2 = Util.formatDate(y2);
		System.out.println("Zeit aus " + x + " ist " + d1);
		System.out.println("Zeit aus " + x2 + " ist " + d2);
		assertEquals("2018-01-28", d1);
		assertEquals("1987-12-30", d2);
	}
	
	public void testFloatString () {
		float test = 17.834f;
		log.info("Utiltest: " + Util.toString(test));
	}
	
	public void testGetHeute () {
		GregorianCalendar heute = Util.getHeute();
		assertNotNull(heute);
		log.info("Heute: " + Util.formatDate(heute));
		System.out.println("Heute: " + Util.formatDate(heute));
	}
	
	public void testGetLetzterHandelstag() {
		Util.getLetzterHandelstag();
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
	
	public void testDatumFormat() {
		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		assertTrue(Util.formatDate(beginn).equalsIgnoreCase("2017-12-02"));
		GregorianCalendar ende = new GregorianCalendar(2018,0,2);
		assertTrue(Util.formatDate(ende).equalsIgnoreCase("2018-01-02"));
		log.info("Beginn: " + Util.formatDate(beginn) + " Ende: "+ Util.formatDate(ende));
	}
	
	public void testDatumFormatTrenner() {
		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		String test1 = Util.formatDate(beginn, ".");
		assertTrue(test1.equalsIgnoreCase("2017.12.02"));
		GregorianCalendar ende = new GregorianCalendar(2018,0,2);
		String test2 = Util.formatDate(ende, ":");
		assertTrue(test2.equalsIgnoreCase("2018:01:02"));
		log.info("Beginn: " + test1 + " Ende: "+ test2);
		
	}
	
	public void testDatumFormatTrennerJahr() {
		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		String test1 = Util.formatDate(beginn, ".", true);
		assertTrue(test1.equalsIgnoreCase("2017.12.02"));
		String test2 = Util.formatDate(beginn, ".", false);
		assertTrue(test2.equalsIgnoreCase("02.12.2017"));
		
	}
	
	public void testParseDatumJJJJ_MM_TT () {
		String testDatum = "2017-12-02";
		GregorianCalendar datum = Util.parseDatum(testDatum);
		assertNotNull(datum);
	}
	
	public void testParseDatumTT_MM_JJJJ () {
		String testDatum = "04.01.2010";
		GregorianCalendar datum = Util.parseDatum(testDatum);
		assertNotNull(datum);
		String test = Util.formatDate(datum, "-", true);
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
		assertEquals(31, Util.anzahlTage(beginn, ende));
		assertEquals(30, Util.anzahlTage(beginn, datum1));
		assertEquals(1, Util.anzahlTage(beginn, datum2));
		assertEquals(29, Util.anzahlTage(beginn, datum3));
	}
	
	public void testAddTage() {
		GregorianCalendar testDatum;
		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		testDatum = Util.addTage(beginn, 1);
		assertEquals(Util.formatDate(testDatum), "2017-12-03");
		testDatum = Util.addTage(beginn, 10);
		assertEquals(Util.formatDate(testDatum), "2017-12-12");
		testDatum = Util.addTage(beginn, 30);
		assertEquals(Util.formatDate(testDatum), "2018-01-01");
		testDatum = Util.addTage(beginn, 60);
		assertEquals(Util.formatDate(testDatum), "2018-01-31");
		testDatum = Util.addTage(beginn, 365);
		assertEquals(Util.formatDate(testDatum), "2018-12-02");
		
	}
	
	public void testIstInZeitraum () {
		GregorianCalendar beginn = new GregorianCalendar(2017,11,2);
		GregorianCalendar ende = new GregorianCalendar(2018,0,2);
		GregorianCalendar datum1 = new GregorianCalendar(2018,0,1);
		GregorianCalendar datum2 = new GregorianCalendar(2017,11,3);
		GregorianCalendar datum3 = new GregorianCalendar(2017,11,31);
		GregorianCalendar datum4 = new GregorianCalendar(2016,11,31);
		GregorianCalendar datum5 = new GregorianCalendar(2019,11,31);
		assertTrue(Util.istInZeitraum(datum1, beginn, ende));
		assertTrue(Util.istInZeitraum(datum2, beginn, ende));
		assertTrue(Util.istInZeitraum(datum3, beginn, ende));
		assertTrue(Util.istInZeitraum(beginn, beginn, ende));
		assertTrue(Util.istInZeitraum(ende, beginn, ende));
		assertFalse(Util.istInZeitraum(datum4, beginn, ende));
		assertFalse(Util.istInZeitraum(datum5, beginn, ende));
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
		GregorianCalendar test = Util.toGregorianCalendar(date);
		assertEquals("1970-01-12", Util.formatDate(test));
				
	}
	

}
