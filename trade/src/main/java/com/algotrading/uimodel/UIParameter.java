package com.algotrading.uimodel;

import java.util.HashMap;

public class UIParameter {

	HashMap<String, String> parameter = new HashMap<>();

	public HashMap<String, String> getParameter() {
		return parameter;
	}

	public void addParameter(String key, String value) {
		this.parameter.put(key, value);
	}

	public UIParameter() {
	}

}
