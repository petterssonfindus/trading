package com.algotrading.depot;

import com.algotrading.signal.Signal;

/**
 * Kauft, wenn GD und RSI positiv sind 
 * @author oskar
 *
 */
public class StrategieGDmitRSI extends SignalStrategie {

	private boolean GDDurchbruch = false; 
	private boolean RSIKauf = false; 
	
	@Override
	public Order entscheideSignal(Signal signal, Depot depot) {
		Order order = null; 
		// filtere die Signale
		// reagiert auf GD-Durchbrüche ( Kauf oder Verkauf) 
		if (signal.getClass().toString() == "SignalGDDurchbruch") {
			// GD -Kauf-Signal 
			if (signal.getKaufVerkauf() == Order.KAUF) {
				this.GDDurchbruch = true;
			}
			// GD - Verkauf-Signal 
			// die Kauf-Zone wird damit verlassen 
			else {
				this.GDDurchbruch = false;
			}
		}
		// reagiert auf RSI - Durchbrüche  
		if (signal.getClass().toString() == "SignalGDDurchbruch") {
			// Eintritt in die Kaufzone
			if (signal.getKaufVerkauf() == Order.KAUF) {
				this.RSIKauf = true;
			}
			// Austritt aus der Kauf-Zone
			else {
				this.RSIKauf = false;
			}
		}
		// wenn sich beide Indikatoren in der Kaufzone befinden, wird gekauft 
		if (this.GDDurchbruch && this.RSIKauf) {
			order = depot.kaufe(depot.anfangsbestand / 3, signal.getKurs().wertpapier);
			// Abwarten, bis zum nächsten Doppelsignal
			this.GDDurchbruch = false; 
			this.RSIKauf = false; 
		}
		return order; 
	}

}
