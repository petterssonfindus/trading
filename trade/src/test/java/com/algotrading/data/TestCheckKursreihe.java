package com.algotrading.data;

import com.algotrading.util.AbstractTest;

public class TestCheckKursreihe extends AbstractTest {

	public static void testCheckKursreihe() {
		DBManager.checkKursreihe("xxxgdaxi", 0.1f);
	}

}
