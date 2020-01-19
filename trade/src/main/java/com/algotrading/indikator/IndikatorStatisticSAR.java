package com.algotrading.indikator;

import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

/**
 * Berechnet den Stop-And-Reverse-Indikator
 */
@Entity(name = "StatisticSAR")
@DiscriminatorValue("StatisticSAR")
public class IndikatorStatisticSAR extends IndikatorAlgorithmus {

	private float afstart = 0.02f; // Standardwert
	private float afstufe = 0.02f; // Standardwert
	private float afmaximum = 0.2f; // Standardwert
	private int trendm1 = -1;
	private int trend = trendm1; // Anzahl Tage im UpTrend oder DownTrend
	private float high = 0; // aktueller Hochpunkt
	private float highm1 = 0;
	private float highm2 = 0;
	private float low = 0; // aktueller Tiefpunkt
	private float lowm1 = 0;
	private float lowm2 = 0;
	private float sar = 0;
	private float sarAlt = 0;
	private float tentsar = 0; // Tentative SAR
	private float calcsar = 0; // Calculated SAR
	private float ep = 0; // Extrempunkt
	private float epAlt = 0; // bisheriger Extrempunkt
	private float af = 0; // Anpassungs-Geschwindigkeit
	private float afAlt = 0;

	/**
	 * 
	 * float afStart, float afStufe, float afMaximum
	 * 
	 * @param aktie
	 */
	@Override
	public void rechne(Aktie aktie) {

		afstart = (Float) getParameter("start");
		afstufe = (Float) getParameter("stufe");
		afmaximum = (Float) getParameter("maximum");

		List<Kurs> kurse = aktie.getKursListe();
		Kurs kurs;
		// die ersten 2 Tage finden Vorbereitungen statt. SAR wird nicht berechnet,
		// sondern gesetzt
		for (int i = 0; i <= 1; i++) {
			kurs = kurse.get(i);
			// bevor der neue Kurs gesetzt wird, wird die Historie gefällt
			highm1 = high;
			lowm1 = low;
			high = kurs.high;
			low = kurs.low;
			// den SAR festlegen - nicht berechnen
			if (i > 0) {
				if (trend < 0)
					sar = highm1;
				else
					sar = lowm1;
				// Extrempunkt und Beschleunigung werden ganz normal berechnet.
				ep = rechneEP(epAlt);
				af = rechneAF(afAlt);
			}
		}
		// ab dem 3. Tag wird der SAR berechnet
		for (int i = 2; i < kurse.size(); i++) {
			// Initialisierung vor jeder Berechnung
			// bevor der aktuelle Kurs gesetzt wird, wird die Historie gesetzt
			kurs = kurse.get(i);
			trendm1 = trend;
			highm2 = highm1;
			highm1 = high;
			high = kurs.high;
			lowm2 = lowm1;
			lowm1 = low;
			low = kurs.low;
			epAlt = ep;
			afAlt = af;
			sarAlt = sar;

			calcsar = rechneCalcSAR();
			tentsar = rechneSARTentative();
			trend = rechneTrend(tentsar);
			sar = rechneSAR(tentsar);
			// das Ergebnis wird in den Kurs eingetragen
			kurs.addIndikator(this, sar);
			ep = rechneEP(epAlt);
			af = rechneAF(afAlt);

		} // i

	} // rechne

	/**
	 * berechnet den Extrempunkt
	 */
	private float rechneEP(float EPalt) {
		float result = 0;
		if (trend == -1) {
			result = low;
		} else if (trend == 1) {
			result = high;
		} else if (trend < 1) {
			result = Math.min(low, EPalt);
		} else if (trend > 1) {
			result = Math.max(high, EPalt);
		}
		return result;
	}

	/**
	 * rechnet SAR
	 */
	private float rechneSAR(float tentSAR) {
		float result = 0;
		if (trend == -1) {
			result = Math.max(high, epAlt);
		} else if (trend == 1) {
			result = Math.min(low, epAlt);
		} else {
			result = tentSAR;
		}
		return result;
	}

	/**
	 * rechnet das erwartete SAR
	 */
	private float rechneSARTentative() {
		float result = 0;
		if (trendm1 < 0) { // wir befinden uns im Down-Trend
			// das erwartete SAR ermitteln auf Basis gestriger Daten
			float max = Math.max(highm1, highm2);
			result = Math.max(max, sarAlt + (afAlt * (epAlt - sarAlt)));
		} else { // wir befinden uns im Up-Trend
			// das erwartete SAR ermitteln auf Basis gestriger Daten
			float min = Math.min(lowm1, lowm2);
			result = Math.min(min, sarAlt + (afAlt * (epAlt - sarAlt)));

		}
		return result;
	}

	/**
	 * entscheidet, ob wir uns im Aufwärts- oder Abwärts-Trend befinden setzt
	 * voraus, dass ein voraaussichtliches SAR (tentSAR) berechnet wurde
	 * 
	 * @return
	 */
	private int rechneTrend(float tentSAR) {
		int trendNEU = trend;
		// präfen, wohin der Trend läuft
		if (trendm1 < 0) { // bisher waren wir im Down-Trend
			// das erwartete SAR < als das neue High
			if (tentSAR < high) { // der Trend dreht nach oben
				trendNEU = 1;
			} else { // Trend läuft weiter nach unten
				trendNEU--;
			}
		} else { // bisher sind wir im Up-Trend
			// das erwartete SAR > als das neue High
			if (tentSAR > low) { // der Trend dreht nach oben
				trendNEU = -1;
			} else { // Trend läuft weiter nach unten
				trendNEU++;
			}
		}
		return trendNEU;
	}

	/**
	 * berechnet den AF Acceleration-Faktor
	 * 
	 * @return
	 */
	private float rechneAF(float afALT) {
		float afNEU = 0;
		if (Math.abs(trend) == 1) { // ein neuer Trend, egal welche Richtung
			afNEU = afstart;
		} else { // ein bestehender Trend
			if (ep != epAlt) { // der Extrempunkt ist nicht gleich geblieben
				afNEU = Math.min(afmaximum, afALT + afstufe); // Geschwindigkeit erhäht bis zum Maximum
			}
			// ist der Extrempunkt gleich geblieben, bleibt der AF bestehen
			else {
				afNEU = afALT;
			}
		}
		return afNEU;
	}

	/**
	 * berechnet den auf Vortageswerten basierenden SAR eines Tages. Grundlage sind
	 * Vortageswerte von SAR, Extrempunkt und Beschleunigung.
	 */
	private float rechneCalcSAR() {
		return sarAlt + (afAlt * (epAlt - sarAlt));
	}

	@Override
	public String getKurzname() {
		return "SSAR";
	}

}
