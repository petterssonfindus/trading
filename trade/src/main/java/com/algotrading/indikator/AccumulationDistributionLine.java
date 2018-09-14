package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

public class AccumulationDistributionLine implements IndikatorAlgorithmus {

	private static AccumulationDistributionLine instance; 
	
	public static AccumulationDistributionLine getInstance () {
		if (instance == null) instance = new AccumulationDistributionLine(); 
		return instance; 
	}

	private AccumulationDistributionLine () {}

	/**
	 * Accumulation Distribution Line
	 * MFM * Volume (akkumuliert) 
	 * Volumen ist positiv, wenn Schlusskurs nahe Hächstkurs
	 * Chaikin Money Flow rechnet identisch, akkumuliert zusätzlich Perioden-Daten
	 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:accumulation_distribution_line#trend_confirmation
	 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:chaikin_money_flow_cmf
	 * @param aktie
	 * @param indikator
	 */
	public void rechne (Aktie aktie , IndikatorBeschreibung indikator) {
		// holt die Kurse, an denen die Umsätze dran hängen.
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		// holt den Parameter "dauer" aus dem Indikator 
		int x = (Integer) indikator.getParameter("dauer");
		// holt den Parameter "durchschnitt" aus dem Indikator, falls er vorhanden ist 
		Object durchschnitt = indikator.getParameter("durchschnitt");
		// wenn nicht vorhanden, wird er vorbelegt mit 1 = linear
		byte verfahren = 0;
		if (durchschnitt == null) {
			verfahren = 1;	// Vorbelegung mit 1
		}
		else {
			verfahren = ((Float) durchschnitt).byteValue();  // den Wert auslesen 
		}
		Kurs kurs; 
		Kurs kursx; 
		float mfm = 0;
		float adlsumme = 0;
		// iteriert äber alle Tageskurse unter Beräcksichtigung der Vorlaufzeit 
		for (int k = x ; k < kurse.size() ; k++) {
			// der Kurs, fär den gerechnet wird
			kurs = kurse.get(k);
			// die Ergebnisse werden zwischen-gespeichert
			float[] adl = new float[x];
			// fär jeden Kurs x-Tage zuräck 
			for (int i = 1 ; i <= x ; i++) {
				kursx = kurse.get(k - x + i);
				// erst den Multiplikator berechnen
				mfm = MoneyFlowMultiplier.calculateMFM(kursx);
				// dann das Volumen mal dem Multiplikator 
				adl[i-1] = mfm * kursx.volume; 
			}
			// den Durchschnittswert berechnen
			adlsumme = Durchschnitt.rechneDurchschnitt(adl, verfahren);
			// das Ergebnis in den Kurs als Indikator eintragen 
			kurs.addIndikator(indikator, adlsumme); 
			adlsumme = 0;
		}
		
	}

}
