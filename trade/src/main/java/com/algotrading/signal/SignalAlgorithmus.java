package com.algotrading.signal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.algotrading.aktie.Aktie;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.signalbewertung.SignalBewertung;
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

	// eine Liste an Indiktoren, die der SignalAlgorithmus benötigt
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "SAID")
	private List<IndikatorAlgorithmus> indikatorAlgorithmen = new ArrayList<IndikatorAlgorithmus>();

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

	/**
	 * vor dem Speichern werden alle Werte für die Datenbank vorbereitet
	 */
	public String synchronizeSAVE() {
		if (this.id == null) {
			this.id = UUID.randomUUID().toString(); // sicherheitshalber die ID erzeugen
		}
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
		// alle benötigten IndikatorAlgorithmen werden vorbereitet
		for (IndikatorAlgorithmus iA : this.indikatorAlgorithmen) {
			iA.synchronizeSAVE();
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
		} else {
			if (!this.getAktieName().matches(sA.getAktieName()))
				return false;
		}
		// die Parameter des SignalAlgorithmus müssen die selben sein
		if (!this.equalsParameter(sA.getParameterList()))
			return false;
		// die Indikatoren müssen die selben sein
		if (!this.equalsIndikatoren(sA.indikatorAlgorithmen)) {
			return false;
		}
		return true;
	}

	/**
	 * Indikatoren müssen identisch sein, Reihenfolge spielt keine Rolle
	 */
	private boolean equalsIndikatoren(List<IndikatorAlgorithmus> indikatoren) {
		if (this.indikatorAlgorithmen == null && indikatoren == null)
			return true;
		if (this.indikatorAlgorithmen == null && indikatoren != null)
			return false;
		if (this.indikatorAlgorithmen != null && indikatoren == null)
			return false;
		// die Anzahl muss identisch sein
		if (this.indikatorAlgorithmen.size() != indikatoren.size())
			return false;
		for (IndikatorAlgorithmus iA : this.indikatorAlgorithmen) {
			boolean gefunden = false;
			for (IndikatorAlgorithmus iAVergleich : indikatoren) {
				if (iA.equals(iAVergleich)) {
					gefunden = true;
					break;
				}
			}
			// ein Indikator konnte nicht gefunden werden
			if (gefunden == false) {
				return false;
			}
		}
		// wenn Ausführung bis hier her kommt, sind alle Indikatoren gefunden worden.
		return true;
	}

	public IndikatorAlgorithmus addIndikatorAlgorithmus(IndikatorAlgorithmus iA) {
		this.indikatorAlgorithmen.add(iA);
		return iA;
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
		this.aktieName = aktie.getName();
	}

	public String getAktieName() {
		return aktieName;
	}

	public String getId() {
		return this.id;
	}

	public List<IndikatorAlgorithmus> getIndikatorAlgorithmen() {
		return indikatorAlgorithmen;
	}

	public String getName() {
		return name;
	}

	public String getP1name() {
		return p1name;
	}

	public String getP1wert() {
		return p1wert;
	}

	public String getP2name() {
		return p2name;
	}

	public String getP2wert() {
		return p2wert;
	}

	public String getP3name() {
		return p3name;
	}

	public String getP3wert() {
		return p3wert;
	}

	public String getP4name() {
		return p4name;
	}

	public String getP4wert() {
		return p4wert;
	}

	public String getP5name() {
		return p5name;
	}

	public String getP5wert() {
		return p5wert;
	}

}
