package com.algotrading.uimodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UIAlgorithmus {

	private Long id;
	// Name des SignalAlgorithmus
	private String signalAlgorithmusName;
	// Key-Value HashMap von Parameter mit Werten 
	private HashMap<String, String> signalParameter;

	private List<UIIndikatorAlgorithmus> indikatorAlgorithmen = new ArrayList<>();

	public UIAlgorithmus() {
		super();
	}

	public UIAlgorithmus(String signalAlgorithmusName, HashMap<String, String> signalParameter, List<UIIndikatorAlgorithmus> indikatorAlgorithmen) {
		super();
		this.signalAlgorithmusName = signalAlgorithmusName;
		this.signalParameter = signalParameter;
		this.indikatorAlgorithmen = indikatorAlgorithmen;
	}

	public String getSignalAlgorithmusName() {
		return signalAlgorithmusName;
	}

	public HashMap<String, String> getSignalParameter() {
		return signalParameter;
	}

	public List<UIIndikatorAlgorithmus> getIndikatorAlgorithmen() {
		return indikatorAlgorithmen;
	}

	public Long getId() {
		return id;
	}

}
