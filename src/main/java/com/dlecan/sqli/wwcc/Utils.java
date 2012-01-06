package com.dlecan.sqli.wwcc;

/**
 * Classe d'utilitaires.
 * 
 * @author dlecan
 */
public final class Utils {

    private static final int NB_SECONDES_MINUTE = 60;

    public static final int NB_SECONDES_HEURE = 60 * NB_SECONDES_MINUTE;

    public static final int NB_SECONDES_JOURNEE = 24 * NB_SECONDES_HEURE;

    private static final int[] PUISSANCES_10 = { 1, 10, 100, 1000, 10000,
            100000, 1000000 };

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
                // jours car les jours sont indexes de 0 a
                // 29 (pour 30j dans le mois de novembre)
                + heure * NB_SECONDES_HEURE //
                + minutes * NB_SECONDES_MINUTE //
                + secondes;

        return delta;
    }

    public static int toInt(byte[] bytes, int from, int offset) {
        int result = 0;
        for (int i = from; i <= offset; i++) {
            int chiffre = Character.digit(bytes[i], 10);

            // En fonction de sa place dans le nombre final, on reconstitue les
            // puissances de 10.
            result += chiffre * PUISSANCES_10[offset - i];
        }
        return result;
    }

    /**
     * Indique si un entier en contient au autre (au sens operation de bits).
     * 
     * @param ensemble
     *            Ensemble ou la donnee peut se trouver.
     * @param aVerifier
     *            Donnee a chercher.
     * @return <code>true</code> si la donnee est "incluse" dans l'ensemble.
     *         <code>false</code> sinon.
     */
    public static boolean contient(int ensemble, int aVerifier) {
        return (ensemble & aVerifier) == aVerifier;
    }

}
