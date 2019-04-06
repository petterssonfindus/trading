package com.algotrading.indikator;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.algotrading.util.Parameter.Para;

/**
 * JPA-Persistenz für abstrakte Oberklasse IndikatorAlgorithmus 
 * Die Liste der Parameter wird aufgelöst in einzelne Attribute, die zu Spalten in der Tabelle werden. 
 * Der Name des IndikatorAlgorithmus wird als String gehalten. 
 * @author oskar
 */

@Entity
@Table( name = "INDIKATOR" )
public class IndikatorAlgorithmusDAO {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	// Referenz auf das Original-Objekt
	@Transient
	private IndikatorAlgorithmus indikatorAlgorithmus; 
	
	private String name; 
	
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
	
	protected IndikatorAlgorithmusDAO () {}
	
	/**
	 * Anhand der Referenz auf das Original-Objekt werden die Werte im DAO-Objekt gesetzt 
	 */
	public IndikatorAlgorithmusDAO(IndikatorAlgorithmus iA) {
		this.indikatorAlgorithmus = iA; 
		this.name = iA.getKurzname();
		// Parameter auslesen 
		List<Para> paras = iA.getParameterList();
		switch (paras.size()) {
			case 5: 
				p5name = paras.get(4).getName();
				p5wert = paras.get(4).getObject().toString();
			case 4: 
				p4name = paras.get(3).getName();
				p4wert = paras.get(3).getObject().toString();
			case 3: 
				p3name = paras.get(2).getName();
				p3wert = paras.get(2).getObject().toString();
			case 2: 
				p2name = paras.get(1).getName();
				p2wert = paras.get(1).getObject().toString();
			case 1: 
				p1name = paras.get(0).getName();
				p1wert = paras.get(0).getObject().toString();
		}
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public IndikatorAlgorithmus getIndikatorAlgorithmus() {
		return indikatorAlgorithmus;
	}
	public void setIndikatorAlgorithmus(IndikatorAlgorithmus indikatorAlgorithmus) {
		this.indikatorAlgorithmus = indikatorAlgorithmus;
	}
	
	public String getName() {
		return name;
	}

	public String getP1name() {
		return p1name;
	}

	public String getP2name() {
		return p2name;
	}

	public String getP3name() {
		return p3name;
	}

	public String getP4name() {
		return p4name;
	}

	public String getP5name() {
		return p5name;
	}

	public String getP1wert() {
		return p1wert;
	}

	public String getP2wert() {
		return p2wert;
	}

	public String getP3wert() {
		return p3wert;
	}

	public String getP4wert() {
		return p4wert;
	}

	public String getP5wert() {
		return p5wert;
	}

	
}
