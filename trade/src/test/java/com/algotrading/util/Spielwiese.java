package com.algotrading.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import junit.framework.TestCase;

public class Spielwiese extends TestCase {

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
//		map.put("test2", new Float(2.2));
//		map.put("test3", new Long(3));
		Set<String> keySet = map.keySet();
		Iterator<String> it = keySet.iterator();
		while (it.hasNext()) {
			String key = it.next();
			System.out.println("Number: " + key + map.get(key));
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
