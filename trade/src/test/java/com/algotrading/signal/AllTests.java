package com.algotrading.signal;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(SignalAuswertung.class);
		suite.addTestSuite(SignalsucheTest.class);
		suite.addTestSuite(TestGDDurchbruch.class);
		suite.addTestSuite(TestMinMax.class);
		suite.addTestSuite(TestMinMaxQuellAktie.class);
		//$JUnit-END$
		return suite;
	}

}
