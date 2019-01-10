package com.algotrading.signal;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;

/**
 * identifiziert Kauf/Verkaufssignale in einer Kursreihe
 * und schreibt sie in die Kursreihe
 * Ist Bestandteil einer Aktie 
 * @author oskar
 *
 */
public class Signalsuche {

	static final Logger log = LogManager.getLogger(Signalsuche.class);
	//Zuordnung zwischen Signaltyp und Berechnung-Algorythmus
	private static HashMap<Short, SignalAlgorithmus> signalAlgorithmen = initialisiereAlgorythmen();
	
	/**
	 * Hier müssen alle Implementierungen bekannt gegeben werden. 
	 * Damit sie über die Konstante gefunden werden. 
	 * TODO: Nicht optimal in der Signalsuche die Registrierung zu machen. 
	 */
	private static HashMap<Short, SignalAlgorithmus> initialisiereAlgorythmen () {
		HashMap<Short, SignalAlgorithmus> result = new HashMap<Short, SignalAlgorithmus>();
		// die Implementierungen der Signal-Algorithmen einhängen 
		result.put(Signal.GDDurchbruch, new GDDurchbruch());
		result.put(Signal.Jahrestag, new Jahrestag());
		result.put(Signal.ADL, new ADLDelta());
		result.put(Signal.FallenderBerg, new SteigendeBergeFallendeTaeler());
		result.put(Signal.SteigenderBerg, new SteigendeBergeFallendeTaeler());
		result.put(Signal.FallendesTal, new SteigendeBergeFallendeTaeler());
		result.put(Signal.SteigendesTal, new SteigendeBergeFallendeTaeler());
		result.put(Signal.MinMax, new MinMax());
		
		return result; 
	}
	
	/**
	 * steuert die Berechnung von allen Signalen einer Aktie auf Basis der vorhandene SignalBeschreibungen 
	 * Die Signalsuche wird in Test-Cases separat/einzeln beauftragt. 
	 * Die Indikatoren müssen bereits berechnet worden sein und hängen am Kurs. 
	 */
	public static void rechneSignale (Aktie aktie) {
		ArrayList<SignalBeschreibung> signalbeschreibungen = aktie.getSignalbeschreibungen();
		SignalAlgorithmus algo; 
		for (SignalBeschreibung signalbeschreibung : signalbeschreibungen) {
			// holt sich den zugehörigen Algorithmus
			algo = signalAlgorithmen.get(signalbeschreibung.getSignalTyp());
			if (algo == null) log.error("sieht aus, wie wenn der Sinalalgorithmus nicht registriert wäre");
			// startet die Berechnung 
			int anzahl = algo.ermittleSignal(aktie, signalbeschreibung);
			log.debug("Signale berechnet: " + signalbeschreibung.getSignalTyp() + " Aktie: " + aktie.name + " Anzahl: " + anzahl);
		}
		
	}

}
