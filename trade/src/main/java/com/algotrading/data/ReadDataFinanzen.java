package com.algotrading.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.util.DateUtil;
import com.algotrading.util.FileUtil;
import com.algotrading.util.Util;


	public class ReadDataFinanzen {
		
	private static final Logger log = LogManager.getLogger(ReadDataFinanzen.class);

	/**
	 * Liest für alle Aktien mit Quelle=Finanzen aktuelle Kurse ein. 
	 * Schreibt die Ergebnisse in das Log-Verzeichnis 
	 */
	public static void FinanzenWSAktienController () {
		Aktien aktien = Aktien.getInstance(); 
		ArrayList<Aktie> alleAktien = aktien.getAllAktien();
		for (Aktie aktie : alleAktien) {
			if (aktie.getQuelle() == 2) {
				FinanzenWSController(aktie.name, false);
			}
		}
	}
	
	/**
	 * Steuert das Einlesen der Kurse aus dem Finanzen-WebService mit bestehender Aktie
	 * Ermittelt die benötigte Zeitspanne 
	 * @param name
	 * @param writeDB steuert, ob direkt in die DB geschrieben wird, oder eine Datei geschrieben wird. 
	 */
	public static String FinanzenWSController (String name, boolean writeDB) {
		String result = null; 
		// die Aktie 
		Aktie aktie = Aktien.getInstance().getAktie(name);
		// der letzte Kurs wird ermittelt
		GregorianCalendar letzterKurs = DBManager.getLastKurs(aktie);
		// bei einer ganz neuen Aktie gibt es keine Kurse
		
		GregorianCalendar letzterHandelstag = DateUtil.getLetzterHandelstag();
		// der nächste erwartete Kurs wird einfach 1 Tag hoch gezählt. Das stimmt nicht genau, spielt aber keine Rolle. 
		GregorianCalendar nextKurs = DateUtil.addTage(letzterKurs, 1);
		// wenn es noch keine Kurse gibt, muss das Datum manuell bestimmt werden. 
		if (nextKurs == null) {
			nextKurs = new GregorianCalendar(1998, 01, 01);
		}
		int diff = DateUtil.anzahlTage(nextKurs, letzterHandelstag);
		System.out.println(name + ": Anfrage von: " + DateUtil.formatDate(nextKurs) + " bis: " + DateUtil.formatDate(letzterHandelstag) + " Diff " + diff);
		// wenn der letzte Kurs vor dem letzten Handelstag liegt
		if (diff >= 1) {	
			// die Kurse werden geholt und in ein String-Array gesteckt.
			ArrayList<String> stringKurse = readFinanzenWS(name, nextKurs, letzterHandelstag);
			// entweder wird direkt in die DB geschrieben 
			if (writeDB) {
				// dann wird aus dem String-Array ein ImportKursreihe gemacht. 
				ImportKursreihe importKursreihe = transformFinanzenWSToKursreihe(stringKurse, name);
				// dann wird die ImportKursreihe in die Kurs-DB geschrieben
				DBManager.schreibeKurse(importKursreihe);
			}
			// oder es wird ein txt-File geschrieben 
			else {
				// schreibt das Ergebnis in eine csv-Datei
				File file = FileUtil.writeCSVFile(stringKurse, name + letzterHandelstag.getTimeInMillis());
				result = file.getName();
			}
		}
		else {
			System.out.println(name + " letzter Kurs: " + DateUtil.formatDate(letzterKurs));
		}
		return result; 
	}
	/**
	 * Liest eine gespeicherte Datei und schreibt die Daten in die DB
	 * Dateiname enthält die Extension z.B. '.txt'
	 */
	public static void readFileWriteDB (String dateiname, String kursname) {
		// den Inhalt der Datei auslesen
		ArrayList<String> kurse = FileUtil.readContent(dateiname);
		// aus dem String-Array ein ImportKursreihe machen 
		ImportKursreihe importKursreihe = transformFinanzenWSToKursreihe(kurse, kursname);
		// die Kursreihe in die DB schreiben
		DBManager.schreibeKurse(importKursreihe);
	}
	
	private static ImportKursreihe transformFinanzenWSToKursreihe (ArrayList<String> response, String name) {
		ImportKursreihe kursreihe = new ImportKursreihe(name);
		ArrayList<Kurs> kurse = kursreihe.kurse;
		String line = "";
		int zaehler = 1; 
		line = response.get(zaehler);
		
		// warte so lange, bis 'Tagestief' in der Zeile auftaucht. Das müsste Zeile 16 sein. 
		while (! line.contains("Tagestief")) {
			zaehler ++; 
			line = response.get(zaehler);
		}
		zaehler ++; //  </tr>
		zaehler ++; //  <tr>   - darauf wird getestet
		String test = null; 
		// bis in einer Zeile 'table' erscheint 
		// enthält entweder <tr> oder </table>
		while (! (response.get(zaehler).indexOf("table") > 0)) {
			test = response.get(zaehler);
			// jetzt folgen Sequenzen mit jeweils 7 Zeilen 
			// Zeile 1 enthält tr
			zaehler ++;
			// zeile 2 enthält das Datum
			test = response.get(zaehler);
			String datum = holeWertZwischenKlammern(test);
			System.out.println("eingelesen: " + datum);
			zaehler ++; 
			test = response.get(zaehler);
			String schluss = holeWertZwischenKlammern(response.get(zaehler));
			zaehler ++; 
			test = response.get(zaehler);
			String eroeffnung = holeWertZwischenKlammern(response.get(zaehler));
			zaehler ++; 
			String hoch = holeWertZwischenKlammern(response.get(zaehler));
			zaehler ++; 
			String tief = holeWertZwischenKlammern(response.get(zaehler));
			zaehler ++; // nächste Zeile </tr>
			zaehler ++; // nächste Zeile <tr> oder </table> 
	    	Kurs kurs = new Kurs();
	    	kurs.wertpapier = name;	// in jedem Kurs ist der Wertpapiername enthalten 
	    	kurs.datum = DateUtil.parseDatum(datum);
	    	try {
				kurs.open = Util.parseFloat(eroeffnung);
				kurs.high = Util.parseFloat(hoch);
				kurs.low = Util.parseFloat(tief);
				kurs.close = Util.parseFloat(schluss);
	
	        	kurse.add(kurs);
	        // wenn beim Parsen etwas schief geht, wird der Kurs nicht eingetragen. 
	    	} catch (NumberFormatException e1) {
	    		log.error("FormatException: " + datum + " - " + schluss + " - " + eroeffnung
	    				+ " - " + hoch + " - " + tief);
	    	}
    	}
		return kursreihe; 
	}
	/**
	 * Holt den Wert, der sich zwischen >< enthalten ist (entweder Datum oder Wert) 
	 * wenn leer, dann ein leerer String
	 *                 <td   >27.11.2018</td>
	 *                                 <td   >19,39</td>
	 */
	private static String holeWertZwischenKlammern (String input) {
		// erstes Auftreten des '>' - Zeichens 
		int stelle1 = input.indexOf('>');
		// zweites Auftreten des '<' - Zeichens
		int stelle2 = input.indexOf('<', stelle1);
		// lese den Wert dazwischen 
		String datum = "";
		try {
			datum = input.substring(stelle1+1, stelle2);
		} catch (Exception e) {
			System.out.print("Exception beim Parsen: " + input);
		}
		return datum; 
	}
	
	/**
	 * Liest von der Finanzen-Seite automatisiert Kurse ein zum gewünschten Zeitraum 
	 * @param name
	 * @param beginn
	 * @param ende
	 * @return
	 */
	public static ArrayList<String> readFinanzenWS(String name, GregorianCalendar beginn, GregorianCalendar ende) {

		URL finanzenURL = null;
		ArrayList<String> result = null; 
		OutputStreamWriter writer = null;
		
		if (ende.before(beginn)) {
			System.out.println("Ende " + DateUtil.formatDate(ende) + " liegt vor Beginn" + DateUtil.formatDate(beginn));
		}
		// die URL zusammen bauen 
		try {
			finanzenURL = new URL (getFinanzenURL(name, beginn, ende));
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		System.out.println("URL: " + finanzenURL.toString());

		HttpURLConnection con = null;
		// String body = "param1=" + URLEncoder.encode( "value1", "UTF-8" ) + "&" +
	    //          "param2=" + URLEncoder.encode( "value2", "UTF-8" );

		try {
			con = (HttpURLConnection) finanzenURL.openConnection();
			con.setRequestMethod("POST");
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("__ath", "jVe8cUoSQj9rDictciDnQj71/h1pd0fWMrbn4HFHc3A=");
			con.setRequestProperty("__atts", "2018-12-21-18-28-42");
			con.setRequestProperty("__atcrv", "318613846");
			con.setRequestProperty("Host", "www.finanzen.net");
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:60.0) Gecko/20100101 Firefox/60.0");
			con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			con.setRequestProperty("Accept-Language", "de,en-US;q=0.7,en;q=0.3");
			con.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
//			con.setRequestProperty("Content-Type", "gzip, deflate, br");
			con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			con.setRequestProperty("Content-Length", "0");
//			con.setRequestProperty("Content-Length", String.valueOf(body.length()));

			writer = new OutputStreamWriter(con.getOutputStream());
			writer.write("");
			writer.flush();

		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			BufferedReader in = null;
			// öffnet einen Stream aus der geöffneten Connection
			InputStream iS = con.getInputStream();
			// schaltet einen GZIP-Kompression ein. 
			GZIPInputStream gzipiS = new GZIPInputStream(iS);
			// gibt das Ganze an einen Reader
			InputStreamReader iSR = new InputStreamReader(gzipiS);
			// gibt das Ganze an einen BufferedReader, um zeilenweise zu lesen. 
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

	/**
	 * Baut die URL zusammen mit fachlichen Parametern 
	 * @param name Kürzel des Wertes mit Yahoo-Abkürzung 
	 * @param period1 Beginn-Tag als GregorianCalendar 
	 * @param period2 Ende-Tag als GregorianCalendar
	 * @return
	 */
	private static String getFinanzenURL (String name, GregorianCalendar beginn, GregorianCalendar ende) {
//  https://www.finanzen.net/Ajax/IndicesController_HistoricPriceList/VDAX_NEW/19.11.2014_19.12.2018/false
		URI uri = null;
		String von = DateUtil.formatDate(beginn, ".", false);
		String bis = DateUtil.formatDate(ende, ".", false);
		try {
			uri = new URIBuilder()
			        .setScheme("https")
			        .setHost("www.finanzen.net")
			        .setPath("/Ajax/IndicesController_HistoricPriceList/" + name + "/" + von + "_" + bis + "/false")
			        .build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return uri.toString();
		
	}
	
	
}
