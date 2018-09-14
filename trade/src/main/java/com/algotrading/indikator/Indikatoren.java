package com.algotrading.indikator;

import java.util.ArrayList;

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

	private static final float BERG = 0.01f;
	private static final float TAL = -0.01f;
	
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
			switch (indikatorBeschreibung.typ) {
				case 1: {
					GleitenderDurchschnitt.getInstance().rechne (aktie, indikatorBeschreibung);
					break;
				}
				case 4: {
					Volatilitaet.getInstance().rechne(aktie, indikatorBeschreibung);
					break;
				}
				case 5: {
					BergTal.getInstance().rechne(aktie, indikatorBeschreibung);
					break;
				}
				case 6: {
//					rechneTal(aktie);
					break;
				}
				case 7: {
					StatisticSAR.getInstance().rechne(aktie, indikatorBeschreibung);
					
					break;
				}
				case 8: {
					RSI.getInstance().rechne(aktie, indikatorBeschreibung);
					break;
				}
				case 10: {
					OnBalanceVolume.rechne(aktie, indikatorBeschreibung);
					break;
				}
				case 11: {
					MoneyFlowMultiplier.rechne(aktie, indikatorBeschreibung);
					break;
				}
				case 12: {
					AccumulationDistributionLine.rechne(aktie, indikatorBeschreibung);
					break;
				}
			}
		}
	}
}
