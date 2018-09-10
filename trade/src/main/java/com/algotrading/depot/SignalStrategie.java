package com.algotrading.depot;

import com.algotrading.signal.Signal;
import com.algotrading.util.Parameter;

/**
 * Bei einer Depot-Simulation wird eine Kauf- und Verkaufstrategie eingesetzt
 * Setzt Signale in Orders um.
 * Hat Zugriff auf die Aktie äber das Signal und den Zustand des Depots.  
 * @author oskar
 */
public abstract class SignalStrategie extends Parameter {
	
	/**
	 * anhand eines Signals wird entschieden, ob es in eine Order umgesetzt wird.
	 * @param signal
	 * @param depot
	 */
	public abstract Order entscheideSignal (Signal signal, Depot depot);
	
}
