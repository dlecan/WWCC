package com.dlecan.sqli.wwcc;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

public class LecanDamien_JSG_Wonka {

    /**
     * @param args
     */
    public static void main(String[] args) {
        long debut = System.currentTimeMillis();

        if (args != null && args.length > 0) {

            QoSChecker qoSChecker = new QoSChecker();
            Object[] resultats = qoSChecker.extractQoS(new File(args[0]));

            System.out.println("Mois : Novembre 2011");
            System.out.println("Client : The Willy Wonka Candy Company");
            System.out.println();
            
            System.out.println(String.format(
                    "Temps de fonctionnement th\u00E9orique : %s secondes",
                    resultats[0]));
            System.out.println(String.format(
                    "Temps de rupture de chocolat blanc : %s secondes",
                    resultats[1]));
            System.out.println(String.format(
                    "Temps de rupture de chocolat noir : %s secondes",
                    resultats[2]));
            System.out.println(String.format(
                    "Temps de rupture de chocolat au lait : %s secondes",
                    resultats[3]));
            System.out.println(String.format(
                    "Temps d'indisponibilit\u00E9 globale : %s secondes",
                    resultats[4]));
            System.out.println();

            NumberFormat percentInstance = NumberFormat.getPercentInstance(Locale.FRANCE);
            percentInstance.setMaximumFractionDigits(2);
            String qos = percentInstance.format(
                    resultats[5]);
            System.out
                    .println("Qualit\u00E9 de Service novembre 2011 : " + qos);

        }
        System.out.println("time = " + (System.currentTimeMillis() - debut)
                + " millis");
    }

}
