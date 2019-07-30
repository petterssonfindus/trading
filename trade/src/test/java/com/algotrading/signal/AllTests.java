package com.algotrading.signal;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.algotrading.Application;

import junit.framework.Test;
import junit.framework.TestSuite;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(SignalAuswertung.class);
		suite.addTestSuite(SignalsucheTest.class);
		suite.addTestSuite(TestGDDurchbruch.class);
		suite.addTestSuite(TestMinMax.class);
		suite.addTestSuite(TestMinMaxQuellAktie.class);
		suite.addTestSuite(TestSignalBewertungEquals.class);
		// $JUnit-END$
		return suite;
	}

}
