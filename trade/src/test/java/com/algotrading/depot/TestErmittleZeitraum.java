package com.algotrading.depot;

import java.util.ArrayList;
import java.util.GregorianCalendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.algotrading.util.AbstractTest;
import com.algotrading.util.DateUtil;
import com.algotrading.util.Util;
import com.algotrading.util.Zeitraum;

public class TestErmittleZeitraum extends AbstractTest {
	private static final Logger log = LogManager.getLogger(Util.class);

	public void testErmittleZeitraumn() {
		GregorianCalendar beginn = new GregorianCalendar(2017, 0, 1);
		GregorianCalendar ende = new GregorianCalendar(2018, 0, 1);

		ArrayList<Zeitraum> zeitraeume = Simulator.ermittleZeitraum(beginn, ende, 180, 30);
		assertEquals(zeitraeume.size(), 7);
		assertEquals(DateUtil.formatDate(zeitraeume.get(0).ende), "2017-06-30");

		for (Zeitraum zeitraum : zeitraeume) {
			log.info(zeitraum.toString());
		}

	}

}
