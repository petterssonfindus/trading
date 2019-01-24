package com.algotrading.depot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.signal.Signal;

public class StrategieAllesKaufen extends SignalStrategie {
	static final Logger log = LogManager.getLogger(StrategieAllesKaufen.class);
	
	/**
	 * Nutzt jedes Kaufsignal zum Kauf
	 */
	@Override
	public Order entscheideSignal(Signal signal, Depot depot) {
		Order order = null; 
		if (signal.getKaufVerkauf() == Order.KAUF) {
			order = depot.kaufe(depot.anfangsbestand/3, signal.getKurs().wertpapier);
		}
		return order; 
	}

}
