/*
 * Created on 09.10.2006
 */
package com.algotrading.data;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.component.AktieVerwaltung;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Zeitraum;

/**
 * @author oskar <br>
 *         über diese Klasse läuft sämtlich Kommunikation mit der Datenbank.<br>
 */

@Component
public class DBManager {
	private static final Logger log = LogManager.getLogger(DBManager.class);

	private AktieVerwaltung aV;

	private static final String DBName = "kurse";
	private static final String StammdatenTabelle = "stammdaten";

	/**
	 * schreibt in eine bestehende Aktie neue Kurse in die DB
	 * TODO wenn bereits Kurs vorhanden sind, werden Daten ergänzt
	 * @param kursreihe
	 * @return
	 */
	public static boolean schreibeKurse(ImportKursreihe kursreihe) {
		String name = kursreihe.kuerzel;
		int zaehler = 0;

		Connection connection = ConnectionFactory.getConnection();
		// iteriert über alle vorhandenen Kurse 
		for (Kurs kurs : kursreihe.kurse) {
			// schreibt den Kurs in die Tabelle 
			// wenn ein Fehler entsteht z.B. duplicate Entry, wird gezählt. 
			if (!DBManager.addKurs(kurs, connection))
				zaehler++;
		}
		log.info("Anzahl " + kursreihe.kurse.size() + " Kurse für " + kursreihe.kuerzel + " Fehler: " + zaehler);
		return true;
	}

	/**
	 * Trägt neue Zeile in Stammdaten ein. 
	 * Schreibt neue Kurs-Tabelle mit dem Kürzel der Aktie
	 * @param aktie
	 */
	public static void neueAktie(Aktie aktie) {
		// trägt die Aktie in die Stammdaten ein. 
		trageNeueAktieInStammdatenEin(aktie);

		schreibeNeueAktieTabelle(aktie.name);

	}

	/**
	 * Schreibt die Stammdaten einer neuen Aktie in die Stammdaten-Tabelle
	 */
	static boolean trageNeueAktieInStammdatenEin(Aktie aktie) {

		String name = aktie.name;
		String firmenname = aktie.firmenname;
		String indexname = aktie.indexname;
		Zeitraum zeitraum = aktie.getZeitraumKurse();
		String quelle = Integer.toString(aktie.getQuelle());
		String beginn;
		String ende;
		String insert;
		insert = "INSERT INTO `" + DBName + "`.`" + StammdatenTabelle + "` (`name`, `firmenname`, `indexname`, `quelle`) VALUES ('" + name + "', '" + firmenname + "', '" + indexname + "', '" + quelle + "');";

		Connection connection = ConnectionFactory.getConnection();
		Statement anweisung = null;
		try {
			anweisung = (Statement) connection.createStatement();
			anweisung.execute(insert);
		} catch (SQLException e) {
			log.error("Fehler beim INSERT Stammdaten von Aktie: " + name);
			log.error(insert);
			return false;
		}
		log.info("Aktie " + name + " in Stammdaten eingetragen");
		return true;

	}

	/**
	 * Legt eine neue Tabelle an mit der Standard-Datenstruktur
	 * #TODO Indikatoren werden nicht genutzt - muss entfernt werden. Indikatoren werden bei Bedarf berechnet. 
	 * @param name der Name der Tabelle in der Datenbank 
	 * @return true, wenn die Tabelle vorhanden ist
	 */
	public static boolean schreibeNeueAktieTabelle(String name) {

		//		String create = "CREATE TABLE IF NOT EXISTS `" + name + "` (" + 
		String create = "CREATE TABLE `" + name + "` (" + "`datum` date NOT NULL," + "`open` float DEFAULT NULL," + "`high` float DEFAULT NULL," + "`low` float DEFAULT NULL," + "`close` float NOT NULL," + "`volume` int(11) DEFAULT NULL," + "`ISIN` varchar(12) DEFAULT NULL," + "PRIMARY KEY (`datum`)," + "UNIQUE KEY `datum` (`datum`)" + ") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		log.info("CreateTable-Statement: " + create);
		Statement anweisung = null;

		Connection connection = ConnectionFactory.getConnection();
		try {
			anweisung = (Statement) connection.createStatement();
			anweisung.execute(create);
		} catch (SQLException e) {
			log.error("Fehler beim CREATE TABLE von Aktie: " + name);
			return false;
		}
		log.info("Tabelle " + name + " in DB angelegt ");
		return true;

	}

	/**
	 * fügt einen neuen Kurs in eine bestehende Tabelle
	 * Im Kurs sind nur Datum und die Kursreihe relevant 
	 * Sortierung spielt keine Rolle.
	 * # TODO Fehlerbehandlung, wenn Kurs bereits vorhanden. 
	 */
	public static boolean addKurs(Kurs kurs, Connection connection) {
		String name = kurs.getWertpapier();
		String datum = addApostroph(DateUtil.formatSQLDate(kurs.datum), false);
		String close = addApostroph(kurs.getClose(), true);
		String open = addApostroph(Float.toString(kurs.open), true);
		String high = addApostroph(Float.toString(kurs.high), true);
		String low = addApostroph(Float.toString(kurs.low), true);
		String volume = addApostroph(Integer.toString(kurs.volume), true);

		String insert = "INSERT INTO `" + name + "` (`datum`, `open`, `high`, `low`, `close`, `volume`) " + "VALUES (" + datum + open + high + low + close + volume + ")";

		//		log.info("InsertStatement: " + insert);
		if (connection == null) {
			connection = ConnectionFactory.getConnection();
		}
		Statement anweisung = null;

		try {
			anweisung = (Statement) connection.createStatement();
			anweisung.execute(insert);
		} catch (SQLException e) {
			if (e.getSQLState().matches("23000")) {
				return true;
			} else {
				log.error("Fehler beim Schreiben von Kurs " + kurs.getWertpapier() + kurs.toString());
				log.error(insert);
				return false;
			}
		}
		//		log.info("Kurs " + kurs + " in DB geschrieben ");
		return true;
	}

	/**
	 * 
	 * @param cal
	 * @return
	 */
	public static Kurs getKurs(String name, GregorianCalendar cal) {
		// SELECT * FROM `appl` WHERE `datum` = '2018-01-02' 
		String select = "SELECT * FROM `" + name + "` WHERE `datum` = '2018-01-02'";

		Connection verbindung = ConnectionFactory.getConnection();
		Statement anweisung = null;
		ResultSet response = null;
		try {
			anweisung = (Statement) verbindung.createStatement();
			response = (ResultSet) anweisung.executeQuery(select);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		Kurs kurs = createKursAusDBSelect(response);
		kurs.setWertpapier(name);
		return kurs;

	}

	/**
	 * Prüft eine Kursreihe nach Inkonsistenzen - fehlerhafte Kurse
	 * Bei Fehler werden logs geschrieben
	 * @param name
	 * @param schwelle der Prozentwert der erlaubten Abweichung z.B. 0.1 
	 */
	protected static void checkKursreihe(String name, float schwelle) {
		ArrayList<Kurs> kurse = getKursreihe(name);
		int zaehler = 0;
		Kurs vortageskurs = null;
		for (Kurs kurs : kurse) {
			if (zaehler > 2) {
				if ((kurs.getKurs() * (1 + schwelle)) < vortageskurs
						.getKurs() || (kurs.getKurs() * (1 - schwelle)) > vortageskurs.getKurs()) {
					log.error(
							"Kurs " + name + " " + DateUtil.formatDate(kurs.datum) + " - " + kurs
									.getKurs() + " - " + vortageskurs.getKurs());
				}
			}
			vortageskurs = kurs;
			zaehler++;
		}
	}

	/**
	 * Prüft, ob alle Kurse vorhanden sind 
	 * Referenz-Kursreihe ist xxxdja
	 * @param name
	 */
	public static void checkKursreiheTage(Aktie aktie, Aktie dow) {
		ArrayList<Kurs> aktieKurse = aktie.getKursListe();
		ArrayList<Kurs> dowKurse = dow.getKursListe();
		// Kurs zum Beginn der Zeitreihe
		Kurs kurs1 = aktieKurse.get(0);
		Kurs dow1 = dowKurse.get(0);
		Kurs kurs2 = null;
		Kurs dow2;
		int abstand = 0;
		// AktienKurs ist jänger 
		if (kurs1.datum.getTimeInMillis() > dow1.datum.getTimeInMillis()) {
			// sucht den Dow-Kurs zum 1. Aktienkurs
			dow2 = dow.getKurs(kurs1.datum);
			// Abstand ist die Anzahl Kurse, die der DOW älter ist 
			abstand = dowKurse.indexOf(dow2);
			int i = 1; // i iteriert über die Kurse der Aktie
			do {
				if (i >= aktieKurse.size()) {
					System.out.println(
							"Kurs fehlt: " + aktie.getName() + " " + i + " last: " + DateUtil.formatDate(kurs2.datum));
				}
				kurs2 = aktieKurse.get(i);
				dow2 = dowKurse.get(i + abstand);

				if (!DateUtil.istGleicherKalendertag(kurs2.datum, dow2.datum)) {
					System.out.println(
							"fehlender Kurs " + aktie.name + " " + DateUtil.formatDate(dow2.datum) + " " + DateUtil
									.formatDate(kurs2.datum));
					return;
				}
				i++;
			} while (i < aktieKurse.size() - 10);
			System.out.println(
					"Aktienkurse geprüft: " + aktie.getName() + " von " + DateUtil
							.formatDate(kurs1.datum) + " bis " + DateUtil
									.formatDate(kurs2.datum));
		} else {	// Aktienkurs ist älter als 1. DowJones-Kurs
			// sucht den Aktien-Kurs zum 1. Dowkurs
			kurs2 = aktie.getKurs(dow1.datum);
			// Abstand ist die Anzahl Kurse, die der DOW älter ist 
			abstand = aktieKurse.indexOf(kurs2);
			int i = 1; // i iteriert über die Kurse der Aktie
			do {
				if (i >= dowKurse.size()) {
					System.out.println(
							"Kurs fehlt: " + aktie.getName() + " " + i + " last: " + DateUtil.formatDate(kurs2.datum));
				}
				kurs2 = aktieKurse.get(i + abstand);
				dow2 = dowKurse.get(i);

				if (!DateUtil.istGleicherKalendertag(kurs2.datum, dow2.datum)) {
					System.out.println(
							"fehlender Kurs " + aktie.name + " " + DateUtil.formatDate(dow2.datum) + " " + DateUtil
									.formatDate(kurs2.datum));
					return;
				}
				i++;
			} while (i < dowKurse.size() - 10);
			System.out.println(
					"Aktienkurse geprüft: " + aktie.getName() + " von " + DateUtil
							.formatDate(kurs1.datum) + " bis " + DateUtil
									.formatDate(kurs2.datum));

		}
		// 

		if (dow1 == null) {	// Kurs im Dow-Jones-Kursreihe nicht gefunden. 
			log.error("Referenzkurs im xxxdja nicht gefunden: " + DateUtil.formatDate(kurs1.datum));
		} else {
			// geht durch alle Kurse der zu präfenden Aktie 
			for (Kurs kurs : aktie.getKurse()) {
				// in beiden Kursreihen wird ein Tag weiter gespult und der Tag wird verglichen

			}
		}

	}

	/**
	 * Ermittelt den Zeitraum, in dem Kurse einer Aktie vorhanden sind 
	 * @return
	 */
	public static Zeitraum getZeitraumVorhandeneKurse(Aktie aktie) {
		ArrayList<Kurs> kurse = DBManager.getKursreihe(aktie.name);
		GregorianCalendar beginn = kurse.get(0).datum;
		GregorianCalendar ende = kurse.get(kurse.size() - 1).datum;
		return new Zeitraum(beginn, ende);
	}

	public static GregorianCalendar getLastKurs(Aktie aktie) {
		String select = "SELECT MAX(Datum) FROM `" + aktie.name + "`";
		Connection verbindung = ConnectionFactory.getConnection();
		Statement anweisung = null;
		ResultSet response = null;
		String responseString = null;
		try {
			anweisung = (Statement) verbindung.createStatement();
			response = (ResultSet) anweisung.executeQuery(select);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}

		try {
			boolean theNext = response.next();
			responseString = response.getString("MAX(Datum)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		GregorianCalendar result = DateUtil.parseDatum(responseString);
		return result;
	}

	/**
	 * Befüllt ein Verzeichnis mit allen Aktien-Stammdaten 
	 * @return
	 */
	public static HashMap<String, Aktie> getVerzeichnis() {
		HashMap<String, Aktie> result;
		String select = "SELECT * FROM `" + StammdatenTabelle;

		Connection verbindung = ConnectionFactory.getConnection();
		Statement anweisung = null;
		ResultSet response = null;
		try {
			anweisung = (Statement) verbindung.createStatement();
			response = (ResultSet) anweisung.executeQuery(select);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		// die nackte Liste ohne Key
		List<Aktie> aktien = createVerzeichnisAusDBSelect(response);
		// das Verzeichnis mit Key
		result = new HashMap<String, Aktie>();
		// den Wertpapier-Namen als Key setzen 
		for (Aktie aktie : aktien) {
			result.put(aktie.name, aktie);
		}
		return result;
	}

	/**
	 * Erzeugt aus der DB-Antwort ein vollständiges Verzeichnis 
	 * Suchen werden auf diesem Verzeichnis ausgeführt. 
	 */
	private static List<Aktie> createVerzeichnisAusDBSelect(ResultSet response) {
		List<Aktie> aktien = new ArrayList<Aktie>();

		try {
			while (response.next()) {
				String name = (response.getString("name"));
				String firmenname = (response.getString("firmenname"));
				String indexname = (response.getString("indexname"));

				// eine Aktie wird erzeugt mit den Minimal-Angaben 
				Aktie aktie = new Aktie(name);
				aktie.setFirmenname(firmenname);
				aktie.setIndexname(indexname);
				aktie.setLand(response.getInt("land"));
				aktie.setWaehrung(response.getInt("waehrung"));
				aktie.setQuelle(response.getInt("quelle"));
				GregorianCalendar beginn = DateUtil.toGregorianCalendar(response.getDate("beginn"));
				GregorianCalendar ende = DateUtil.toGregorianCalendar(response.getDate("ende"));
				Zeitraum zeitraum = new Zeitraum(beginn, ende);
				aktie.setZeitraumKurse(zeitraum);
				aktien.add(aktie);

			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return aktien;
	}

	/**
	 * Liest alle vorhandenen Kursinformationen zu einem Wertpapier
	 * @param cal
	 * @return
	 */
	public static ArrayList<Kurs> getKursreihe(String name) {
		String select = "SELECT * FROM `" + name;
		ArrayList<Kurs> kursreihe = getKursreiheSELECT(select, name);
		return kursreihe;
	}

	/**
	 * Liest alle vorhandenen Kursinformationen zu einem Wertpapier
	 * ab einem Beginn-Datum 
	 * @return
	 */
	public static ArrayList<Kurs> getKursreihe(String name, GregorianCalendar beginn) {
		if (beginn == null)
			return null;  // #TODO Exception werfen 

		String select = "SELECT * FROM " + name + " WHERE `datum` >= '" + DateUtil.formatDate(beginn) + "'";
		// den DB-SELECT Ausführen und eine Kursreihe erzeugen mit enthaltenen Wertpapier-Namen
		ArrayList<Kurs> kursreihe = getKursreiheSELECT(select, name);
		return kursreihe;
	}

	/**
	 * erzeugt eine Liste von Tageskursreihen aus einem vorbereiteten SELECT-Statement
	 * @param select
	 * @return
	 */
	private static ArrayList<Kurs> getKursreiheSELECT(String select, String name) {
		Connection verbindung = ConnectionFactory.getConnection();
		Statement anweisung = null;
		ResultSet response = null;
		try {
			anweisung = (Statement) verbindung.createStatement();
			response = (ResultSet) anweisung.executeQuery(select);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		ArrayList<Kurs> kursreihe = createKursreiheAusDBSelect(response);
		// den Wertpapier-Namen in allen Kursen setzen
		for (Kurs kurs : kursreihe) {
			kurs.setWertpapier(name);
		}
		return kursreihe;
	}

	/**
	 * die Kursreihe werden mit Tageskursreihen befüllt 
	 * @param response
	 * @return
	 */
	private static ArrayList<Kurs> createKursreiheAusDBSelect(ResultSet response) {
		ArrayList<Kurs> kursreihe = new ArrayList<Kurs>();

		try {
			while (response.next()) {
				Kurs kurs = new Kurs();
				kurs.setKurswert(response.getFloat("close"));
				kurs.close = response.getFloat("close");
				kurs.high = response.getFloat("high");
				kurs.low = response.getFloat("low");
				kurs.open = response.getFloat("open");
				kurs.volume = response.getInt("volume");
				kurs.setDatum(DateUtil.toGregorianCalendar(response.getDate("datum")));
				kursreihe.add(kurs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return kursreihe;
	}

	/**
	 * ein einzelner Kurs wird erzeugt aus einem DB-SELECT
	 * @param response
	 * @return
	 */
	private static Kurs createKursAusDBSelect(ResultSet response) {
		Kurs kurs = new Kurs();
		try {
			response.next();
			kurs.close = response.getFloat("close");
			kurs.setDatum(DateUtil.toGregorianCalendar(response.getDate("datum")));

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return kurs;
	}

	/**
	 * ergänzt einen Text um ein führendes Komma und einen SQL-Apostroph vorne
	 * und hinten aus Mueller wird , 'Mueller'
	 * 
	 * @param text
	 * @return
	 */
	private static String addApostroph(String text, boolean mitKomma) {
		String result;
		if (text != null) {
			result = " '" + text + "' ";
		} else {
			result = " NULL ";
		}
		if (mitKomma) {
			result = " , " + result;
		}
		return result;
	}

}