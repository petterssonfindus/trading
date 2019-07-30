package com.algotrading.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.algotrading.indikator.IndikatorAlgorithmus;

/**
 * Verwaltet eine Liste von Parametern mit Namen und beliebigem Object. Es kann
 * sich um einen Zahlen-Wert handeln oder einen Zeitraum oder eine
 * IndikatorAlgorithmus Über den Namen kann man sich den Wert besorgen.
 * 
 * @author oskar
 */
@Component
public class Parameter {
	static final Logger log = LogManager.getLogger(Parameter.class);

	private HashMap<String, Object> parameter = new HashMap<String, Object>();

	/**
	 * Holt ein Parameter, falls er vorhanden ist - ansonsten null
	 * 
	 * @param name
	 * @return der Wert als Object, oder null
	 */
	public Object getParameter(String name) {
		Object result;
		result = this.parameter.get(name);
		if (result == null) {
		}
		return result;
	}

	public HashMap<String, Object> getAllParameter() {
		return this.parameter;
	}

	public boolean replace(String key, Object value) {
		this.parameter.replace(key, value);
		return true;
	}

	/**
	 * Alle Parameter als Liste mit Para-Objekten
	 */
	public List<Para> getParameterList() {
		ArrayList<Para> paras = new ArrayList<Para>();
		for (String name : this.parameter.keySet()) {
			// einen neuen Para erzeugen mit Name und zugehörigem Objekt
			paras.add(new Para(name, this.parameter.get(name)));
		}
		return paras;
	}

	/**
	 * wenn es den Parameter bereits gibt, wird der Wert überschrieben
	 */
	public void addParameter(String name, float wert) {
		this.parameter.put(name, wert);
	}

	/**
	 * wenn es den Parameter bereits gibt, wird der Wert überschrieben
	 */
	public void addParameter(String name, int wert) {
		this.parameter.put(name, wert);
	}

	public void addParameter(String name, Number zahl) {

		this.parameter.put(name, zahl);
	}

	public void addParameter(String name, Object o) {

		this.parameter.put(name, o);
	}

	/**
	 * Alle Parameter müssen identisch vorhanden sein die Reihenfolge der Parameter
	 * kann abweichen
	 */
	public boolean equalsParameter(List<Para> parameterList) {
		// die Anzahl der Paras muss gleich sein
		if (this.parameter.size() != parameterList.size())
			return false;
		// Iterieren mit der Liste
		for (Para para : parameterList) {
			// anhand des Key suchen in der HashMap
			Object o = this.parameter.get(para.getName());
			// die Werte vergleichen
			if (!equalsValue(para.getObject(), o))
				return false;
		}
		return true;
	}

	public String toStringParameter() {
		String result = "";
		Iterator<String> it = this.parameter.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next(); // der Key als String
			String value = this.parameter.get(key).toString(); // der Value als Object
			// der Parameter "indikator" ist eine Referenz auf eine IndikatorAlgorithmus
//			if (key =="indikator") result = result.concat(value.toString());
			result = result.concat(Util.separatorCSV + key + ":" + value);
		}
		return result;
	}

	public boolean equalsValue(Object object1, Object object2) {
		// wenn die Referenzen auf dasselbe Obekt zeigen muss es das selbe Objekt sein
		if (object1 == object2)
			return true;
		// wenn beide Objekte Strings sind und den selben Inhalt haben
		if (object1 instanceof String && object2 instanceof String) {
			String string1 = (String) object1;
			String string2 = (String) object2;
			if (string1.matches(string2))
				return true;
			// wenn die String unterschiedlichen Inhalt haben müssen sie unterschiedlich
			// sein
			else
				return false;
		}
		// beide Parameter sind IndikatorAlgorithmus
		// vor dem "Equals" muss das Nach-Laden statt gefunden haben.
		if (object1 instanceof IndikatorAlgorithmus && object2 instanceof IndikatorAlgorithmus) {
			IndikatorAlgorithmus iA1 = (IndikatorAlgorithmus) object1;
			IndikatorAlgorithmus iA2 = (IndikatorAlgorithmus) object2;
			return iA1.equals(iA2);
		}

		// vielleicht sind beide Integer
		Integer integer1 = parseInteger(object1);
		Integer integer2 = parseInteger(object2);
		if (integer1 instanceof Integer || integer2 instanceof Integer) {
			if (integer1.intValue() == integer2.intValue())
				return true;
			// wenn ein Integer dabei ist und nicht gleich ist müssen sie unterschiedlich
			else
				return false;
		}

		// vielleicht sind beide Float
		Float float1 = parseFloat(object1);
		Float float2 = parseFloat(object2);
		if (float1 instanceof Float || float2 instanceof Float) {
			if (float1.floatValue() == float2.floatValue())
				return true;
			// wenn ein Float dabei ist und nicht gleich ist müssen sie unterschiedlich sein
			else
				return false;
		}

		// es ist kein String, Integer, Float id, müssen die Referenzen überein stimmen
		// z.B. ein Typ Indikator
		if (object1.equals(object2))
			return true;
		return false;
	}

	private static Integer parseInteger(Object object) {
		// wenn es ein Integer ist, wird dieser zurück gegeben
		if (object instanceof Integer)
			return (Integer) object;
		// wenn es ein String ist, wird eine Umwandlung versucht
		if (object instanceof String) {
			String string = (String) object;
			try {
				return Integer.parseInt(string);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	private static Float parseFloat(Object object) {
		// wenn es ein Floatist, wird dieser zurück gegeben
		if (object instanceof Float)
			return (Float) object;
		// wenn es ein String ist, wird eine Umwandlung versucht
		if (object instanceof String) {
			String string = (String) object;
			try {
				return Float.parseFloat(string);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	/**
	 * Sucht alle nicht instanziierten IA-Parameter an denen ein String mit UUID
	 * hängt. Damit dieser ersetzt werden kann durch ein IA-Objekt
	 */
	public List<Para> findIAParameter() {
		List<Para> result = new ArrayList<Para>();
		List<Para> liste = this.getParameterList();
		for (Para para : liste) {
			if (para.getObject() instanceof String) {
				String string = (String) para.getObject();
				try {
					@SuppressWarnings("unused")
					UUID uuid = UUID.fromString(string);
					result.add(para);

				} catch (Exception e) {
					continue;
				}
			}
		}
		return result;
	}

	/**
	 * ein Para kann vom Typ Integer, Float, String oder IndikatorAlgorithmus sein.
	 * Der String kann eine UUID eines IndikatorAlgorithmus enthalten. Dieser
	 * UUID-String kann ersetzt werden durch den instanztiierten IA.
	 */
	public class Para {
		private String name;
		private Object object;

		Para(String name, Object object) {
			this.name = name;
			this.object = object;
		}

		public boolean equals(Para para) {
			boolean result = true;
			// der Name muss identisch sein
			if (!this.getName().matches(para.getName()))
				return false;
			// wenn es ein String ist
			if (this.getObject() instanceof String) {
				String objectString = (String) this.getObject();
				// VergleichsObject ist auch ein String
				if (!(para.getObject() instanceof String))
					return false;
				String equalsString = (String) para.getObject();
				if (!objectString.matches(equalsString))
					return false;
			}
			// wenn es ein Integer ist
			if (this.getObject() instanceof Integer) {
				Integer objectInteger = (Integer) this.getObject();
				// VergleichsObject ist auch ein Integer
				if (!(para.getObject() instanceof Integer))
					return false;
				Integer equalsInteger = (Integer) para.getObject();
				if (objectInteger.intValue() != equalsInteger.intValue())
					return false;
			}
			// wenn es ein Float ist
			if (this.getObject() instanceof Float) {
				Float objectFloat = (Float) this.getObject();
				// VergleichsObject ist auch ein Float
				if (!(para.getObject() instanceof Float))
					return false;
				Float equalsFloat = (Float) para.getObject();
				if (objectFloat.floatValue() != equalsFloat.floatValue())
					return false;
			}
			return result;
		}

		public String getName() {
			return name;
		}

		public Object getObject() {
			return object;
		}

		/**
		 * Wird genutzt, um aus einem UUID-String einen instanziierten
		 * Indikator-Algorithmus zu machen.
		 */
		public void replaceObject(Object o) {
			this.object = o;
		}
	}

}
