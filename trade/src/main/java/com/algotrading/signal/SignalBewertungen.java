package com.algotrading.signal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "SIGNALBEWERTUNGEN")
public class SignalBewertungen {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false, nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date timestamp;

	// Liste aller SignalBewertungen
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "signalbewertung_id")
	private List<SignalBewertung> signalBewertung = new ArrayList<SignalBewertung>();

	public Long getId() {
		return id;
	}

	public List<SignalBewertung> getSignalBewertungen() {
		return signalBewertung;
	}

	public void addSignalBewertungen(List<SignalBewertung> bewertungen) {
		this.signalBewertung.addAll(bewertungen);
	}

}
