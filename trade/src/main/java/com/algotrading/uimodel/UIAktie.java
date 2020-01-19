package com.algotrading.uimodel;

import org.springframework.http.HttpStatus;

import com.algotrading.aktie.Aktie;
import com.algotrading.util.RestApplicationException;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UIAktie {

	private String name;
	private long id;
	private String isin;
	private String wkn;
	private int quelle;
	private String firmenname;
	private byte boersenplatz;
	private int land;

	public UIAktie(@JsonProperty("name") String name) {
		System.out.println("name: " + name);
		this.name = name;
	}

	public UIAktie() {
	}

	public UIAktie(Aktie aktie) {
		if (aktie.getName() == null || aktie.getName().isEmpty()) {
			throw new RestApplicationException("Aktie ohne Name", HttpStatus.BAD_REQUEST);
		}
		this.name = aktie.getName();
		this.id = aktie.getId();
		this.isin = aktie.getISIN();
		this.wkn = aktie.getWkn();
		this.quelle = aktie.getQuelle();
		this.firmenname = aktie.getFirmenname();
		this.boersenplatz = aktie.getBoersenplatz();
		this.land = aktie.getLand();
	}

	public Aktie mapToAktie() {
		Aktie aktie = new Aktie(this.name);
		aktie.setId(this.getId());
		aktie.setISIN(this.getIsin());
		aktie.setWkn(this.getWkn());
		aktie.setQuelle(this.getQuelle());
		aktie.setFirmenname(this.getFirmenname());
		aktie.setBoersenplatz(this.getBoersenplatz());
		aktie.setLand(this.getLand());
		return aktie;
	}

	public String getName() {
		return name;
	}

	public Long getId() {
		return id;
	}

	public String getIsin() {
		return isin;
	}

	public String getWkn() {
		return wkn;
	}

	public int getQuelle() {
		return quelle;
	}

	public String getFirmenname() {
		return firmenname;
	}

	public byte getBoersenplatz() {
		return boersenplatz;
	}

	public int getLand() {
		return land;
	}

	@Override
	public String toString() {
		return "UIAktie [name=" + name + ", id=" + id + ", isin=" + isin + ", wkn=" + wkn + ", quelle=" + quelle + ", firmenname=" + firmenname + ", boersenplatz=" + boersenplatz + ", land=" + land + "]";
	}

}
