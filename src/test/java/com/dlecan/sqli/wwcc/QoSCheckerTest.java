package com.dlecan.sqli.wwcc;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

public class QoSCheckerTest {

	private QoSChecker qoSChecker;

	@Before
	public void setUp() throws Exception {
		qoSChecker = new QoSChecker();
	}

	@Test
	public void testExtractQoS() throws Exception {
		long debut = System.currentTimeMillis();

		qoSChecker.extractQoS(new File(getClass().getResource(
				"/wonka_data_2011.dat").toURI()));

		long fin = System.currentTimeMillis();

		System.out.println(String.format("Durée : %s ms", (fin - debut)));

	}

}
