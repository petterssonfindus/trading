package com.algotrading.signal;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.aktie.Kurs;
import com.algotrading.component.AktieVerwaltung;
import com.algotrading.depot.Order;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Util;

/**
 * repräsentiert ein Kauf/Verkaufsignal Wurde ausgelöst auf Basis einer
 * SignalBeschreibung mit Parametern Es können mehrere Signale vom gleichen Typ
 * auftreten. Ein Signal gehört zu einem Kurs. Ein Signal hat keinen Parameter,
 * aber eine Referenz auf die SignalBeschreibung Liefert häufig einen Wert über
 * die Stärke Bewertet selbst seine Prognose-Qualität
 * 
 * @author oskar
 *
 */
public class Signal {
	private static final Logger log = LogManager.getLogger(Signal.class);

	private AktieVerwaltung aV;

	// Referenz zum Kurs, zu dem es gehört
	private Kurs kurs;
	// eine Referenz auf die Signalbeschreibung wird beim Erzeugen gesetzt
	private SignalAlgorithmus sA;
	// Kauf oder Verkauf
	private byte kaufVerkauf;
	// optional - eine Zahl von 0 - 100 über die Stärke
	private float staerke;

	// misst die tatsächliche Performance in die Zukunft 
	private HashMap<Integer, Float> performance;

	/**
	 * Konstruktor kann nur über die Methode create genutzt werden. Dadurch kann
	 * beim Erzeugen die Referenz auf den Kurs eingetragen werden.
	 * 
	 * @param tageskurs der Kurs, an dem das Signal hängt.
	 */
	public Signal(SignalAlgorithmus sA, Kurs tageskurs, byte kaufVerkauf, float staerke) {
		this.sA = sA;
		// der SignalAlgorithmus erhält Zugriff auf das neue Signal
		sA.addSignal(this);
		this.kurs = tageskurs;
		this.kaufVerkauf = kaufVerkauf;
		this.performance = new HashMap<Integer, Float>();
		// wenn im Konstruktor die Stärke gesetzt wird
		if (staerke != 0) {
			this.setStaerke(staerke);
		}
	}

	public Kurs getKurs() {
		return this.kurs;
	}

	public float getStaerke() {
		return staerke;
	}

	/**
	 * Die Signal-Stärke kann im Konstruktor oder nachträglich gesetzt werden.
	 */
	public void setStaerke(float staerke) {
		if (staerke == 0) {
			// 			System.out.println("Staerke wird gesetzt auf 0");
		}
		this.staerke = staerke;
	}

	public byte getKaufVerkauf() {
		return this.kaufVerkauf;
	}

	public String kaufVerkaufToString() {
		if (this.kaufVerkauf == Order.KAUF)
			return "Kauf";
		else
			return "Verkauf";
	}

	public void setKaufVerkauf(byte kaufVerkauf) {
		this.kaufVerkauf = kaufVerkauf;
	}

	public SignalAlgorithmus getSignalAlgorithmus() {
		return this.sA;
	}

	private AktieVerwaltung getAV() {
		return this.sA.getaV();
	}

	/**
	 * Fügt eine Performance-Bewertung hinzu. Berechnet
	 * die Performance in die Zukunft, wenn die Kursdaten verfügbar sind, ansonten null. 
	 */
	private Float addPerformance(int tage) {
		float result = 0;
		// hole den aktuellen Kurs
		Kurs aktKurs = this.getKurs();
		// hole den künftigen Kurs
		Kurs kursTage = aktKurs.getKursTage(this.getAV(), tage);
		if (kursTage == null)
			return null;
		// berechne die Performance
		result = (float) Util.rechnePerformancePA(aktKurs.getKurs(), kursTage.getKurs(), tage);
		// Verkaufsignal werden im Vorzeichen umgedreht 
		// if (this.getKaufVerkauf() == Order.VERKAUF)
		// 	result *= -1;
		// trage die Performance ein
		this.performance.put(tage, result);
		return result;
	}

	/**
	 * Das Signal bewertet sich selbst. Dabei prüft es, ob sich der Kurs nach x
	 * Tagen erwartungsgemäß entwickelt hat. Die Bewertung wird gecacht. Wenn es
	 * keine Bewertung gibt, wird sie ermittelt
	 */
	public Float getPerformance(int tage) {
		if (this.performance.containsKey(tage)) {
			return this.performance.get(tage);
		}
		return this.addPerformance(tage);
	}

	/**
	 * allgemeine ausführliche Ausgabe mit Bewertungen 
	 */
	public String toString() {
		String result;
		result = this.kurs.getAktieID() + Util.separatorCSV + DateUtil.formatDate(
				this.kurs.getDatum()) + Util.separatorCSV + this.kaufVerkaufToString() + Util.separatorCSV + this.sA
						.getKurzname() + Util.separatorCSV + Util
								.toStringExcel(this.staerke) + this.toStringPerformance();
		return result;
	}

	/**
	 * Formatierte Ausgabe für Excel-Tabelle
	 */
	// @formatter:off
	public String toStringCSV() {
		StringBuilder sb = new StringBuilder(); 
		sb.append(this.sA.getKurzname() + Util.separatorCSV);
 		sb.append(this.kaufVerkauf == Order.KAUF ? this.getKurs().getClose() : "");
		sb.append(Util.separatorCSV);
 		sb.append(this.kaufVerkauf == Order.VERKAUF ? this.getKurs().getClose() : ""); 
		sb.append(this.toStringPerformance());
		return sb.toString();
	}
	// @formatter:on

	/**
	 * Alle Bewertungen hintereinander, mit csv getrennt
	 * Die Reihenfolge wird durch die Tage-Liste an der Aktie bestimmt 
	 */
	// @formatter:off
	private String toStringPerformance() {
		// die Aktie gibt vor, welche Tage-erformance ausgegeben wird 
		List<Integer> tageListe = this.getKurs().getAktie().getPerformanceTage();
		if (tageListe == null) {
			log.error("Signal ohne Bewertung: " + DateUtil.formatDate(this.getKurs().getDatum()));
			return "";
		}
		StringBuilder sb = new StringBuilder(); 
		for (Integer tag : tageListe) {
			if (this.getPerformance(tag) != null) {
				sb.append(String.format("%s%s", 
					Util.separatorCSV, 
					Util.toStringExcel(Util.rundeBetrag(this.getPerformance(tag), 3))));
			}
		}
		return sb.toString();
	}
	// @formatter:on

	/**
	 * Kurz-Ausgabe: Typ + KV + Stärke ohne Bewertungen
	 */
	// @formatter:off
	public String toStringShort() {
		return String.format("%s%s%s%s%s", 
				Util.toStringExcel(this.staerke),
				Util.separatorCSV,
				this.kaufVerkauf == Order.KAUF ? this.getKurs().getClose() : "", 
				Util.separatorCSV,
				this.kaufVerkauf == Order.VERKAUF ? this.getKurs().getClose() : ""); 
	// @formatter:off
	}
	
	public void setaV(AktieVerwaltung aV) {
		this.aV = aV;
	}

}
