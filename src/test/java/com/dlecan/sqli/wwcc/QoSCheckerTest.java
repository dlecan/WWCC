package com.dlecan.sqli.wwcc;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;

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
        Object[] resultats = new Object[] { 374400, 20662, 39080, 20485, 74880 };
        
        runTest("wonka_data_2011_v3", resultats);
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
        Object[] resultats = qoSChecker.extractQoS(
                new File(getClass().getResource("/" + nomFichierTest + ".dat")
                        .toURI()), 2011, 11).toArray();

        if (attendus != null) {

            // Supression de la qualit� de service pour certains tests.
            Object[] resultatsModifies = new Object[attendus.length];
            System.arraycopy(resultats, 0, resultatsModifies, 0,
                    attendus.length);

            assertEquals(attendus, resultatsModifies);
        }
    }

}
