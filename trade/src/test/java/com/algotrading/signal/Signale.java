package com.algotrading.signal;

import java.util.ArrayList;

public class Signale extends ArrayList<SignalAlgorithmus> {
	
	private static final long serialVersionUID = 1L;

	public static Signale getNew () {
		return new Signale();
	}
	
	public SignalAlgorithmus addSignnal (SignalAlgorithmus sA) {
		this.add(sA);
		return sA; 
	}

}
