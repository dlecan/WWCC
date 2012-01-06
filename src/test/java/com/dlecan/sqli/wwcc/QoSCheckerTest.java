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
    public void test_wonka_data_2011() throws Exception {
        Object[] resultats = new Object[] { 374400, 20662, 39088, 20485, 74888 };

        runTest("wonka_data_2011", resultats);
    }

    @Test
    public void test_wonka_data_2011_v2() throws Exception {
        Object[] resultats = new Object[] { 374400, 20662, 39080, 20485, 74880 };

        runTest("wonka_data_2011_v2", resultats);
    }

    @Test
    public void test_wonka_data_2011_v3() throws Exception {
        runTest("wonka_data_2011_v3", null);
    }

    @Test
    public void test_test1() throws Exception {
        Object[] resultats = new Object[] { 374400, 5400, 3600, 4104, 13104,
                ((double) 0.965) };

        runTest("test1", resultats);
    }

    @Test
    public void test_test2() throws Exception {
        Object[] resultats = new Object[] { 374400, 19800, 18000, 18000, 30600 };

        runTest("test2", resultats);
    }

    @Test
    public void test_test3() throws Exception {
        Object[] resultats = new Object[] { 374400, 5399, 3600, 4104, 13102 };

        runTest("test3", resultats);
    }

    @Test
    public void test_test4() throws Exception {
        Object[] resultats = new Object[] { 374400, 7200, 7200, 7200, 7200 };

        runTest("test4", resultats);
    }

    @Test
    public void test_test5() throws Exception {
        Object[] resultats = new Object[] { 374400, 36000, 21600, 21600, 72000 };

        runTest("test5", resultats);
    }

    private void runTest(String nomFichierTest, Object[] attendus)
            throws URISyntaxException {
        StopWatch stopWatch = new Slf4JStopWatch("testExtractQoS_"
                + nomFichierTest);

        Object[] resultats = qoSChecker.extractQoS(new File(getClass()
                .getResource("/" + nomFichierTest + ".dat").toURI()));

        stopWatch.stop();

        if (attendus != null) {

            // Supression de la qualité de service pour certains tests.
            Object[] resultatsModifies = Arrays.copyOf(resultats,
                    attendus.length);

            assertEquals(attendus, resultatsModifies);
        }
    }

}
