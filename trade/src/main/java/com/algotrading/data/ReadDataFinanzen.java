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
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Aktien;
import com.algotrading.aktie.Kurs;
import com.algotrading.component.AktieVerwaltung;
import com.algotrading.util.DateUtil;
import com.algotrading.util.FileUtil;
import com.algotrading.util.Util;

@Service
public class ReadDataFinanzen {

	private static final Logger log = LogManager.getLogger(ReadDataFinanzen.class);

	@Autowired
	private AktieVerwaltung aV;

	/**
	 * Liest für alle Aktien mit Quelle=Finanzen aktuelle Kurse ein. 
	 * Schreibt die Ergebnisse in das Log-Verzeichnis und in die Kurse-DB
	 * Jede Aktie muss Kurse haben. Neue Aktie muss manuell mit Kurse versorgt werden. 
	 */
	public void FinanzenWSAktienController(Aktien aktien) {
		for (Aktie aktie : aktien) {
			// wenn die Quelle 2 ist, dann Kurs über Finanzen aktualisieren 
			if (aktie.getQuelle() == 2) {
				FinanzenWSController(aktie, true, true, null);
			}
		}
	}

	/**
	 * Steuert das Einlesen der Kurse aus dem Finanzen-WebService mit bestehender Aktie
	 * Ermittelt die benötigte Zeitspanne 
	 * @param name
	 * @param writeDB: true schreibt in die DB, 
	 * 					false: schreibt in eine Datei kann dann mit ReadFileWriteDB in die DB geschrieben werden 
	 * @param writeFile: true schreibt ein File mit dem Inhalt des Service-Response
	 * @param beginn: der Tag, ab dem das Einlesen beginnt. Wenn null, dann ab letztem vorhandenem Kurs 
	 */
	public String FinanzenWSController(Aktie aktie, boolean writeFile, boolean writeDB,
			GregorianCalendar beginn) {
		String result = null;
		GregorianCalendar letzterKurs;
		GregorianCalendar beginnEinlesen;	// der Beginn des einzulesenden Kurs-Zeitraums
		// wenn ein Beginn-Datum vorgegeben ist, wird dieses verwendet
		if (beginn != null) {
			beginnEinlesen = beginn;
		} else {	// das Beginn-Datum wird berechnet
			letzterKurs = aV.getDatumLetzterKurs(aktie.getId());
			beginnEinlesen = DateUtil.addTage(letzterKurs, 1);
		}

		GregorianCalendar endeEinlesen = DateUtil.getLetzterHandelstag();

		int diff = DateUtil.anzahlKalenderTage(beginnEinlesen, endeEinlesen);
		System.out.println(
				aktie.getName() + ": Anfrage von: " + DateUtil.formatDate(beginnEinlesen) + " bis: " + DateUtil
						.formatDate(endeEinlesen) + " Diff " + diff);
		// wenn eine Aktualisierung notwendig ist 
		// der letzte Kurs liegt vor dem letzten Handelstag 
		if (diff >= 3) {

			// Aufruf des Finanzen-Service und in ein String-Array stecken.
			List<String> stringKurse = readFinanzenWS(aktie.getName(), beginnEinlesen, endeEinlesen);
			// in ein txt-File schreiben
			if (writeFile) {
				// schreibt das Ergebnis in eine csv-Datei
				File file = FileUtil.writeCSVFile(stringKurse, aktie.getName() + "%" + endeEinlesen.getTimeInMillis());
				result = file.getName();
			}
			// in die DB schreiben
			if (writeDB) {
				// aus dem String-Array ein ImportKursreihe transformieren 
				ImportKursreihe importKursreihe = transformFinanzenWSToKursreihe(stringKurse, aktie.getName());
				// ImportKursreihe in die Kurs-DB schreiben
				DBManager.schreibeKurse(importKursreihe);
			}
		} else {
			System.out.println(
					"Kein Einlesen notwendig für " + aktie.getName() + " Erster fehlender Kurs: " + DateUtil
							.formatDate(beginnEinlesen));
		}
		return result;
	}

	/**
	 * Liest eine gespeicherte Datei und schreibt die Daten in die DB
	 * Dateiname enthält die Extension z.B. '.txt'
	 */
	public void readFileWriteDB(String dateiName, String aktieName) {
		// den Inhalt der Datei auslesen
		List<String> kurse = FileUtil.readContent(dateiName);
		// aus dem String-Array ein ImportKursreihe machen 
		ImportKursreihe importKursreihe = transformFinanzenWSToKursreihe(kurse, aktieName);
		// Aktie holen 
		Aktie aktie = aV.getAktieLazy(aktieName);
		aktie.setKurse(importKursreihe.getKurse());
		// die Kursreihe in die DB schreiben
		aV.saveAktie(aktie);
		//		DBManager.schreibeKurse(importKursreihe);
	}

	/**
	 * Ermittelt den Aktien-Namen aus dem Dateinamen anhand des %-Zeichens
	 */
	public static void readFileWriteDB(String dateiname) {

	}

	private ImportKursreihe transformFinanzenWSToKursreihe(List<String> response, String name) {
		ImportKursreihe kursreihe = new ImportKursreihe(name);
		String line = "";
		int zaehler = 1;
		line = response.get(zaehler);

		// warte so lange, bis 'Tagestief' in der Zeile auftaucht. Das müsste Zeile 16 sein. 
		while (!line.contains("Tagestief")) {
			zaehler++;
			line = response.get(zaehler);
		}
		zaehler++; //  </tr>
		zaehler++; //  <tr>   - darauf wird getestet
		String test = null;
		// bis in einer Zeile 'table' erscheint 
		// enthält entweder <tr> oder </table>
		while (!(response.get(zaehler).indexOf("table") > 0)) {
			test = response.get(zaehler);
			// jetzt folgen Sequenzen mit jeweils 7 Zeilen 
			// Zeile 1 enthält tr
			zaehler++;
			// zeile 2 enthält das Datum
			test = response.get(zaehler);
			String datum = holeWertZwischenKlammern(test);
			System.out.println("eingelesen: " + datum);
			zaehler++;
			test = response.get(zaehler);
			String schluss = holeWertZwischenKlammern(response.get(zaehler));
			zaehler++;
			test = response.get(zaehler);
			String eroeffnung = holeWertZwischenKlammern(response.get(zaehler));
			zaehler++;
			String hoch = holeWertZwischenKlammern(response.get(zaehler));
			zaehler++;
			String tief = holeWertZwischenKlammern(response.get(zaehler));
			zaehler++; // nächste Zeile </tr>
			zaehler++; // nächste Zeile <tr> oder </table> 
			Kurs kurs = new Kurs();
			kurs.setDatum(DateUtil.parseDatum(datum));
			try {
				kurs.open = Util.parseFloat(eroeffnung);
				kurs.high = Util.parseFloat(hoch);
				kurs.low = Util.parseFloat(tief);
				kurs.close = Util.parseFloat(schluss);

				kursreihe.kurse.add(kurs);
				// wenn beim Parsen etwas schief geht, wird der Kurs nicht eingetragen. 
			} catch (NumberFormatException e1) {
				log.error(
						"FormatException: " + datum + " - " + schluss + " - " + eroeffnung + " - " + hoch + " - " + tief);
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
	private static String holeWertZwischenKlammern(String input) {
		// erstes Auftreten des '>' - Zeichens 
		int stelle1 = input.indexOf('>');
		// zweites Auftreten des '<' - Zeichens
		int stelle2 = input.indexOf('<', stelle1);
		// lese den Wert dazwischen 
		String datum = "";
		try {
			datum = input.substring(stelle1 + 1, stelle2);
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
			if (beginn == null) {
				finanzenURL = new URL(getFinanzenURL(name));
			} else {
				finanzenURL = new URL(getFinanzenURL(name, beginn, ende));
			}
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
			con.setRequestProperty("__ath", "7Cumv1jFnj7W8ZujjGvwAsRy9A1dQMAZmDbXp8xFSMk=");
			con.setRequestProperty("__atts", "2019-04-06-13-39-00");
			con.setRequestProperty("__atcrv", "669908818");
			con.setRequestProperty("Host", "www.finanzen.net");
			con.setRequestProperty(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:60.0) Gecko/20100101 Firefox/60.0");
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

		} catch (IOException e) {
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
	private static String getFinanzenURL(String name, GregorianCalendar beginn, GregorianCalendar ende) {
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

	private static String getFinanzenURL(String name) {
		//  https://www.finanzen.net/historische-kurse/Fresenius
		URI uri = null;
		try {
			uri = new URIBuilder()
					.setScheme("https")
					.setHost("www.finanzen.net")
					.setPath("/historische-kurse/" + name)
					.build();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return uri.toString();

	}

}
