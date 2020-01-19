package com.algotrading.uimodel;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.algotrading.aktie.Kurs;

public class UIKursListe {

	private List<UIKurs> uiKursListe = new ArrayList<>();

	public UIKursListe() {
	}

	public UIKursListe(List<Kurs> kurse) {
		for (Kurs kurs : kurse) {
			this.uiKursListe.add(new UIKurs(kurs));
		}
	}

	public List<UIKurs> getKursListe() {
		return uiKursListe;
	}

	public void addKurs(@NotNull Kurs kurs) {
		this.uiKursListe.add(new UIKurs(kurs));
	}

}
