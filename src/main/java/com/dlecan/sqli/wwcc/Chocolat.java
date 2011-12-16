/**
 * 
 */
package com.dlecan.sqli.wwcc;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Les types de chocolats fabriqués.
 * 
 * @author inulecd
 * 
 */
public enum Chocolat {

	BLANC("1", "blanc"),

	NOIR("2", "noir"),

	LAIT("3", "au lait");

	private static final Map<String, Chocolat> ASSOC = Maps.newHashMap();

	static {
		for (Chocolat c : Chocolat.values()) {
			ASSOC.put(c.type, c);
		}
	}

	private final String type;
	private final String nom;

	private Chocolat(String type, String nom) {
		this.type = type;
		this.nom = nom;
	}
	
	/**
	 * Récupère l'instance de {@link Chocolat} à partir de son type.
	 * 
	 * @param type
	 *            Type : 1, 2 ou 3.
	 * @return <code>null</code> Si type différent de 1, 2 ou 3.
	 */
	public static Chocolat fromType(String type) {
		return ASSOC.get(type);
	}

	@Override
	public String toString() {
		return nom;
	}
	
}
