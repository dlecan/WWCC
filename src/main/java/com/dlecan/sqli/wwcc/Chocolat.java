/**
 * 
 */
package com.dlecan.sqli.wwcc;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Les types de chocolats fabriqu�s.
 * 
 * @author inulecd
 * 
 */
public enum Chocolat {

    BLANC((byte) '1', "blanc", QoSChecker.ETAT_CHOCOLAT_BLANC),

    NOIR((byte) '2', "noir", QoSChecker.ETAT_CHOCOLAT_NOIR),

    LAIT((byte) '3', "au lait", QoSChecker.ETAT_CHOCOLAT_LAIT);

    private static final Map<Byte, Chocolat> ASSOC_TYPE = Maps.newHashMap();

    static {
        for (Chocolat c : Chocolat.values()) {
            ASSOC_TYPE.put(c.type, c);
        }
    }

    private static final Map<Byte, Chocolat> ASSOC_ETAT = Maps.newHashMap();

    static {
        for (Chocolat c : Chocolat.values()) {
            ASSOC_ETAT.put(c.etat, c);
        }
    }

    private final byte type;

    private final String nom;

    private final byte etat;

    private Chocolat(byte type, String nom, byte etat) {
        this.type = type;
        this.nom = nom;
        this.etat = etat;
    }

    /**
     * R�cup�re l'instance de {@link Chocolat} � partir de son type.
     * 
     * @param type
     *            Type : 1, 2 ou 3.
     * @return <code>null</code> Si type diff�rent de 1, 2 ou 3.
     */
    public static Chocolat fromType(byte type) {
        return ASSOC_TYPE.get(type);
    }

    /**
     * R�cup�re l'instance de {@link Chocolat} � partir de son état.
     * 
     * @param type
     *            L'état.
     * @return <code>null</code> Si type inconnu.
     */
    public static Chocolat fromEtat(byte etat) {
        return ASSOC_ETAT.get(etat);
    }

    @Override
    public String toString() {
        return nom;
    }

    public byte getEtat() {
        return etat;
    }

}
