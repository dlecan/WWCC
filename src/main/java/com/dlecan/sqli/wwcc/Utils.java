package com.dlecan.sqli.wwcc;


public final class Utils {

    public static final int NB_SECONDES_MINUTE = 60;

    public static final int NB_SECONDES_HEURE = 60 * NB_SECONDES_MINUTE;

    public static final int NB_SECONDES_JOURNEE = 24 * NB_SECONDES_HEURE;

    public static final int NB_JOURS_MOIS_11 = 30;

    public static final int NB_SECONDES_MOIS_11 = NB_JOURS_MOIS_11
            * NB_SECONDES_JOURNEE;

    /**
     * Calcul le delta en secondes entre la date passee en parametre et le 1er
     * novembre 2011.
     * 
     * @param date
     *            Date a parser.
     * @return Delta calcule, en secondes depuis le 01/11.
     */
    public static int getDelta(int jour, int heure, int minutes, int secondes) {

        int delta = (jour - 1) * NB_SECONDES_JOURNEE // -1 sur le nombre de
                // jours car les jours
                // sont indexés de 0 à
                // 29 (pour 30j dans le
                // mois de novembre)
                + heure * NB_SECONDES_HEURE //
                + minutes * NB_SECONDES_MINUTE //
                + secondes;

        return delta;
    }

    public static int toInt(byte[] bytes, int from, int offset) {
        // TODO : Faire ces calculs en binaire si besoin
        // car plus rapide
        int result = 0;
        for (int i = from; i <= offset; i++) {
            result += (Character.digit(bytes[i], 10))
                    * Math.pow(10, offset - i);
        }
        return result;
    }

}
