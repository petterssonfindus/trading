package com.algotrading.uimodel;

import java.util.HashMap;

public class UIMapper {

	static HashMap<String, String> mapObjectToString(HashMap<String, Object> input) {
		HashMap<String, String> result = new HashMap<>();
		for (String s : input.keySet()) {
			result.put(s, input.get(s).toString());
		}
		return result;
	}

}
