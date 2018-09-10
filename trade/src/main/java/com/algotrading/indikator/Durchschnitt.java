package com.algotrading.indikator;
/**
 * Kämmert sich um die Berechnung von Durchschnitten
 * @author oskar
 *
 */
public class Durchschnitt {

	/**
	 * Steuert die Berechnungsverfahren zur Durchschnitts-Berechnung
	 * Kapselt die einzelnen Verfahren 
	 * 1 = linear
	 * 2 = degressiv
	 * @param werte
	 * @param methode
	 * @return der Durchschnittswert
	 */
	static float rechneDurchschnitt (float[] werte, byte methode) {
		float result = 0;
		
		switch (methode) {
			// linearer Durchnitt
			case 1: {
				result = rechneDurchschnittLinear(werte);
				break;
			}
			// degressiver Durchschnitt
			case 2: {
				result = rechneDurchschnittDegressiv(werte);
				break;
			}
				
		}
		return result; 
	}

	/**
	 * Rechnet den Durchschnitt mit ansteigender Gewichtung
	 * der erste Wert wird gering gewichtet, der letzte Wert stark gewichtet 
	 * @param werte
	 */
	static float rechneDurchschnittDegressiv (float[] werte) {
		float result = 0;
		int x = werte.length;
		float summe = 0;
		int multiplikator = 0;
		for (int i = 0 ; i < x ; i++) {
			// jeder Wert wird mit seiner Rangstelle gewichtet
			summe += werte[i] * (i+1);
		}
		int divisor = (x/2)*(x+1);
		result = summe / divisor;
		return result; 
	}

	/**
	 * Rechnet den Durchschnitt mit ansteigender Gewichtung
	 * der erste Wert wird gering gewichtet, der letzte Wert stark gewichtet 
	 * @param werte
	 */
	static float rechneDurchschnittLinear (float[] werte) {
		float result = 0;
		int x = werte.length;
		float summe = 0;
		int multiplikator = 0;
		for (int i = 0 ; i < x ; i++) {
			// jeder Wert wird gleich gewichtet
			summe += werte[i];
		}
		result = summe / (x+1);
		return result; 
	}

}
