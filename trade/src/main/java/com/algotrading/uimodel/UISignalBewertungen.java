package com.algotrading.uimodel;

import java.util.ArrayList;
import java.util.List;

import com.algotrading.signalbewertung.SignalBewertung;
import com.algotrading.signalbewertung.SignalBewertungen;

public class UISignalBewertungen {

	private Long id;
	private Integer anzahl;

	private float kaeufe;
	private float kaeufeKorrekt;
	private float performanceProKauf;
	private float kaeufePerformanceDelta;
	private float verkaeufe;
	private float verkaeufeKorrekt;
	private float performanceProVerkauf;
	private float verkaeufePerformanceDelta;
	private float gesamtPerformance;

	private List<UISignalBewertung> signalBewertung;

	public UISignalBewertungen() {
		super();
	}

	public UISignalBewertungen(SignalBewertungen sB) {

		this.id = sB.getId();
		this.anzahl = sB.getSignalBewertungen().size();
		this.kaeufe = sB.getKaeufe();
		this.kaeufeKorrekt = sB.getKaeufeKorrekt();
		this.verkaeufe = sB.getVerkaeufe();
		this.verkaeufeKorrekt = sB.getVerkaeufeKorrekt();
		this.performanceProKauf = sB.getPerformanceProKauf();
		this.performanceProVerkauf = sB.getPerformanceProVerkauf();
		this.kaeufePerformanceDelta = sB.getKaeufePerformanceDelta();
		this.verkaeufePerformanceDelta = sB.getVerkaeufePerformanceDelta();
		this.gesamtPerformance = sB.getGesamtPerformance();

		this.signalBewertung = mapSBToUISB(sB.getSignalBewertung());

	}

	private List<UISignalBewertung> mapSBToUISB(List<SignalBewertung> liste) {
		List<UISignalBewertung> result = new ArrayList<>();
		for (SignalBewertung sB : liste) {
			result.add(new UISignalBewertung(sB));
		}
		return result;
	}

	public Long getId() {
		return id;
	}

	public Integer getAnzahl() {
		return anzahl;
	}

	public float getKaeufe() {
		return kaeufe;
	}

	public float getVerkaeufe() {
		return verkaeufe;
	}

	public float getKaeufeKorrekt() {
		return kaeufeKorrekt;
	}

	public float getVerkaeufeKorrekt() {
		return verkaeufeKorrekt;
	}

	public float getPerformanceProKauf() {
		return performanceProKauf;
	}

	public float getPerformanceProVerkauf() {
		return performanceProVerkauf;
	}

	public float getKaeufePerformanceDelta() {
		return kaeufePerformanceDelta;
	}

	public float getVerkaeufePerformanceDelta() {
		return verkaeufePerformanceDelta;
	}

	public float getGesamtPerformance() {
		return gesamtPerformance;
	}

	public List<UISignalBewertung> getSignalBewertung() {
		return signalBewertung;
	}

}
