package com.algotrading.signal;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.depot.Order;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Util;
import com.algotrading.aktie.Kurs;

/**
 * repräsentiert ein Kauf/Verkaufsignal 
 * Wurde ausgelöst auf Basis einer SignalBeschreibung mit Parametern
 * Es können mehrere Signale vom gleichen Typ auftreten. 
 * Ein Signal gehört zu einem Kurs.
 * Ein Signal hat keinen Parameter, aber eine Referenz auf die SignalBeschreibung
 * Liefert häufig einen Wert über die Stärke
 * @author oskar
 *
 */
public class Signal {
	private static final Logger log = LogManager.getLogger(Signal.class);
	// Referenz zum Kurs, zu dem es gehört
	private Kurs kurs; 
	// eine Referenz auf die Signalbeschreibung wird beim Erzeugen gesetzt 
	private SignalBeschreibung signalBeschreibung; 
	// Kauf oder Verkauf 
	private byte kaufVerkauf;
	// optional - eine Zahl von 0 - 100 über die Stärke
	public float staerke; 
	// bewertet die Prognosequalität dieses Signals 
	private HashMap<Integer, Float> bewertung;
	
	// die Liste aller Signal-Typen
	public static final short SteigenderBerg = 1;
	public static final short FallenderBerg = 2;
	public static final short SteigendesTal= 3;
	public static final short FallendesTal= 4;

	public static final short GDDurchbruch = 5;
	public static final short GDSchnitt = 6;

	public static final short Jahrestag = 8;
	
	public static final short RSI = 10; 
	public static final short ADL = 12; 
	
	public static final short MinMax = 13; 
	
	/**
	 * private Konstruktor kann nur über die Methode create genutzt werden. 
	 * Dadurch kann beim Erzeugen die Referenz auf den Kurs eingetragen werden. 
	 * @param tageskurs der Kurs, an dem das Signal hängt. 
	 * @param kaufVerkauf
	 * @param typ
	 * @param staerke
	 */
	private Signal (SignalBeschreibung sB, Kurs tageskurs, byte kaufVerkauf, float staerke){
		this.signalBeschreibung = sB; 
		this.kurs = tageskurs; 
		this.kaufVerkauf = kaufVerkauf;
		this.staerke = staerke;
	}
	
	/**
	 * Die Signalsuche hat ein Signal identifiziert und hängt es in den Kurs ein
	 * Der Typ ist in der zugehörigen Signalbeschreibung festgelegt. 
	 * @param tageskurs 
	 * @param kaufVerkauf 
	 * @param staerke (optional)
	 * @return
	 */
	public static Signal create (SignalBeschreibung sB, Kurs tageskurs, byte kaufVerkauf, float staerke) {
		Signal signal = new Signal(sB, tageskurs, kaufVerkauf, staerke);
		tageskurs.addSignal(signal);
		log.debug("neues Signal: " + signal.toString());
		return signal;
	}
	/**
	 * Zugriff auf Typ-Eigenschaft, die an der Beschreibung gehalten wird
	 * @return
	 */
	public short getTyp () {
		return this.signalBeschreibung.getSignalTyp();
	}

	public Kurs getKurs () {
		return this.kurs;
	}

	public byte getKaufVerkauf() {
		return this.kaufVerkauf;
	}
	
	public String kaufVerkaufToString() {
		if (this.kaufVerkauf == Order.KAUF) return "Kauf";
		else return "Verkauf";
	}

	public void setKaufVerkauf(byte kaufVerkauf) {
		this.kaufVerkauf = kaufVerkauf;
	}
	
	public SignalBeschreibung getSignalBeschreibung() {
		return signalBeschreibung;
	}
	/**
	 * Fügt eine Bewertung hinzu
	 * Berechnet die Performance in die Zukunft, wenn die Kursdaten verfügbar sind. 
	 * @param tage
	 * @return
	 */
	public float addBewertung (int tage) {
		float result = 0;
		// hole den aktuellen Kurs
		Kurs aktKurs = this.getKurs();
		// hole den künftigen Kurs
//		float neuKurs = aktKurs.;
		// berechne die Performance
		
		// trage die Performance ein 
		return result; 
	}
	
	public String toString () {
		String result; 
		result = this.kurs.wertpapier + Util.separatorCSV +
			DateUtil.formatDate(this.kurs.datum) + Util.separatorCSV + 
			this.kaufVerkaufToString() + Util.separatorCSV + 
			this.signalBeschreibung.getSignalTyp() + Util.separatorCSV + 
			Util.toString(this.staerke);
		return result;
	}
	
	public String toStringShort () {
		String result; 
		result = this.signalBeschreibung.getSignalTyp() + Util.separatorCSV + 
				this.kaufVerkaufToString() + Util.separatorCSV + 
				Util.toString(this.staerke);
		return result;
	}

}
