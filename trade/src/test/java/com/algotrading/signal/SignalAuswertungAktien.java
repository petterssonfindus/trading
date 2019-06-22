package com.algotrading.signal;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGDalt;
import com.algotrading.indikator.IndikatorOBV;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

public class SignalAuswertungAktien extends TestCase {


	public static void testSignalAuswertungAktien () {
		List<Aktie> aktien = AktieVerzeichnis.getInstance().getAktien(DateUtil.createGregorianCalendar(01, 01, 2010));
		Aktie aktie = AktieVerzeichnis.getInstance().getAktie("^gdaxi");
		
		IndikatorAlgorithmus iA = aktie.addIndikatorAlgorithmus(new IndikatorGDalt());
		iA.addParameter("dauer", 10);
		
		IndikatorAlgorithmus iA2 = aktie.addIndikatorAlgorithmus(new IndikatorOBV());
		iA2.addParameter("dauer", 10);
		iA2.addParameter("stabw", 10);
		iA2.addParameter("faktor", 1);

		aktie.rechneIndikatoren();
		
//		SignalBeschreibung sB = aktie.createSignalBeschreibung(Signal.MinMax);
//		sB.addParameter("dauer", 30);
//		sB.addParameter("indikator" , iA);
//		sB.addParameter("schwelle" , 2f);
		SignalAlgorithmus sA2 = aktie.addSignalAlgorithmus(new SignalMinMax());
		sA2.addParameter("dauer", 30);
		sA2.addParameter("indikator" , iA2);
		sA2.addParameter("schwelle" , 1.5f);
		aktie.rechneSignale();
		
		aktie.bewerteSignale(new Zeitraum(2010, 2011), 30);
		aktie.bewerteSignale(new Zeitraum(2012, 2013), 30);
		aktie.bewerteSignale(new Zeitraum(2014, 2015), 30);
		aktie.bewerteSignale(new Zeitraum(2016, 2017), 30);
		aktie.bewerteSignale(new Zeitraum(2000, 2017), 30);
		
//		aktie.writeFileKursIndikatorSignal();
	}
	
}
