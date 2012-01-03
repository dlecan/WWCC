package com.dlecan.sqli.wwcc;


import java.io.File;

public class LecanDamien_JSG_Wonka {

    /**
     * @param args
     */
    public static void main(String[] args) {
        long debut = System.currentTimeMillis();

        if (args != null && args.length > 1) {

            QoSChecker qoSChecker = new QoSChecker();
            Object[] resultats = qoSChecker.extractQoS(new File(args[0]));

            System.out.println("Mois : Novembre 2011");
            System.out.println("Client : The Willy Wonka Candy Company");
            System.out.println(String.format(
                    "Temps de fonctionnement théorique : %s secondes",
                    resultats[0]));
            System.out.println(String.format(
                    "Temps de rupture de chocolat blanc : %s secondes",
                    resultats[1]));
            System.out.println(String.format(
                    "Temps de rupture de chocolat noir : %s secondes",
                    resultats[1]));
            System.out.println(String.format(
                    "Temps de rupture de chocolat au lait : %s secondes",
                    resultats[1]));
            System.out.println(String.format(
                    "Temps d'indisponibilité globale : %s secondes",
                    resultats[1]));
            System.out.println();
            System.out.println(String.format(
                    "Qualité de Service novembre 2011 : %s%", resultats[1]));

        }
        System.out.println("time = " + (System.currentTimeMillis() - debut)
                + " millis");
    }

}
