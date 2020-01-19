package com.algotrading.uimodel;

import java.util.ArrayList;
import java.util.List;

public class UICreateSignalBewertung {

	// ID der Aktie 
	private String aktieId;
	// Turnus der Bewertungs-Zeiträume
	private Integer bewertungTurnus;
	// Liste von typischen Tage-Zeiträumen zur Bewertung 
	private Integer bewertungTage;
	// SignalAlgorithmus mit zugehörigen IndikatorAlgorithmen
	private List<UIAlgorithmus> algorithmen = new ArrayList<>();

	public UICreateSignalBewertung() {
	}

	public String getAktieId() {
		return aktieId;
	}

	public Integer getBewertungTurnus() {
		return bewertungTurnus;
	}

	public Integer getBewertungTage() {
		return bewertungTage;
	}

	public List<UIAlgorithmus> getAlgorithmen() {
		return algorithmen;
	}

	// @formatter:off
	@Override
	public String toString() {
		return "UICreateSignalBewertung [aktieId=" + aktieId + 
				", bewertungTurnus=" + bewertungTurnus + 
				", bewertungTage=" + bewertungTage + 
				"]";
	}
	// @formatter:on

}
