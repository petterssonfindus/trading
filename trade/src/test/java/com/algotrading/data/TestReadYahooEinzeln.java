package com.algotrading.data;

import junit.framework.TestCase;

public class TestReadYahooEinzeln extends TestCase {
	

	public void testReadYahoo () {
		ReadDataYahoo.YahooWSController("^VIX1Y");
	}

}
