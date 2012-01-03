package com.dlecan.sqli.wwcc;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Arrays;

import static org.junit.Assert.*;
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
    public void test_wonka_data_2011_v2() throws Exception {
        runTest("wonka_data_2011_v2", null);
    }

//    @Test
    public void test_test1() throws Exception {
        Object[] resultats = new Object[] { 374400, 5400, 3600, 4104, 13104,
                ((double) 0.965) };

        runTest("test1", resultats);
    }

//    @Test
    public void test_test2() throws Exception {
        Object[] resultats = new Object[] { 374400, 19800, 18000, 18000, 30600 };

        runTest("test2", resultats);
    }

    private void runTest(String nomFichierTest, Object[] attendus)
            throws URISyntaxException {
        StopWatch stopWatch = new Slf4JStopWatch("testExtractQoS_"
                + nomFichierTest);

        Object[] resultats = qoSChecker.extractQoS(new File(getClass()
                .getResource("/" + nomFichierTest + ".dat").toURI()));

        stopWatch.stop();

        if (attendus != null) {

            // Supression de la qualit� de service pour certains tests.
            Object[] resultatsModifies = Arrays.copyOf(resultats,
                    attendus.length);

            assertEquals(attendus, resultatsModifies);
        }
    }

}
