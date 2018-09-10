package com.algotrading.depot;

import java.util.HashMap;

import com.algotrading.signal.Signal;
/**
 * Generische Strategie nimmt beliebige Indikatoren entgegen und kombiniert diese mit UND-Verknäpfung
 * Alle Bedingungen mässen vorliegen, damit gekauft wird 
 * @author oskar
 *
 */
public class StrategieKaufKombination extends SignalStrategie {
	/**
	 * Bei der Erzeugung werden die Indikatoren gesetzt, die äberwacht werden 
	 */
	public StrategieKaufKombination(HashMap<String, Float> indikator) {

	}
	
	@Override
	public Order entscheideSignal(Signal signal, Depot depot) {
		Order order = null; 
		// TODO Auto-generated method stub
		return order; 
	}

}
