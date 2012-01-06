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
            TimeLine tl = qoSChecker.extractQoS(new File(args[0]));

            StringBuilder sb = new StringBuilder();

            sb.append("Mois : Novembre 2011");
            sb.append('\n');
            sb.append("Client : The Willy Wonka Candy Company");
            sb.append('\n');
            sb.append('\n');

            sb.append("Temps de fonctionnement th\u00E9orique : ");
            sb.append(tl.getTempsFonctionnementTheorique());
            sb.append(" secondes");
            sb.append('\n');

            sb.append("Temps de rupture de chocolat blanc : ");
            sb.append(tl.getTempsRuptureChocolatBlanc());
            sb.append(" secondes");
            sb.append('\n');

            sb.append("Temps de rupture de chocolat noir : ");
            sb.append(tl.getTempsRuptureChocolatNoir());
            sb.append(" secondes");
            sb.append('\n');

            sb.append("Temps de rupture de chocolat au lait : ");
            sb.append(tl.getTempsRuptureChocolatLait());
            sb.append(" secondes");
            sb.append('\n');

            sb.append("Temps d'indisponibilit\u00E9 globale : ");
            sb.append(tl.getTempsIndisponibiliteGlobale());
            sb.append(" secondes");
            sb.append('\n');
            sb.append('\n');

            NumberFormat percentInstance = new DecimalFormat("00.0#%");
            String qos = percentInstance.format(tl.getQos());
            sb.append("Qualit\u00E9 de Service novembre 2011 : ");
            sb.append(qos);
            sb.append('\n');

            System.out.println(sb.toString());
        }

        System.out.println("time = " + (System.currentTimeMillis() - debut)
                + " millis");
    }

}
