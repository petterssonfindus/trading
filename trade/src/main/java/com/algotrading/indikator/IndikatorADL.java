package com.algotrading.indikator;

import java.util.ArrayList;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

@Entity(name = "AccumulationDistributedLine")
@DiscriminatorValue("AccumulationDistributedLine")
public class IndikatorADL extends IndikatorAlgorithmus {

	private static IndikatorADL instance;

	public static IndikatorADL getInstance() {
		if (instance == null)
			instance = new IndikatorADL();
		return instance;
	}

	IndikatorADL() {
	}

	/**
	 * Accumulation Distribution Line MFM * Volume (akkumuliert) Volumen ist
	 * positiv, wenn Schlusskurs nahe Höchstkurs Chaikin Money Flow rechnet
	 * identisch, akkumuliert zusätzlich Perioden-Daten
	 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:accumulation_distribution_line#trend_confirmation
	 * http://stockcharts.com/school/doku.php?id=chart_school:technical_indicators:chaikin_money_flow_cmf
	 * 
	 * @param aktie
	 * @param indikator
	 */
	public void rechne(Aktie aktie) {
		// holt die Kurse, an denen die Umsätze dran hängen.
		ArrayList<Kurs> kurse = aktie.getKursListe();
		// holt den Parameter "dauer" aus dem Indikator
		int x = (Integer) getParameter("dauer");
		// holt den Parameter "durchschnitt" aus dem Indikator, falls er vorhanden ist
		Object durchschnitt = getParameter("durchschnitt");
		// wenn nicht vorhanden, wird er vorbelegt mit 1 = linear
		byte verfahren = 0;
		if (durchschnitt == null) {
			verfahren = 1; // Vorbelegung mit 1
		} else {
			verfahren = ((Float) durchschnitt).byteValue(); // den Wert auslesen
		}
		Kurs kurs;
		Kurs kursx;
		float mfm = 0;
		float adlsumme = 0;
		// iteriert äber alle Tageskurse unter Beräcksichtigung der Vorlaufzeit
		for (int k = x; k < kurse.size(); k++) {
			// der Kurs, fär den gerechnet wird
			kurs = kurse.get(k);
			// die Ergebnisse werden zwischen-gespeichert
			float[] adl = new float[x];
			// fär jeden Kurs x-Tage zuräck
			for (int i = 1; i <= x; i++) {
				kursx = kurse.get(k - x + i);
				// erst den Multiplikator berechnen
				mfm = IndikatorMFM.calculateMFM(kursx);
				// dann das Volumen mal dem Multiplikator
				adl[i - 1] = mfm * kursx.volume;
			}
			// den Durchschnittswert berechnen
			adlsumme = Durchschnitt.rechneDurchschnitt(adl, verfahren);
			// das Ergebnis in den Kurs als Indikator eintragen
			kurs.addIndikator(this, adlsumme);
			adlsumme = 0;
		}

	}

	@Override
	public String getKurzname() {
		return "ADL";
	}

}
