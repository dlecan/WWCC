package com.dlecan.sqli.wwcc;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;

public class QoSCheckerTest {

	private QoSChecker qoSChecker;

	@Before
	public void setUp() throws Exception {
		qoSChecker = new QoSChecker();
	}

	@Test
	public void testExtractQoS() throws Exception {
		runTest();
	}

    private void runTest() throws URISyntaxException {
        StopWatch stopWatch = new Slf4JStopWatch("testExtractQoS");

		qoSChecker.extractQoS(new File(getClass().getResource(
				"/wonka_data_2011_v2.dat").toURI()));

		stopWatch.stop();
    }

}
