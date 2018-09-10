package com.algotrading.signal;

import com.algotrading.aktie.Aktie;

/**
 * Ein Signal-Algorythmus muss diese Schnittstelle implementieren 
 * @author Oskar 
 *
 */
public interface SignalAlgorithmus {
	/**
	 * ermittelt Signale anhand eines Kurses 
	 * @param tageskurs
	 * @param aktie
	 * @return Anzahl erzeugter Signale
	 */
	public int ermittleSignal(Aktie aktie, SignalBeschreibung signalbeschreibung);
	

}
