package com.algotrading.signal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Aktie;
import com.algotrading.aktie.Kurs;
import com.algotrading.depot.Order;
import com.algotrading.indikator.IndikatorAlgorithmus;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Zeitraum;

@Entity(name = "SignalDurchbruch")
@DiscriminatorValue("SignalDurchbruch")
public class SignalGDDurchbruch extends SignalAlgorithmus {
	static final Logger log = LogManager.getLogger(SignalGDDurchbruch.class);

	/**
	 * erzeugt ein Signal, wenn der Kurs den GD schneidet 
	 * Stärke sagt aus, wie weit der Kurs vom GD abweicht. 
	 * Parameter "schwelle" - Dezimalwert der Überschreitung 
	 * Parameter "indikator" - die zugehörige GD-IndikatorAlgorithmus
	 * Parameter "zeitraum" - der zu berechnende Zeitraum
	 * @param kursreihe
	 */
	@Override
	public int rechne(@NotNull Aktie aktie) {
		int anzahl = 0;
		IndikatorAlgorithmus indikator = (IndikatorAlgorithmus) getIndikatorAlgorithmen().get(0);
		if (indikator == null)
			log.error("Signal enthält keinen Indikator");
		// wie weit muss der Kurs den Gleitenden Durchschnitt durchbrechen
		float schwelle = (Float) getParameter("schwelle");
		float schwelleSteigung = 1 + schwelle;
		float schwelleSinkflug = 1 - schwelle;
		// wenn der Zeitraum explizit gesetzt wurde (ansonsten werden alle Kurse geholt) 
		Zeitraum zeitraum = (Zeitraum) getParameter("zeitraum");

		for (Kurs kurs : aktie.getKurse(zeitraum)) {
			Kurs vortageskurs = aktie.getVortageskurs(kurs);
			if (vortageskurs != null) {
				// bisher darunter, jetzt darüber
				// dabei werden die Signale erstellt und mit dem Kurs verbunden 
				if (pruefeGleitenderDurchschnittSteigung(kurs, vortageskurs, indikator, schwelleSteigung))
					anzahl++;

				// bisher darüber, jetzt darunter
				if (pruefeGleitenderDurchschnittSinkflug(kurs, vortageskurs, indikator, schwelleSinkflug))
					anzahl++;
			}
		}
		return anzahl;
	}

	/**
	 * bisher darunter, jetzt darüber
	 * erzeugt Signale und hängt sie an den Kurs an
	 * wenn kein Indikator verfügbar am Tag oder Vortag, dann geschieht nichts
	 */
	private boolean pruefeGleitenderDurchschnittSteigung(Kurs kurs, Kurs vortageskurs, IndikatorAlgorithmus iA,
			float schwelle) {
		if (kurs == null || vortageskurs == null)
			log.error("Inputvariable ist null");
		boolean result = false;
		Float gd = kurs.getIndikatorWert(iA);
		Float gdvt = vortageskurs.getIndikatorWert(iA);
		log.trace(
				"GD-Signal Steigung: " + DateUtil.formatDate(kurs.getDatum()) + " - " + vortageskurs
						.getKurs() + " GDVt: " + gdvt + " Kurs: " + kurs.getKurs() + " GD: " + gd);
		if (gd != null && gdvt != null) {

			Signal signal = null;
			// wenn am Vortag der Kurs unter dem GD war, und heute der Kurs über dem GD ist 
			if ((vortageskurs.getKurs() < (gdvt * schwelle)) && kurs.getKurs() > (gd * schwelle)) {
				signal = kurs.createSignal(this, Order.KAUF, 0);
				result = true;
				signal.setStaerke((kurs.getKurs() - gd) / gd);
				log.debug(
						"GD-Steigung erkannt: " + DateUtil.formatDate(kurs.getDatum()) + " VTKurs " + vortageskurs
								.getKurs() + " GDVt: " + gdvt + " Kurs: " + kurs.getKurs() + " GD: " + gd);
			}
		}
		return result;
	}

	/**
	 * bisher darüber, jetzt darunter
	 * Wenn der IndikatorWert fehlt am Tageskurs oder Vortag, geschieht nichts
	 */
	private boolean pruefeGleitenderDurchschnittSinkflug(Kurs tageskurs, Kurs vortageskurs,
			IndikatorAlgorithmus indikator, float schwelle) {
		if (tageskurs == null || vortageskurs == null || indikator == null)
			log.error("Inputvariable ist null");
		boolean result = false;
		Float gd = tageskurs.getIndikatorWert(indikator);
		Float gdvt = vortageskurs.getIndikatorWert(indikator);
		Signal signal = null;

		if (gd != null && gdvt != null) {
			if ((vortageskurs.getKurs() > (gdvt * schwelle)) && tageskurs.getKurs() < (gd * schwelle)) {
				signal = tageskurs.createSignal(this, Order.VERKAUF, 0);
				result = true;
				signal.setStaerke((gd - tageskurs.getKurs()) / gd);
				log.debug(
						"GD-Sinkflug: " + DateUtil.formatDate(tageskurs.getDatum()) + " VTKurs " + vortageskurs
								.getKurs() + " GDVt: " + gdvt + " Kurs: " + tageskurs.getKurs() + " GD: " + gd);
			}
		}
		return result;
	}

	@Override
	public String getKurzname() {
		return "GDDurch";
	}

}
