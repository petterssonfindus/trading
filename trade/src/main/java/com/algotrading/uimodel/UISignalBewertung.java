package com.algotrading.uimodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.signalbewertung.SignalBewertung;
import com.algotrading.util.DateUtil;

public class UISignalBewertung {

	private Long id;
	private Integer tage;
	private String aktieName;
	private String datumBeginn;
	private String datumEnde;
	private String parameter;

	private String saName;
	private String saID;
	private HashMap<String, String> saParameter;
	//	private String saParameter;

	private Integer kauf;
	private Float kaufKorrekt;
	private Float performanceProKauf;
	private Float performanceKaufDelta;

	private Integer verkauf;
	private Float verkaufKorrekt;
	private Float performanceProVerkauf;
	private Float performanceVerkaufDelta;

	private Float performance;

	private List<UIIndikatorAlgorithmus> uiIndikatorAlgorithmen;

	public UISignalBewertung() {
	}

	public UISignalBewertung(SignalBewertung sb) {
		this.id = sb.getId();
		this.tage = sb.getTage();
		this.aktieName = sb.getAktieName();
		this.datumBeginn = DateUtil.formatDate(sb.getZeitraumBeginn(), ":");
		this.datumEnde = DateUtil.formatDate(sb.getZeitraumEnde(), ":");
		this.saID = sb.getSignalAlgorithmus().getId();
		this.saName = sb.getSignalAlgorithmus().getKurzname();
		this.saParameter = UIMapper.mapObjectToString(sb.getSignalAlgorithmus().getParameter());

		this.kauf = sb.getKauf();
		this.kaufKorrekt = sb.getKaufKorrekt();
		this.performanceProKauf = sb.getPerformanceProKauf();
		this.performanceKaufDelta = sb.getPerformanceKaufDelta();

		this.verkauf = sb.getKauf();
		this.verkaufKorrekt = sb.getVerkaufKorrekt();
		this.performanceProVerkauf = sb.getPerformanceProVerkauf();
		this.performanceVerkaufDelta = sb.getPerformanceVerkaufDelta();

		this.uiIndikatorAlgorithmen = mapIAToUIIA(sb.getSignalAlgorithmus().getIndikatorAlgorithmen());

	}

	private List<UIIndikatorAlgorithmus> mapIAToUIIA(List<IndikatorAlgorithmus> iAList) {
		List<UIIndikatorAlgorithmus> result = new ArrayList<>();
		iAList.forEach(iA -> result.add(new UIIndikatorAlgorithmus(iA)));
		return result;
	}

	public Long getId() {
		return id;
	}

	public Integer getTage() {
		return tage;
	}

	public String getAktieName() {
		return aktieName;
	}

	public String getSaName() {
		return saName;
	}

	public String getDatumBeginn() {
		return datumBeginn;
	}

	public String getDatumEnde() {
		return datumEnde;
	}

	public String getParameter() {
		return parameter;
	}

	public Integer getKauf() {
		return kauf;
	}

	public Float getKaufKorrekt() {
		return kaufKorrekt;
	}

	public Float getPerformanceProKauf() {
		return performanceProKauf;
	}

	public Float getPerformanceKaufDelta() {
		return performanceKaufDelta;
	}

	public Integer getVerkauf() {
		return verkauf;
	}

	public Float getVerkaufKorrekt() {
		return verkaufKorrekt;
	}

	public Float getPerformanceProVerkauf() {
		return performanceProVerkauf;
	}

	public Float getPerformanceVerkaufDelta() {
		return performanceVerkaufDelta;
	}

	public Float getPerformance() {
		return performance;
	}

	public String getSaID() {
		return saID;
	}

	public List<UIIndikatorAlgorithmus> getUiIndikatorAlgorithmus() {
		return uiIndikatorAlgorithmen;
	}

	public HashMap<String, String> getSaParameter() {
		return saParameter;
	}

}
