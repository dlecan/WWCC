package com.dlecan.sqli.wwcc;

/**
 * Classe d'utilitaires.
 * 
 * @author dlecan
 */
public final class Utils {

    public static final int NB_SECONDES_MINUTE = 60;

    public static final int NB_SECONDES_HEURE = 60 * NB_SECONDES_MINUTE;

    public static final int NB_SECONDES_JOURNEE = 24 * NB_SECONDES_HEURE;

    public static final int NB_JOURS_MOIS_11 = 30;

    public static final int NB_SECONDES_MOIS_11 = NB_JOURS_MOIS_11
            * NB_SECONDES_JOURNEE;

    private Utils() {
        // Rien
    }

    /**
     * Calcul le delta en secondes entre la date passee en parametre et le 1er
     * novembre 2011.
     * 
     * @param jour
     *            Numero du jour dans le mois.
     * @param heure
     *            Heure
     * @param minutes
     *            Minutes
     * @param secondes
     *            Secondes
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
            int chiffre = Character.digit(bytes[i], 10);

            // En fonction de sa place dans le nombre final, on reconstitue les
            // puissances de 10.
            result += chiffre * Math.pow(10, offset - i);
        }
        return result;
    }

}
