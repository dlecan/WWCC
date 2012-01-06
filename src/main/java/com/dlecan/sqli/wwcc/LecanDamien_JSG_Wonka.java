package com.dlecan.sqli.wwcc;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Classe de lancement.
 * 
 * @author dlecan
 */
public class LecanDamien_JSG_Wonka {

    /**
     * Main.
     */
    public static void main(String[] args) {
        long debut = System.currentTimeMillis();

        if (args != null && args.length > 0) {

            QoSChecker qoSChecker = new QoSChecker();
            Object[] resultats = qoSChecker.extractQoS(new File(args[0]));

            StringBuilder sb = new StringBuilder();

            sb.append("Mois : Novembre 2011");
            sb.append('\n');
            sb.append("Client : The Willy Wonka Candy Company");
            sb.append('\n');
            sb.append('\n');

            sb.append("Temps de fonctionnement th\u00E9orique : ");
            sb.append(resultats[0]);
            sb.append(" secondes");
            sb.append('\n');

            sb.append("Temps de rupture de chocolat blanc : ");
            sb.append(resultats[1]);
            sb.append(" secondes");
            sb.append('\n');

            sb.append("Temps de rupture de chocolat noir : ");
            sb.append(resultats[2]);
            sb.append(" secondes");
            sb.append('\n');

            sb.append("Temps de rupture de chocolat au lait : ");
            sb.append(resultats[3]);
            sb.append(" secondes");
            sb.append('\n');

            sb.append("Temps d'indisponibilit\u00E9 globale : ");
            sb.append(resultats[4]);
            sb.append(" secondes");
            sb.append('\n');
            sb.append('\n');

            NumberFormat percentInstance = new DecimalFormat("00.0#%");
            String qos = percentInstance.format(resultats[5]);
            sb.append("Qualit\u00E9 de Service novembre 2011 : ");
            sb.append(qos);
            sb.append('\n');

            System.out.println(sb.toString());
        }

        System.out.println("time = " + (System.currentTimeMillis() - debut)
                + " millis");
    }

}
