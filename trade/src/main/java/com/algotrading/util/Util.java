package com.algotrading.util;

import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
	static final Logger log = LogManager.getLogger(Util.class);

	public static String separator = " ; ";

	/**
	 * macht aus einem GregorianCal-Datum ein String
	 * kann für Text-Ausgabe und SQL-Abfragen genutzt werden
	 * 
	 * @param date das Datum, das umgewandelt werden soll
	 * @return ein String oder der Wert 'NULL'
	 */
	public static String formatDate(GregorianCalendar cal) {
		if (cal == null)
			return "NULL";
		else {
			String dateString;
			// aus dem Caldendar ein Datum erzeugen
			java.sql.Date date = new java.sql.Date (cal.getTimeInMillis());
			// Datumsformat festlegen
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			// das Datum in dem Format als String ausgeben
			dateString = formatter.format(date);
			return dateString;
		}
	}
	
	public static String formatDate(GregorianCalendar cal, String trenner, boolean jahrVoraus) {
		if (cal == null || trenner == null)
			return "NULL";
		else {
			String dateString;
			// aus dem Caldendar ein Datum erzeugen
			java.sql.Date date = new java.sql.Date (cal.getTimeInMillis());
			// Datumsformat festlegen
			String vorlage = "";
			if (jahrVoraus) vorlage = "yyyy" + trenner + "MM" + trenner + "dd";
			else vorlage = "dd" + trenner + "MM" + trenner + "yyyy";
			DateFormat formatter = new SimpleDateFormat(vorlage);
			// das Datum in dem Format als String ausgeben
			dateString = formatter.format(date);
			return dateString;
		}
		
	}
	/**
	 * aus einem Datum einen String mit vorgegebenen Trennzeichen 
	 * @param cal
	 * @param trenner
	 * @return
	 */
	public static String formatDate(GregorianCalendar cal, String trenner) {
		String dateString = null; 
		if (cal == null || trenner == null)
			return "NULL";
		else {
			String minusString = formatDate(cal);
			dateString = minusString.replace("-", trenner);
		}
		return dateString;
	}

	/**
	 * Der letzte abgeschlossene Tag an dem gehandelt wurde 
	 * Entspricht den Arbeitstagen. 
	 * @return
	 */
	public static GregorianCalendar getLetzterHandelstag () {
		
		// den Wochentag des aktuellen Tages ermitteln. 
		// die Woche beginnt bei 1 = Sonntag endet bei 7 = Samstag
		GregorianCalendar heute = new GregorianCalendar(); 
		int test = heute.get(Calendar.DAY_OF_WEEK);
		int diff = 1; // normalerweise wird 1 Tag zurück gegangen
		if (test == 1) { // es ist ein Sonntag
			diff = 2; 	// zurück auf Freitag
		}
		else if (test == 2) { // es ist ein Montag
			diff = 3;	// zurück auf Freitag 
		}
		GregorianCalendar letzterArbeitstag = Util.addTage(heute, -1 * diff);
		
		return letzterArbeitstag;
	}
	
	public static GregorianCalendar getHeute () {
		return new GregorianCalendar();
	}
	/**
	 * prüft, ob es sich um den gleichen Kalendertag handelt
	 * @param tag1
	 * @param tag2
	 * @return
	 */
	public static boolean istGleicherKalendertag (GregorianCalendar tag1, GregorianCalendar tag2) {
		boolean result = false;
		// präft das Jahr
		if (tag1.get(Calendar.YEAR) == tag2.get(Calendar.YEAR)) {
			// präft den Tag des Jahres (von 1 bis 366) 
			if (tag1.get(Calendar.DAY_OF_YEAR) == tag2.get(Calendar.DAY_OF_YEAR)) {
				result = true; 
			}
		}
		return result; 
	}
	/**
	 * Formt eine Zeitangabe von Gregorian Calendar in Java-Millis um. 
	 */
	public static long toTimeInMillis (GregorianCalendar cal) {
		return cal.getTimeInMillis();
	}
	/**
	 * Formt eine Zeitangabe von Millis in ein Datum um 
	 * @param Zeit in Millis
	 * @return Zeit in Gregorian Calendar
	 */
	public static GregorianCalendar toGregorianCalendar (long millis) {
		GregorianCalendar result = new GregorianCalendar();
		result.setTimeInMillis(millis);
		return result; 
	}
	/**
	 * Eine Unix-Zeit in GregorianCalendar
	 */
	public static GregorianCalendar unixTimeToGregorianCalendar(long unixseconds) {
		return toGregorianCalendar(unixseconds * 1000);
	}
	
	/**
	 * 	Einen Gregorian Calendar in Unix-Zeit (Sekunden seit 1970) 
	 */
	public static long toTimeInUnixSeconds (GregorianCalendar cal) {
		return toTimeInMillis(cal) / 1000; 
	}
	
	/**
	 * Formt ein sql.Date in ein GregorianCalendar um 
	 * Wenn kein Datum vorhanden, dann null 
	 * @param date
	 * @return
	 */
	public static GregorianCalendar toGregorianCalendar (Date date) {
		if (date == null) return null; 
		
		GregorianCalendar result = new GregorianCalendar();
		result.setTime(date);
		return result; 
	}
	
	/**
	 * prüft, ob der Stichtag sich innerhalb oder gleich der Zeitraum befindet 
	 * @param stichtag der angefragte Stichtag
	 * @param beginn
	 * @param ende
	 * @return true, wenn Stichtag innerhalb der Zeitraum
	 */
	public static boolean istInZeitraum (GregorianCalendar stichtag, GregorianCalendar beginn, GregorianCalendar ende) {
		if (stichtag == null) log.error("Inputvariable stichtag ist null");
		if (beginn == null) log.error("Inputvariable beginn ist null");
		if (ende == null) log.error("Inputvariable ende ist null");

		boolean result = true; 
		if (stichtag.before(beginn) || stichtag.after(ende)) {
			result = false; 
		}
		return result; 
	}
	
	public static boolean istInZeitraum (GregorianCalendar stichtag, Zeitraum Zeitraum) {
		return istInZeitraum(stichtag, Zeitraum.beginn, Zeitraum.ende);
	}
	
	/**
	 * ermittelt die Anzahl Tage zwischen 2 Datümern 
	 * @param beginn
	 * @param ende
	 * @return
	 */
	public static int anzahlTage (GregorianCalendar beginn, GregorianCalendar ende) {
		long dauer = ende.getTimeInMillis() - beginn.getTimeInMillis(); 
		float test = (dauer / (1000 * 60 * 60 * 24));
		int result = (int) (dauer / (1000 * 60 * 60 * 24));
		return result; 
	}
	/**
	 * Berechnet die Rendite eines Kapitals das x Tage lang liegt
	 */
	public static float rechneRendite (float kapitalBeginn, float kapitalEnde, int tage) {
		if ((kapitalBeginn == 0) || (kapitalEnde == 0) || tage < 1) log.error("Fehler in rechneRendite() Inputparameter ");
		return ((kapitalEnde - kapitalBeginn) / kapitalBeginn) * (250f / tage) ; 
	}
	
	/**
	 * Addiert zu einem Datum x Tage hinzu 
	 * und gibt ein neues Datum zurück 
	 * @param datum
	 * @param tage
	 * @return
	 */
	public static GregorianCalendar addTage (GregorianCalendar datum, int tage) {
		if (datum == null) return null; 
		GregorianCalendar result = new GregorianCalendar();
		result.setTimeInMillis(datum.getTimeInMillis());
		result.add(Calendar.DAY_OF_MONTH, tage);
		return result; 
	}
	
	/**
	 * Formatiert eine float-Zahl in deutscher Schreibweise mit Komma ohne Punkt. 
	 * @param input
	 * @return
	 */
	public static String toString( float input) {
		DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance(Locale.US);
		df.applyPattern( "#,###,##0.00" );
		String result = df.format(input);
		return result;
	}
	
	/**
	 * parst einen String im Format jjjj-mm-tt oder tt-mm-jjjj
	 * @param datum
	 */
	public static GregorianCalendar parseDatum (String datum) {
		// prüft, ob die ersten 4 Zeichen ein Jahr sein könnten 
		int jahr = 0;
		boolean istJahr = false; 
		try {
			jahr = Integer.parseInt(datum.substring(0, 4));
			istJahr = true;
			// es hat funktioniert 
		} catch (Exception e) {	
			istJahr = false; 
		}
		if (istJahr) {
			return parseDatumJJJJ_MM_TT(datum);
		}
		else {
			return parseDatumTT_MM_JJJJ(datum);
		}
	}
	
	private static GregorianCalendar parseDatumJJJJ_MM_TT (String datum) {
		if (datum == null) return null; 
		int jahr = Integer.parseInt(datum.substring(0, 4));
        int monat = Integer.parseInt(datum.substring(5, 7));
        int tag = Integer.parseInt(datum.substring(8, 10));
        return new GregorianCalendar(jahr, monat-1, tag);
		
	}
	private static GregorianCalendar parseDatumTT_MM_JJJJ (String datum) {
		if (datum == null) return null; 
		int tag = Integer.parseInt(datum.substring(0, 2));
		int monat = Integer.parseInt(datum.substring(3, 5));
        int jahr = Integer.parseInt(datum.substring(6, 10));
        return new GregorianCalendar(jahr, monat-1, tag);
		
	}
	/**
	 * Parst ein Float-String in Punkt oder Komma-Notation
	 * Wenn der Float leer ist, kommt ein Float mit 0,00 zurück
	 */
	public static Float parseFloat (String floatString) {
		Float result = null; 
		// wenn der String leer ist, 
		if (floatString.length() > 0) {
		// erst probieren, ob es mit Punkt-Notation funktioniert
			try {
				result = Float.parseFloat(floatString);
			} catch (NumberFormatException e) {
				// wenn ein Komma die Ursache ist, wird das Komma durch einen Punkt ersetzt 
				if (floatString.indexOf(',') > 0) {
					String test = floatString.replace(',', '.');
					
					try {
						result = Float.parseFloat(test);
					} catch (NumberFormatException e1) {
						return null; 
					}
				}
			}
		}
		if (result == null) {
			result = new Float(0);
		}
		return result; 
	}
	
	/**
	 * rundet einen Betrag kaufmännisch 
	 * @param betrag
	 * @return
	 */
	public static float rundeBetrag (float betrag) {
		return ((float) Math.round(betrag * 100)) / 100;
	}
	
	public static String getLineSeparator () {
		return System.getProperty("line.separator");
	}
	public static String getFileSeparator () {
		return System.getProperty("file.separator");
	}
	
	/**
	 * ermittelt das User-Directory in einem Windows-System 
	 * user.country The ISO code of the operating system's (or local user's) configured country. 
	 * user.dir The local directory from which the Java process has been started, and from which files will be read/written by default unless a path is specified. 
	 * user.home The current user's "home" directory, such as C:\Users\Fred on Windows systems, or /home/fred/ on UNIX-like systems. 
	 * user.language The ISO code of the operating system's (or local user's) configured language, such as "en" for English. 
	 * user.name The local user's system user name. On Windows systems, this is typically close to a "real life" name. On UNIX-like systems, it is common for user names to be all lower case letters. 
	 */
	public static String getUserProperty (String property) {
		return System.getProperty("user." + property);
	}
	/**
	 * Umschließt einen Text mit Anführungszeichen
	 * @param text
	 * @return
	 */
	public static String addAnfZeichen (String text) {
		return ("\"" + text + "\"" );
	}
	
	
	
}
