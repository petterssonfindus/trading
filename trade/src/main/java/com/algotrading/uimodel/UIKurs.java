package com.algotrading.uimodel;

import com.algotrading.aktie.Kurs;
import com.algotrading.util.DateUtil;

public class UIKurs {

	private long aktieId;
	private String aktieName;
	private String close;
	private String datum;
	private float kurs;

	public UIKurs(Kurs kurs) {
		this.aktieId = kurs.getAktieID();
		this.aktieName = kurs.getAktieName();
		this.close = kurs.getClose();
		this.datum = DateUtil.formatDate(kurs.getDatum(), ":");
		this.kurs = kurs.getKurs();
	}

	public long getAktieId() {
		return aktieId;
	}

	public String getAktieName() {
		return aktieName;
	}

	public String getClose() {
		return close;
	}

	public String getDatum() {
		return datum;
	}

	public float getKurs() {
		return kurs;
	}

}
