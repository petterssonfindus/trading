package com.algotrading.signal;

import java.util.ArrayList;

public class Signale extends ArrayList<SignalAlgorithmus> {

	private static final long serialVersionUID = 1L;
	
	private Signale () {}

	public SignalAlgorithmus addSignalAlgorithmus (SignalAlgorithmus sA) {
		this.add(sA);
		return sA; 
	}
	
	public static Signale getNew () {
		return new Signale();
	}
	
}
