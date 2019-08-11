package com.algotrading.indikator;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.algotrading.aktie.Aktie;

/**
 * 
 * @author oskar
 */
@Entity(name = "2Faktor")
@DiscriminatorValue("2Faktor")
public class Indikator2Faktor extends IndikatorAlgorithmus {

	@Override
	public void rechne(Aktie aktie) {
		// holt sich die 2 Indikatoren
		IndikatorAlgorithmus iA1 = (IndikatorAlgorithmus) getParameter("indikator1");
		IndikatorAlgorithmus iA2 = (IndikatorAlgorithmus) getParameter("indikator2");

	}

	@Override
	public String getKurzname() {
		return "2Faktor";
	}

}
