package com.algotrading.indikator;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;

/**
 * berechnet alle statistischen Indikatoren auf Basis einer Kursreihe
 * und ergänzt die Kursreihe mit den Daten. 
 * @author oskar
 *
 */
public class Indikatoren {
	static final Logger log = LogManager.getLogger(Indikatoren.class);

	public static final short INDIKATOR_GLEITENDER_DURCHSCHNITT = 1; 
	public static final short INDIKATOR_MINUS_DIFFERENZ = 2; 
	public static final short INDIKATOR_PLUS_DIFFERENZ = 3; 
	public static final short INDIKATOR_VOLATILITAET = 4; 
	public static final short INDIKATOR_BERG = 5; 
	public static final short INDIKATOR_TAL= 6; 
	public static final short INDIKATOR_SAR= 7; 
	public static final short INDIKATOR_RSI= 8; 
	public static final short INDIKATOR_OBV = 10; // On Balance Volume
	public static final short INDIKATOR_MFM = 11; // Money Flow Multiplier
	public static final short INDIKATOR_ADL = 12; // Accumulation Distribution Line (MFM * Volumen)
													// wird auch Chaikin Money Flow genannt, wenn er akkumuliert wird. 
	
	private static HashMap<Short, IndikatorAlgorithmus> indikatoren = initialisiereIndikatoren();
	
	/**
	 * Ordnet jedem Indikatortyp einen Algorithmus zu
	 * @return
	 */
	private static HashMap<Short, IndikatorAlgorithmus> initialisiereIndikatoren () {
		HashMap<Short, IndikatorAlgorithmus> result = new HashMap<Short, IndikatorAlgorithmus>();
		// die Implementierungen der Signal-Algorithmen einhängen 
		result.put(Indikatoren.INDIKATOR_GLEITENDER_DURCHSCHNITT, GleitenderDurchschnitt.getInstance());
		result.put(Indikatoren.INDIKATOR_ADL, AccumulationDistributionLine.getInstance());
		result.put(Indikatoren.INDIKATOR_MFM, MoneyFlowMultiplier.getInstance());
		result.put(Indikatoren.INDIKATOR_OBV, OnBalanceVolume.getInstance());
		result.put(Indikatoren.INDIKATOR_RSI, RSI.getInstance());
		result.put(Indikatoren.INDIKATOR_SAR, StatisticSAR.getInstance());
		result.put(Indikatoren.INDIKATOR_VOLATILITAET, Volatilitaet.getInstance());
		return result; 
	}

	
	/**
	 * steuert die Berechnung der gewünschten Indikatoren
	 * Die Indikator-Beschreibungen hängen initial an der Aktie. 
	 * Jeder berechnete Wert wird mit einer Referenz auf die ursprüngliche Indikator-Beschreibung am Kurs gespeichert
	 * @param aktie
	 */
	public static void rechneIndikatoren(Aktie aktie) {
		if (aktie == null) log.error("Inputvariable aktie ist null");
		ArrayList<IndikatorBeschreibung> indikatorBeschreibungen = aktie.getIndikatorBeschreibungen();
		if (indikatorBeschreibungen == null) log.error("Inputvariable Indikatoren ist null");
		
		for (IndikatorBeschreibung indikatorBeschreibung : indikatorBeschreibungen) {
			// holt sich die Implementierung des Indikators 
			IndikatorAlgorithmus indiAlgo = indikatoren.get(indikatorBeschreibung.getTyp());
			indiAlgo.rechne(aktie, indikatorBeschreibung);
		}
	}
}
