package com.algotrading.signal;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.algotrading.util.Parameter.Para;

@Entity
@Table( name = "SignalAlgorithmus" )
public class SignalAlgorithmusDAO {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
    @Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false, nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;

	// Referenz auf das Original-Objekt
	@Transient
	private SignalAlgorithmus signalAlgorithmus; 
	
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
	
	protected SignalAlgorithmusDAO () {}
	
	/**
	 * Anhand der Referenz auf das Original-Objekt werden die Werte im DAO-Objekt gesetzt 
	 */
	public SignalAlgorithmusDAO(SignalAlgorithmus sA) {
		this.signalAlgorithmus = sA; 
		this.name = sA.getKurzname();
		// Parameter auslesen 
		List<Para> paras = sA.getParameterList();
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
}	