package com.algotrading.indikator;

import java.util.ArrayList;

public class Indikatoren extends ArrayList<IndikatorAlgorithmus> {

	private static final long serialVersionUID = 1L;

	public static Indikatoren getNew () {
		return new Indikatoren();
	}
	
	public IndikatorAlgorithmus addIndikator (IndikatorAlgorithmus iA) {
		this.add(iA);
		return iA; 
	}

	
	
}
