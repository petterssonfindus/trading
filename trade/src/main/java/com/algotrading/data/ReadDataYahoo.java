package com.algotrading.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.component.AktieVerwaltung;
import com.algotrading.util.DateUtil;

@Service
public class ReadDataYahoo {
	private static final Logger log = LogManager.getLogger(ReadDataYahoo.class);

	@Autowired
	private AktieVerwaltung aV;

	private static void startWikipedia() {
		URL wikipedia = null;
		//			wikipedia = new URL("https://de.wikipedia.org/w/api.php?action=query&list=search&srsearch=Nelson%20Mandela&utf8=&format=json");
		//			wikipedia = new URL("https://de.wikipedia.org/w/api.php?action=query&list=search&srsearch=Nelson%20Mandela&utf8=&format=json");
		wikipedia = getWikipediaURL();

		URLConnection con = null;
		try {
			con = wikipedia.openConnection();
			System.out.println(con.getURL().toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader in = null;
		try {
			InputStreamReader iSR = new InputStreamReader(con.getInputStream());
			in = new BufferedReader(iSR);
			String inputLine;

			while ((inputLine = in.readLine()) != null)
				System.out.println(inputLine.length());
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(in);
	}

	/**
	 * Liest für alle Aktien mit Quelle=Yahoo aktuelle Kurse ein. 
	 */
	public void YahooWSAktienController() {
		List<Aktie> alleAktien = aV.getAktienListe();
		for (Aktie aktie : alleAktien) {
			if (aktie.getQuelle() == 1) {
				YahooWSController(aktie.getName());
			}
		}
	}

	/**
	 * Steuert das Einlesen der Kurse aus dem Yahoo-Finance-WebService mit bestehender Aktie
	 * Ermittelt die benötigte Zeitspanne 
	 * @param name
	 */
	public void YahooWSController(String name) {

		// die Aktie 
		Aktie aktie = aV.getAktieLazy(name);

		GregorianCalendar nextKurs = aktie.nextKurs();
		// wenn es noch keine Kurse gibt, muss das Datum manuell bestimmt werden. 
		if (nextKurs == null) {
			nextKurs = new GregorianCalendar(1980, 1, 1);
		}
		GregorianCalendar endeEinlesen = DateUtil.getLetzterHandelstag();
		int diff = DateUtil.anzahlKalenderTage(nextKurs, endeEinlesen);
		System.out.println(
				name + ": Anfrage von: " + DateUtil.formatDate(nextKurs) + " bis: " + DateUtil
						.formatDate(endeEinlesen) + " Diff " + diff);
		// wenn der letzte Kurs vor dem letzten Handelstag liegt
		if (diff >= 1) {
			// die Kurse werden geholt und in ein String-Array gesteckt.
			ArrayList<String> stringKurse = readYahooWS(name, nextKurs, endeEinlesen);
			// dann wird aus dem String-Array ein ImportKursreihe gemacht. 
			ImportKursreihe importKursreihe = transformYahooWSToKursreihe(stringKurse, name);
			// dann wird die ImportKursreihe in die Kurs-DB geschrieben
			DBManager.schreibeKurse(importKursreihe);
		}
	}

	private static ImportKursreihe transformYahooWSToKursreihe(ArrayList<String> response, String name) {
		ImportKursreihe kursreihe = new ImportKursreihe(name);
		ArrayList<Kurs> kurse = kursreihe.kurse;
		String line = "";
		for (int i = 1; i < response.size(); i++) {
			line = response.get(i);
			String splitBy = ",";
			String[] zeile = line.split(splitBy);
			Kurs kurs = new Kurs();
			kurs.setDatum(DateUtil.parseDatum(zeile[0]));
			try {
				kurs.open = Float.parseFloat(zeile[1]);
				kurs.high = Float.parseFloat(zeile[2]);
				kurs.low = Float.parseFloat(zeile[3]);
				kurs.close = Float.parseFloat(zeile[4]);
				// nicht immer sind die Spalten AdjClose und Volume vorhanden 
				if (zeile.length > 5) {
					kurs.adjClose = Float.parseFloat(zeile[5]);
					// Indizes haben als Volume Gleitkommazahlen, deshalb wird gecasted 
					kurs.volume = (int) Float.parseFloat(zeile[6]);
				}
				kurse.add(kurs);
				// wenn beim Parsen etwas schief geht, wird der Kurs nicht eingetragen. 
			} catch (NumberFormatException e1) {
				log.error(
						"FormatException: " + zeile[0] + " - " + zeile[1] + " - " + zeile[2] + " - " + zeile[3] + " - " + zeile[4] + " - " + zeile[5] + " - " + zeile[6]);
			}
		}
		return kursreihe;
	}

	/**
	 * Liest von der Yahoo-Finance-Seite automatisiert Kurse ein zum gewünschten Zeitraum 
	 * @param name
	 * @param beginn
	 * @param ende
	 * @return
	 */
	public static ArrayList<String> readYahooWS(String name, GregorianCalendar beginn, GregorianCalendar ende) {

		URL yahoo = null;
		ArrayList<String> result = null;

		if (ende.before(beginn)) {
			System.out.println("Ende " + DateUtil.formatDate(ende) + " liegt vor Beginn" + DateUtil.formatDate(beginn));
		}
		/*
					try {
						yahoo = new URL("https://query1.finance.yahoo.com/v7/finance/download/AMZN?period1=567817200&period2=1517094000&interval=1d&events=history&crumb=J0UqI6PCmkS");
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		*/
		try {
			yahoo = new URL(getYahooURL(name, beginn, ende));
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("URL: " + yahoo.toString());

		URLConnection con = null;
		try {
			con = yahoo.openConnection();
			con.setRequestProperty("Cookie", "B=18ol5dle115i9&b=3&s=gi");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		BufferedReader in = null;
		try {
			InputStreamReader iSR = new InputStreamReader(con.getInputStream());
			in = new BufferedReader(iSR);
			String inputLine;
			result = new ArrayList<String>();

			while ((inputLine = in.readLine()) != null) {
				result.add(inputLine);
				System.out.println(inputLine);
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private static void TestApacheHTTPClient() {
		HttpGet httpget = new HttpGet(
				"http://www.google.com/search?hl=en&q=httpclient&btnG=Google+Search&aq=f&oq=");

		URI uri = null;
		try {
			uri = new URIBuilder()
					.setScheme("http")
					.setHost("www.google.com")
					.setPath("/search")
					.setParameter("q", "httpclient")
					.setParameter("btnG", "Google Search")
					.setParameter("aq", "f")
					.setParameter("oq", "")
					.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpGet httpget2 = new HttpGet(uri);
		System.out.println(httpget.getURI());
	}

	/**
	 * Baut die URL zusammen mit fachlichen Parametern 
	 * @param name Kürzel des Wertes mit Yahoo-Abkürzung 
	 * @param period1 Beginn-Tag als GregorianCalendar 
	 * @param period2 Ende-Tag als GregorianCalendar
	 * @return
	 */
	private static String getYahooURL(String name, GregorianCalendar beginn, GregorianCalendar ende) {
		//		https://query1.finance.yahoo.com/v7/finance/download/AMZN?period1=567817200&period2=1517094000&interval=1d&events=history&crumb=J0UqI6PCmkS
		URI uri = null;
		String period1 = Long.toString(DateUtil.toTimeInUnixSeconds(beginn));
		String period2 = Long.toString(DateUtil.toTimeInUnixSeconds(ende));
		try {
			uri = new URIBuilder()
					.setScheme("https")
					.setHost("query1.finance.yahoo.com")
					.setPath("/v7/finance/download/" + name)
					.setParameter("period1", period1)
					.setParameter("period2", period2)
					.setParameter("interval", "1d")
					.setParameter("events", "history")
					.setParameter("crumb", "J0UqI6PCmkS")
					.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return uri.toString();

	}

	private static URL getWikipediaURL() {
		//		wikipedia = new URL("https://de.wikipedia.org/w/api.php?action=query&list=search&srsearch=Nelson%20Mandela&utf8=&format=json");
		URI uri = null;
		URL url = null;
		try {
			uri = new URIBuilder()
					.setScheme("https")
					.setHost("www.wikipedia.org")
					.setPath("/w/api.php")
					.setParameter("action", "query")
					.setParameter("list", "search")
					.setParameter("srsearch", "Nelson Mandela")
					.setParameter("format", "json")
					.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			url = uri.toURL();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
	}

}
