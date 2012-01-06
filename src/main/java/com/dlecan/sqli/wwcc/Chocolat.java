/**
 * 
 */
package com.dlecan.sqli.wwcc;

import static com.dlecan.sqli.wwcc.Etat.ETAT_CHOCOLAT_BLANC;
import static com.dlecan.sqli.wwcc.Etat.ETAT_CHOCOLAT_LAIT;
import static com.dlecan.sqli.wwcc.Etat.ETAT_CHOCOLAT_NOIR;

/**
 * Les types de chocolats fabriques.
 * <p>
 * Pourrait etre une enum, mais c'est trop lent.
 * </p>
 * 
 * @author dlecan
 * 
 */
public final class Chocolat {

    private static final byte TYPE_CHOCOLAT_BLANC = (byte) '1';

    private static final byte TYPE_CHOCOLAT_NOIR = (byte) '2';

    private static final byte TYPE_CHOCOLAT_LAIT = (byte) '3';

    private Chocolat() {
        // Rien
    }

    /**
     * Recupere l'etat du chocolat a partir de son type.
     * 
     * @param type
     *            Type : 1, 2 ou 3, codé en octet.
     * @return L'état correspondant.
     */
    public static byte fromType(byte type) {
        byte retour;

        switch (type) {

        case TYPE_CHOCOLAT_BLANC:
            retour = ETAT_CHOCOLAT_BLANC;
            break;

        case TYPE_CHOCOLAT_NOIR:
            retour = ETAT_CHOCOLAT_NOIR;
            break;

        case TYPE_CHOCOLAT_LAIT:
        default:
            retour = ETAT_CHOCOLAT_LAIT;
            break;

        }
        return retour;
    }

    /**
     * Recupere le type de chocolat a partir de son etat.
     * 
     * @param etat
     *            L'etat.
     * @return Le type correspondant (attention, codé en octet).
     */
    public static byte fromEtat(byte etat) {
        byte retour;

        switch (etat) {

        case ETAT_CHOCOLAT_BLANC:
            retour = TYPE_CHOCOLAT_BLANC;
            break;

        case ETAT_CHOCOLAT_NOIR:
            retour = TYPE_CHOCOLAT_NOIR;
            break;

        case ETAT_CHOCOLAT_LAIT:
        default:
            retour = TYPE_CHOCOLAT_LAIT;
            break;

        }
        return retour;
    }

}
