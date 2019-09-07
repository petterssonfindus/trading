package com.algotrading.aktie;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "KursAktie")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "typ", discriminatorType = DiscriminatorType.STRING)
public abstract class KursAktie {

	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "kurse")
	private List<Kurs> kurse = new ArrayList<Kurs>();

	public List<Kurs> getKurse() {
		return kurse;
	}

	public void addKurs(Kurs kurs) {
		kurs.setKursAktie(this);
		this.kurse.add(kurs);
	}

	public String getAktieName() {
		return this.getClass().getName();
	}

}
