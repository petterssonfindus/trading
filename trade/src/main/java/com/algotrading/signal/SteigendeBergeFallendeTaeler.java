package com.algotrading.signal;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.depot.Order;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;

public class SteigendeBergeFallendeTaeler implements SignalAlgorithmus {

	private static final float SCHWELLEBERGSUMME = 0.05f;
	private static final float SCHWELLEBERGSTEIGT = 0.01f;
	private static final float SCHWELLETALFAELLT = -0.01f;
	private static final float SCHWELLEBERGFAELLT = -0.01f;
	private static final float SCHWELLETALSTEIGT = 0.01f;
	// Schwelle, ab der Berge und Täler beräcksichtigt werden. 
	private static final float SCHWELLETALSUMME = 0.05f;
	// Kursdifferenz in Prozentpunkte
	private static final float FAKTORSTAERKEBERGTAL = 0.01f;
	
	static final Logger log = LogManager.getLogger(Signalsuche.class);

	/**
	 * erzeugt Kaufsignal, wenn Berge ansteigen
	 * Verkaufsignal, wenn Täler fallen
	 * @param kursreihe
	 */
	public int ermittleSignal(Aktie aktie, SignalBeschreibung sB) {
		int anzahl = 0;
		ArrayList<Kurs> alleBerge = new ArrayList<Kurs>();
		IndikatorAlgorithmus iB = (IndikatorAlgorithmus) sB.getParameter("indikator");
		if (iB == null) log.error("am Signal SteigendeTälerFallendeBerge hängt kein Indikator");
		for (Kurs kurs : aktie.getBoersenkurse()) {
			float staerke; 
			// prüfe, ob Berg vorhanden
			if (istBerg(kurs,iB)) {
				alleBerge.add(kurs);
				// prüfe, ob Kurs zum letzten Berg ansteigt - Delta ist positiv 
				if (alleBerge.size() > 1) {
					float kursdelta = (kurs.getKurs() - alleBerge.get(alleBerge.size() - 2).getKurs()) / kurs.getKurs();
					if (kursdelta > SteigendeBergeFallendeTaeler.SCHWELLEBERGSTEIGT) {
						staerke = (kursdelta / SteigendeBergeFallendeTaeler.FAKTORSTAERKEBERGTAL);
						sB.createSignal( kurs, Order.KAUF, staerke);
						anzahl++;
					}
					else if (kursdelta < SteigendeBergeFallendeTaeler.SCHWELLEBERGFAELLT) {
						staerke = (kursdelta / SteigendeBergeFallendeTaeler.FAKTORSTAERKEBERGTAL);
						sB.createSignal(kurs, Order.VERKAUF, staerke);
						anzahl++;
					}
				}
			}
		}
		return anzahl; 
	}
	
	/**
	 * prüft, ob der Kurs ein Berg ist 
	 * @param kurs
	 * @return
	 */
	static boolean istBerg (Kurs kurs, IndikatorAlgorithmus indikator) {
		if (kurs.getIndikatorWert(indikator) > SteigendeBergeFallendeTaeler.SCHWELLEBERGSUMME) {
			return true;
		}
		else return false; 
	}

	static boolean istTal (Kurs kurs, IndikatorAlgorithmus indikator) {
		if (kurs.getIndikatorWert(indikator) > SteigendeBergeFallendeTaeler.SCHWELLETALSUMME) {
			return true;
		}
		else return false; 
	}
}
