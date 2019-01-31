package com.algotrading.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import junit.framework.TestCase;

public class Spielwiese extends TestCase {

	/**
	 * 
	 * @startuml
	 * Klasse1 -> Kl2
	 * Kl2 --> Klasse1
	 * @enduml
	 * 
	 */
	
/*
	public void testBiConsumer () {
		Map<String,Number> num = new HashMap<>();
		num.put( "zwei", 2 );
		num.put( "drei", 3.0 );
		BiConsumer<String,Number> action = (key, value) -> {meineImplementierung(key,value);};
		num.forEach( action );
	}
	private void meineImplementierung (String key, Number value) {
		System.out.println( key + "=" + value);
	}
*/	
	public void testHashMap () {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("test1", new Integer(1));
		map.put("test2", new Integer(2));
		map.put("test3", new Integer(3));
		Set<String> keySet = map.keySet();
		// mit einem Iterator iterieren 
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			String key = it.next();
			System.out.println("Number: " + key + map.get(key));
		}
		// über die values iterieren
		for (Integer i : map.values()) {
			System.out.println("Values: " + i);
		}
		// über die Schlüssel iterieren 
		for (String s : map.keySet()) {
			Integer i = map.get(s);
			System.out.println("Keys: " + i);
		}
		// über die Entries iterieren 
		for (Object o : map.entrySet()) {
			System.out.println("Entries: " + o);
		}
		
	}
	
	public void testHashMapReplace () {
		HashMap<String, Integer> testMap = new HashMap<String, Integer>();
		testMap.put("1", new Integer(1));
		testMap.put("2", new Integer(2));
		
		Integer test = testMap.get("2");
		testMap.replace("1", 5);
		testMap.remove("2");
		testMap.put("2", 3);
		System.out.println("HashMapRaplace:" + testMap.get("1"));
		System.out.println("HashMapRaplace:" + testMap.get("2"));
		
	}
	
	public void testArrayList() {
		ArrayList<String> test = new ArrayList<String>();
		test.add("1");
		test.add("2");
		test.add("3");
		test.add("4");
		
		for (String t : test) {
			System.out.println("ArrayList: " + t);
		}
		
	}
	
	public void testNumber () {
		Number nummer1 = new Integer(1); 
		Number nummer2 = new Float(2.2); 
		System.out.println(nummer1.intValue());
		System.out.println(nummer2.intValue());
		System.out.println(nummer1.floatValue());
		System.out.println(nummer2.longValue());
	}
	
	
	
	
}
