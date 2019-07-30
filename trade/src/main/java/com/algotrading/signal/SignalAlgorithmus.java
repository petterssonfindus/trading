package com.algotrading.signal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.algotrading.aktie.Aktie;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.util.Parameter;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

/**
 * Ein Signal-Algorithmus muss diese Schnittstelle implementieren
 * 
 * @author Oskar
 *
 */
@Entity
@Table(name = "SignalAlgorithmus")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "typ", discriminatorType = DiscriminatorType.STRING)
public abstract class SignalAlgorithmus extends Parameter {
	@Id
	@Column(columnDefinition = "VARCHAR(36)")
	private String id;

	@Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	private String name;

	private String aktieName;

	// Referenz auf die Aktie - wird bei der Erzeugung gesetzt
	@Transient
	private Aktie aktie;

	// eine Liste aller Signale, die von diesem Algorithmus erzeugt wurden
	@Transient
	private List<Signal> signale = new ArrayList<Signal>();

	// eine Liste aller Bewertungen mit unterschiedlichen Tagen, Zeiträumen ...
	@Transient
	private List<SignalBewertung> signalBewertungen = new ArrayList<SignalBewertung>();

	private String p1name;
	private String p1wert;
	private String p2name;
	private String p2wert;
	private String p3name;
	private String p3wert;
	private String p4name;
	private String p4wert;
	private String p5name;
	private String p5wert;

	@Transient
	private boolean istBerechnet = false;

	public String synchronizeSAVE() {

		this.id = UUID.randomUUID().toString(); // sicherheitshalber die ID erzeugen
		this.name = this.getKurzname();
		this.aktieName = this.getAktie().getName();
		List<Para> paras = getParameterList();
		switch (paras.size()) {
		case 5:
			p5name = paras.get(4).getName();
			p5wert = toStringParamObject(paras.get(4).getObject());
		case 4:
			p4name = paras.get(3).getName();
			p4wert = toStringParamObject(paras.get(3).getObject());
		case 3:
			p3name = paras.get(2).getName();
			p3wert = toStringParamObject(paras.get(2).getObject());
		case 2:
			p2name = paras.get(1).getName();
			p2wert = toStringParamObject(paras.get(1).getObject());
		case 1:
			p1name = paras.get(0).getName();
			p1wert = toStringParamObject(paras.get(0).getObject());
		}
		return this.id;
	}

	public void instanziiereParameter() {

	}

	private String toStringParamObject(Object o) {
		String result = null;
		if (o instanceof IndikatorAlgorithmus) {
			IndikatorAlgorithmus iA = (IndikatorAlgorithmus) o;
			result = iA.getId().toString();
		} else {
			result = o.toString();
		}
		return result;
	}

	@PostLoad
	public void synchronizeLOAD() {
		if (p1name != null) {
			addParameter(p1name, p1wert);
		}
		if (p2name != null) {
			addParameter(p2name, p2wert);
		}
		if (p3name != null) {
			addParameter(p3name, p3wert);
		}
		if (p4name != null) {
			addParameter(p4name, p4wert);
		}
		if (p5name != null) {
			addParameter(p5name, p5wert);
		}
	}

	/**
	 * Der Zeitraum, vom Beginn des ersten Signals bis zum letzten Signal
	 */
	public Zeitraum getZeitraumSignale() {
		Signal signal1 = this.signale.get(0);
		Signal signaln = this.signale.get(this.signale.size() - 1);
		return new Zeitraum(signal1.getKurs().getDatum(), signaln.getKurs().getDatum());
	}

	/**
	 * ermittelt Signale anhand einer Kursreihe
	 * 
	 * @param aktie
	 * @return Anzahl erzeugter Signale
	 */
	public abstract int rechne(Aktie aktie);

	public abstract String getKurzname();

	public boolean equals(SignalAlgorithmus sA) {
		// wenn die Referenzen identisch sind, muss es das selbe Objekt sein
		if (this == sA)
			return true;
		// die Aktie muss die selbe sein
		// entweder über die Aktie-Referenz - oder über den Aktie-Name
		if (this.getAktie() != null && sA.getAktie() != null) {
			if (!this.getAktie().getName().matches(sA.getAktie().getName()))
				return false;
		}

		else {
			if (!this.getAktieName().matches(sA.getAktieName()))
				return false;
		}
		// die Parameter des SignalAlgorithmus müssen die selben sein
		if (!this.equalsParameter(sA.getParameterList()))
			return false;
		return true;
	}

	/**
	 * Eine neue Bewertung wird durchgeführt
	 */
	public SignalBewertung createBewertung() {
		SignalBewertung sBW = new SignalBewertung(this);
		this.signalBewertungen.add(sBW);
		return sBW;
	}

	public void addSignal(Signal signal) {
		this.signale.add(signal);
	}

	public List<SignalBewertung> getBewertungen() {
		return this.signalBewertungen;
	}

	public List<Signal> getSignale() {
		return this.signale;
	}

	/**
	 * enthält den Kurznamen und eine Liste der vorhandenen Parameter
	 */
	public String toString() {
		String result = ";I:" + getKurzname();
		for (String name : this.getAllParameter().keySet()) {
			result = result + (Util.separatorCSV + name + ":" + this.getParameter(name));
		}
		return result;
	}

	public Aktie getAktie() {
		return aktie;
	}

	public void setAktie(Aktie aktie) {
		this.aktie = aktie;
	}

	public String getAktieName() {
		return aktieName;
	}

	public String getId() {
		return this.id;
	}

}
