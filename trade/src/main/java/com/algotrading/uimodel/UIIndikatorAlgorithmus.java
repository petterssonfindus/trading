package com.algotrading.uimodel;

import java.util.HashMap;

import com.algotrading.indikator.IndikatorAlgorithmus;

public class UIIndikatorAlgorithmus {

	private String id;
	// Name des IndiaktorAlgorithmus 
	private String indikatorName;
	// Key-Value HashMap von Parameter mit Werten 
	private HashMap<String, String> indikatorParameter;

	public UIIndikatorAlgorithmus() {
		super();
	}

	public UIIndikatorAlgorithmus(IndikatorAlgorithmus iA) {
		this.id = iA.getId();
		this.indikatorName = iA.getName();
		this.indikatorParameter = UIMapper.mapObjectToString(iA.getAllParameter());
	}

	public String getIndikatorName() {
		return indikatorName;
	}

	public HashMap<String, String> getIndikatorParameter() {
		return indikatorParameter;
	}

	public String getId() {
		return id;
	}

}
