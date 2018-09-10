package com.algotrading.indikator;

import java.util.ArrayList;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

public class MoneyFlowMultiplier {

	/**
	 * Money Flow Multiplier = [(Close  -  Low) - (High - Close)] /(High - Low) 
	 * Ist positiv, wenn Schlusskurs nahe Tages-high - ist negativ wenn Schlusskurs nahe Tages-low. 
	 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:accumulation_distribution_line#trend_confirmation
	 * @param aktie
	 * @param indikator
	 */
	static void rechne (Aktie aktie, IndikatorBeschreibung indikator) {
		// holt die Kurse, an denen die Umsätze dran hängen.
		ArrayList<Kurs> kurse = aktie.getBoersenkurse();
		// holt den Parameter aus dem Indikator 
		int x = ((Float) indikator.getParameter("dauer")).intValue();
		Kurs kurs; 
		Kurs kursx; 
		float mfm = 0;
		float mfmsumme = 0;
		// iteriert äber alle Tageskurse unter Beräcksichtigung der Vorlaufzeit 
		for (int k = x ; k < kurse.size() ; k++) {
			// der Kurs, fär den gerechnet wird
			kurs = kurse.get(k);
			// fär jeden Kurs x-Tage zuräck 
			for (int i = 1 ; i < x ; i++) {
				kursx = kurse.get(k - x + i);
				mfm = calculateMFM(kursx);
				mfmsumme += mfm; 
			}
			kurs.addIndikator(indikator, mfmsumme); 
			mfmsumme = 0;
		}
		
	}

	static float calculateMFM (Kurs kurs) {
		float close = kurs.close;
		float low = kurs.low;
		float high = kurs.high;
		float mfm = (((close - low) - (high - close)) / (high - low));
		return mfm; 
	}

}
