package com.algotrading.depot;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.AktieVerzeichnis;
import com.algotrading.aktie.Aktien;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.indikator.IndikatorGD;
import com.algotrading.signal.SignalAlgorithmus;
import com.algotrading.signal.SignalGDDurchbruch;
import com.algotrading.signal.Signale;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

import junit.framework.TestCase;

public class SimulatorTest extends TestCase {
	private static final Logger log = LogManager.getLogger(Util.class);

	protected void setUp() throws Exception {

	}
	
	public void testSimuliereDepots() {
		// Zeitspannen bestimmen
		GregorianCalendar beginn = new GregorianCalendar(2015,1,1);
		GregorianCalendar ende = new GregorianCalendar(2017,6,1);
		Zeitraum zeitraum = new Zeitraum(beginn, ende);
		int dauer = 0;
		int rhythmus = 0;
		
		// Aktienliste erzeugen und befüllen
		Aktien aktien = Aktien.create();
		Aktie aktie = aktien.addAktie("xxxdja");
		
		// Indikatoren konfigurieren 
		// Indikator am Aktien-Behälter ist für alle Aktien gültig. 
		IndikatorAlgorithmus iA1 = aktien.addIndikator(new IndikatorGD());
		iA1.addParameter("dauer", 10);
		iA1.addParameter("durchschnitt", 2f);
		// MInimim der Dauer ist 1 Tag. 

		// Signalbeschreibungen bestimmen
		// anhand der Signalbeschreibungen werden dann die Signale ermittelt
		// die erforderlichen Indikatoren müssen vorhanden sein. 
		SignalAlgorithmus sA = aktien.addSignalAlgorithmus(new SignalGDDurchbruch());
		sA.addParameter("indikator", iA1);
		sA.addParameter("schwelle", 0f);
		sA.addParameter("schwelledurchbruch", 0.01f);

		// die Strategien werden festgelegt
		SignalStrategie signalStrategie = new StrategieJahrAlleSignale();
		signalStrategie.addParameter("kaufbetrag", 0.2f);

		TagesStrategie tagesStrategie = null;
/*		
		TagesStrategie tagesStrategie = new StopLossStrategieStandard();
		tagesStrategie.addParameter("verlust", 0.05f);
*/
		// die Dokumentation festlegen 
		boolean writeOrders = true;
		boolean writeHandelstag = true; 
		
		// Simulation ausführen
		Simulator.simuliereDepots(
				aktien, 
				beginn, 
				ende, 
				dauer, 
				rhythmus, 
				signalStrategie, 
				tagesStrategie,
				writeOrders,
				writeHandelstag);
		
	}
	public void testSimuliereDepotsSellInMay () {
		// Zeitspannen bestimmen
		GregorianCalendar beginn = new GregorianCalendar(2015,1,1);
		GregorianCalendar ende = new GregorianCalendar(2017,6,1);
		Zeitraum zeitraum = new Zeitraum(beginn, ende);
		int dauer = 0;
		int rhythmus = 0;
		// Aktienliste bestimmen

		Aktien aktien = Aktien.create();
		aktien.add(AktieVerzeichnis.getInstance().getAktie("xxxdja"));
//		aktien.add(Aktien.getInstance().getAktie("aa"));

//		ArrayList<Aktie> aktien = Aktien.getInstance().getAktien(zeitraum, false);
		// Indikatoren konfigurieren 
		ArrayList<IndikatorAlgorithmus> indikatoren = new ArrayList<IndikatorAlgorithmus>();
/*		
		Indikator adl = new Indikator(Indikatoren.INDIKATOR_MFM);
		indikatoren.add(adl);
		adl.addParameter("dauer", 10f);
		adl.addParameter("durchschnitt", 2f);
		// MInimim der Dauer ist 1 Tag. 
*/	
		// Signalbeschreibungen bestimmen
		// anhand der Signalbeschreibungen werden dann die Signale ermittelt
		// die erforderlichen Indikatoren mässen vorhanden sein. 
		Signale signalBeschreibungen = new Signale();
		
/*		SignalBeschreibung sb1 = new SignalBeschreibung(Signal.ADL);
		signalBeschreibungen.add(sb1);
		sb1.addParameter("indikator", adl);
		sb1.addParameter("schwelle", 0f);
		sb1.addParameter("gd2", obv10);
		sb1.addParameter("schwelledurchbruch", 0.01f);
		SignalAlgorithmus sb2 = aktien.addSignalAlgorithmus(new Signal;
		signalBeschreibungen.add(sb2);
		sb2.addParameter("tage", 120);
		sb2.addParameter("kaufverkauf", Order.VERKAUF);
		SignalBeschreibung sb3 = new SignalBeschreibung(Signal.Jahrestag);
		signalBeschreibungen.add(sb3);
		sb3.addParameter("tage", 240);
		sb3.addParameter("kaufverkauf", Order.KAUF);
 */

		// die Strategien werden festgelegt
		SignalStrategie signalStrategie = new StrategieJahrAlleSignale();
		signalStrategie.addParameter("kaufbetrag", 0.2f);

		TagesStrategie tagesStrategie = null;
/*		
		TagesStrategie tagesStrategie = new StopLossStrategieStandard();
		tagesStrategie.addParameter("verlust", 0.05f);
*/
		// die Dokumentation festlegen 
		boolean writeOrders = true;
		boolean writeHandelstag = true; 
		
		// Simulation ausfähren
		Simulator.simuliereDepots(
				aktien, 
				beginn, 
				ende, 
				dauer, 
				rhythmus, 
				signalStrategie, 
				tagesStrategie,
				writeOrders,
				writeHandelstag);
		
	}


}
