package com.algotrading.uimodel;

import java.util.List;

/**
 * Enthält den Pfad zu einem File mit Inhalt als String
 * @author xk02200
 *
 */
public class UIFileText {

	private String path;
	private List<String> lines;

	public UIFileText() {
	}

	public UIFileText(String path, List<String> zeilen) {
		super();
		this.path = path;
		this.lines = zeilen;
	}

	public String getPath() {
		return path;
	}

	public List<String> getZeilen() {
		return lines;
	}

}
