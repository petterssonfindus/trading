package com.algotrading.indikator;

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
import com.algotrading.util.Parameter;
import com.algotrading.util.Util;

/**
 * Alle Indikatoren müssen rechnen können
 * 
 * @author oskar
 *
 */
@Entity
@Table(name = "INDIKATORALGO")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "typ", discriminatorType = DiscriminatorType.STRING)

public abstract class IndikatorAlgorithmus extends Parameter {

	@Id
	@Column(columnDefinition = "VARCHAR(36)")
	private String id;

	@Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	private String name;  // der Name des IndikatorAlgorithmus

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
	 * iteriert über alle Kurse dieser Aktie und berechnet die Indikatorenwerte, die
	 * dann am Kurs hängen Die Parameter hängen am Indikator-Algorithmus
	 */
	public abstract void rechne(Aktie aktie);

	public void synchronizeSAVE() {
		if (this.id == null)
			this.id = UUID.randomUUID().toString();
		this.name = this.getKurzname();
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
	}

	/**
	 * der Wert wird in einen String verwandelt um ihn generisch zu speichern
	 */
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

	/**
	 * verwandelt den String aus der DB zurück in ein Object
	 */
	private Object toObjectParamString(String s) {
		// wenn es ein Integer ist
		try {
			Integer integer = Integer.parseInt(s);
			return integer;
		} catch (Exception e) {

		}
		try {
			Float floatO = Float.parseFloat(s);
			return floatO;
		} catch (Exception e) {

		}
		return s;
	}

	@PostLoad
	public void synchronizeLOAD() {
		if (p1name != null) {
			addParameter(p1name, toObjectParamString(p1wert));
		}
		if (p2name != null) {
			addParameter(p2name, toObjectParamString(p2wert));
		}
		if (p3name != null) {
			addParameter(p3name, toObjectParamString(p3wert));
		}
		if (p4name != null) {
			addParameter(p4name, toObjectParamString(p4wert));
		}
		if (p5name != null) {
			addParameter(p5name, toObjectParamString(p5wert));
		}
	}

	public abstract String getKurzname();

	/**
	 * Wird von der Aktie aufgerufen, wenn die Berechnung erfolgt ist
	 */
	public void berechnet() {
		istBerechnet = true;
	}

	/**
	 * Gibt Auskunft, ob bereits berechnet wurde
	 */
	public boolean istBerechnet() {
		return istBerechnet;
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

	public IndikatorAlgorithmus createConcreteObject(String name) {
		if (name.compareTo("") == 1)
			return new IndikatorGD();
		return null;
	}

	public boolean equals(IndikatorAlgorithmus iA) {
		// der Name muss der selbe sein
		if (!this.getName().matches(iA.getName()))
			return false;
		// die Parameter müssen die selben sein
		if (!this.equalsParameter(iA.getParameterList()))
			return false;
		return true;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getP1name() {
		return p1name;
	}

	public void setP1name(String p1name) {
		this.p1name = p1name;
	}

	public String getP1wert() {
		return p1wert;
	}

	public void setP1wert(String p1wert) {
		this.p1wert = p1wert;
	}

	public String getP2name() {
		return p2name;
	}

	public void setP2name(String p2name) {
		this.p2name = p2name;
	}

	public String getP2wert() {
		return p2wert;
	}

	public void setP2wert(String p2wert) {
		this.p2wert = p2wert;
	}

	public String getP3name() {
		return p3name;
	}

	public void setP3name(String p3name) {
		this.p3name = p3name;
	}

	public String getP3wert() {
		return p3wert;
	}

	public void setP3wert(String p3wert) {
		this.p3wert = p3wert;
	}

	public String getP4name() {
		return p4name;
	}

	public void setP4name(String p4name) {
		this.p4name = p4name;
	}

	public String getP4wert() {
		return p4wert;
	}

	public void setP4wert(String p4wert) {
		this.p4wert = p4wert;
	}

	public String getP5name() {
		return p5name;
	}

	public void setP5name(String p5name) {
		this.p5name = p5name;
	}

	public String getP5wert() {
		return p5wert;
	}

	public void setP5wert(String p5wert) {
		this.p5wert = p5wert;
	}

	public String getName() {
		if (this.name == null)
			this.name = this.getKurzname();
		return name;
	}

	public String getId() {
		return this.id;
	}

}
