package com.algotrading.indikator;

import com.algotrading.aktie.Aktie;
/**
 * Bewertet die Prognose-Qualität von Signalen in einem bestimmten Zeitraum. 
 * Wird berechnet, nachdem das zugrunde liegende Signal berechnet wurde. 
 * #Achtung nicht fertig !!!
 * 
 * @author oskar
 */
public class SignalBewertung implements IndikatorAlgorithmus {

	private static SignalBewertung instance; 
	
	public static SignalBewertung getInstance () {
		if (instance == null) instance = new SignalBewertung(); 
		return instance; 
	}
	
	private SignalBewertung () {}

	/**
	 * 		
	 */
	@Override
	public void rechne (Aktie aktie, IndikatorBeschreibung indikator) {
		// holt die Kursreihe 
		float[] kurse = aktie.getKursArray();
		// holt die gewünschte Dauer
		int x = ((Integer) indikator.getParameter("dauer")).intValue();
		// holt die Berechnungs-Art als Wert oder Differenz zum Kurs
		int berechnungsArt = 0; 
		Object o = indikator.getParameter("berechnungsart");
		if (o != null) berechnungsArt = ((Integer) o).intValue();
		
		float summe = 0;
		
		// addiert die Kurse der vergangenen x Tage. 
		// dabei wird nicht geschrieben, da die Berechnung noch unvollständig ist. 
		if (kurse.length <= x) Indikatoren.log.error(aktie.name + " zu wenig Kurse: " + kurse.length + " vorhanden: " + x + " benoetigt."); // wenn weniger Kurse vorhanden sind
		// addiert die ersten x Kurse. 
		for (int i = 0 ; i < x ; i++) {
			summe += kurse[i];
		}
		// ein neuer Kurs kommt hinzu, ein alter Kurs fällt weg 
		for (int i = x ; i < kurse.length; i++) {
			float kursneu = kurse[i];
			float kursalt = kurse[i - x];
			summe += kursneu;
			summe -= kursalt; 
			float ergebnis = summe / x;
			// abhängig von der Berechnungs-Art wird noch die Differenz berechnet. 
			if (berechnungsArt == 1) {
				ergebnis -= kursneu;
			}
			// das GD-Ergebnis in den Kurs eintragen
			aktie.getBoersenkurse().get(i).addIndikator(indikator, ergebnis); 
			Indikatoren.log.trace("GD: " + x + " - " + ergebnis);
		}

	}

}
