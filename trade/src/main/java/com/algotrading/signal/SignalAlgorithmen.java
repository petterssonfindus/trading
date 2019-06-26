package com.algotrading.signal;

import java.util.ArrayList;

public class SignalAlgorithmen extends ArrayList<SignalAlgorithmus> {
	
	private static final long serialVersionUID = 1L;

	public static SignalAlgorithmen getNew () {
		return new SignalAlgorithmen();
	}
	
	public SignalAlgorithmus addSignnal (SignalAlgorithmus sA) {
		this.add(sA);
		return sA; 
	}

}
