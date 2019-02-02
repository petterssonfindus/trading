package com.algotrading.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
	static final Logger log = LogManager.getLogger(Util.class);

	public static String separatorCSV = ";";

	/**
	 * Berechnet die Jahres-Performance/Rendite eines Kapitals das x Tage lang liegt
	 */
	public static float rechnePerformancePA (float kapitalBeginn, float kapitalEnde, int tage) {
		if ((kapitalBeginn == 0) || (kapitalEnde == 0) || tage < 1) log.error("Fehler in rechneRendite() Inputparameter ");
		return ((kapitalEnde - kapitalBeginn) / kapitalBeginn) * (250f / tage) ; 
	}
	
	/**
	 * Berechnet die Kapitalverzinsung, unabhängig vom Zeitraum 
	 */
	public static float rechnePerformance (float kapitalBeginn, float kapitalEnde) {
		return (kapitalEnde - kapitalBeginn) / kapitalBeginn; 
	}
	
	/**
	 * Formatiert eine float-Zahl in Schreibweise mit Komma ohne Punkt. 
	 * Float rechnet mit 7 signifikanten Stellen. 
	 * Das führt bei Beträgen von 10.000 zu Fehlern im Cent-Bereich.
	 */
	public static String toString( Float input) {
		if (input == null) return ""; 
		DecimalFormat df = (DecimalFormat)DecimalFormat.getInstance(Locale.GERMANY);
//		df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.GERMAN)); 
//		df.applyPattern( "####.##0,00" );
		return df.format(input);
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
	
	public static float rundeBetrag (float betrag, int stellen) {
		float x = (float) Math.pow(10d, stellen);
		return ((float) Math.round(betrag * x ) / x);
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
