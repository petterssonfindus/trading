package com.algotrading.indikator;

import junit.framework.Test;
import junit.framework.TestSuite;

public class IndikatorTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(IndikatorTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(TestAccumulationDistributionLine.class);
		suite.addTestSuite(TestGleitenderDurchschnitt.class);
		suite.addTestSuite(TestMoneyFlowMultiplier.class);
		suite.addTestSuite(TestOnBalanceVolume.class);
		suite.addTestSuite(TestRSI.class);
		suite.addTestSuite(TestSAR.class);
		suite.addTestSuite(TestVolatilitaet.class);
		//$JUnit-END$
		return suite;
	}

}
