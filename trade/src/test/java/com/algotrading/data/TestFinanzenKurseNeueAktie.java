package com.algotrading.data;

import static org.junit.Assert.*;

import java.util.GregorianCalendar;

import org.junit.Test;

import com.algotrading.util.DateUtil;

public class TestFinanzenKurseNeueAktie {

	@Test
	public void test() {
		String name = "fresenius";
		GregorianCalendar beginn = DateUtil.createGregorianCalendar(01, 01, 1998);
		
		ReadDataFinanzen.FinanzenWSController(name, true, true, beginn);
		
		assertTrue(true);
	}

}
