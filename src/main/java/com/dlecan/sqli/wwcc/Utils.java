package com.dlecan.sqli.wwcc;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public final class Utils {

	private static final DateTimeFormatter DTF = DateTimeFormat.forPattern(
			"dd/MM/yyyy HH:mm:ss").withZone(DateTimeZone.forID("Europe/Paris"));

	private static final DateTime PREMIER_NOVEMBRE = new DateTime(2011, 11, 1,
			0, 0, 0);

	/**
	 * Parse la date (pattern "dd/MM/yyyy HH:mm:ss").
	 * 
	 * @param date
	 *            Date à parser.
	 * @return Date parsée.
	 */
	public static DateTime getDate(String date) {
		return DTF.parseDateTime(date);
	}

	/**
	 * Parse la date (pattern "dd/MM/yyyy HH:mm:ss").
	 * 
	 * @param date
	 *            Date à parser.
	 * @return Timestamp.
	 */
	public static int getTimestampInSeconds(String date) {
		return (int) DTF.parseMillis(date) / 1000;
	}

	/**
	 * Calcul le delta en secondes entre la date passé en paramètre et le 1er
	 * novembre 2011.
	 * 
	 * @param date
	 *            Date à parser.
	 * @return Delta calculé.
	 */
	public static int getDelta(String date) {
		int delta = (int) ((DTF.parseMillis(date) - PREMIER_NOVEMBRE.getMillis()) / 1000);
		return delta;
	}

}
