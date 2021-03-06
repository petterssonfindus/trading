package com.algotrading.signal;

import java.util.HashMap;
import java.util.Iterator;

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

	/**
	 * Fügt eine Performance-Bewertung hinzu. Berechnet
	 * die Performance in die Zukunft, wenn die Kursdaten verfügbar sind, ansonten 0. 
	 * Ein Kauf-Signal wird bei steigenden Kursen positiv bewertet. 
	 * Ein Verkauf-Signal wird bei fallenden Kursen positiv bewertet. 
	 */
	private float addPerformance(int tage) {
		float result = 0;
		// hole den aktuellen Kurs
		Kurs aktKurs = this.getKurs();
		// hole den künftigen Kurs
		Kurs kursTage = aktKurs.getKursTage(aV, tage);
		if (kursTage == null)
			return 0;
		// berechne die Performance
		result = Util.rechnePerformancePA(aktKurs.getKurs(), kursTage.getKurs(), tage);
		// Verkaufsignal werden im Vorzeichen umgedreht 
		if (this.getKaufVerkauf() == Order.VERKAUF)
			result *= -1;
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
		return new Float(this.addPerformance(tage));
	}

	/**
	 * ausführliche Ausgabe mit Bewertungen
	 */
	public String toString() {
		String result;
		result = this.kurs.getAktieID() + Util.separatorCSV + DateUtil.formatDate(
				this.kurs.getDatum()) + Util.separatorCSV + this.kaufVerkaufToString() + Util.separatorCSV + this.sA
						.getKurzname() + Util.separatorCSV + Util.toString(this.staerke) + this.toStringBewertungen();
		return result;
	}

	/**
	 * Alle Bewertungen hintereinander, mit csv getrennt
	 */
	// @formatter:off
	private String toStringBewertungen() {
		String result = "";
		Iterator<Float> it = this.performance.values().iterator();
		while (it.hasNext()) {
			result += String.format("%s%s", 
					Util.separatorCSV, 
					Util.toString(Util.rundeBetrag(it.next().floatValue(), 3)));
		}
		return result;
	}
	// @formatter:on

	/**
	 * Kurz-Ausgabe: Typ + KV + Stärke + Bewertungen
	 */
	// @formatter:off
	public String toStringShort() {
		String result = String.format("%s%s%s%s%s%s%s%s", 
				this.sA.getKurzname(), 
				Util.separatorCSV,  
				this.kaufVerkauf == Order.KAUF?this.kaufVerkaufToString():"", 
				Util.separatorCSV,
				this.kaufVerkauf == Order.VERKAUF?this.kaufVerkaufToString():"", 
				Util.separatorCSV,
				Util.toString(this.staerke),
				this.toStringBewertungen());
		return result;
	// @formatter:off
	}
	
	public void setaV(AktieVerwaltung aV) {
		this.aV = aV;
	}

}
