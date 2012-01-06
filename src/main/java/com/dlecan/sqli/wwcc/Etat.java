/**
 * 
 */
package com.dlecan.sqli.wwcc;

/**
 * Gestion des etats. Serait une enum si l'exigence de perf n'était pas si forte
 * (trop lent l'enum).
 * 
 * @author dlecan
 * 
 */
public class Etat {

    public static final byte ETAT_OUVERT_AUX_VISITES = 1 << 0; // 1

    public static final byte ETAT_CHOCOLAT_BLANC = 1 << 1; // 2

    public static final byte ETAT_CHOCOLAT_NOIR = 1 << 2; // 4

    public static final byte ETAT_CHOCOLAT_LAIT = 1 << 3; // 8

    public static final byte[] ETATS_CHOCOLAT = { ETAT_CHOCOLAT_BLANC,
            ETAT_CHOCOLAT_NOIR, ETAT_CHOCOLAT_LAIT };

}
