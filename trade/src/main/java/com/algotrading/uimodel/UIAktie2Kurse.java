package com.algotrading.uimodel;

import java.util.List;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

public class UIAktie2Kurse {

	private Long id;
	private String name;
	private UIKurs erster;
	private UIKurs letzter;
	private Integer anzahl;

	public UIAktie2Kurse() {

	}

	public UIAktie2Kurse(Aktie aktie) {
		this.id = aktie.getId();
		this.name = aktie.getName();
		List<Kurs> kurse = aktie.getKursListe();
		if (kurse != null && !kurse.isEmpty()) {
			this.anzahl = kurse.size();
			this.erster = new UIKurs(kurse.get(0));
			this.letzter = new UIKurs(kurse.get(kurse.size() - 1));
		}
	}

	public UIAktie2Kurse(Long id, String name, UIKurs erster, UIKurs letzter) {
		super();
		this.id = id;
		this.name = name;
		this.erster = erster;
		this.letzter = letzter;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public UIKurs getErster() {
		return erster;
	}

	public UIKurs getLetzter() {
		return letzter;
	}

	public Integer getAnzahl() {
		return anzahl;
	}

}
