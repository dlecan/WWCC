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

//    @Test
    public void test_wonka_data_2011_v2() throws Exception {
        runTest("wonka_data_2011_v2.dat");
    }
    
    @Test
    public void test_test1() throws Exception {
        runTest("test1");
    }

    private void runTest(String nomFichierTest) throws URISyntaxException {
        StopWatch stopWatch = new Slf4JStopWatch("testExtractQoS_"
                + nomFichierTest);

        qoSChecker.extractQoS(new File(getClass().getResource(
                "/" + nomFichierTest).toURI()));

        stopWatch.stop();
    }

}
