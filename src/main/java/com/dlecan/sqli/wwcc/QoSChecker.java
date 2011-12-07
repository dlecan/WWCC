package com.dlecan.sqli.wwcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class QoSChecker {

	private static final DateTimeFormatter DTF = DateTimeFormat.forPattern(
			"dd/MM/yyyy HH:mm:ss").withZone(DateTimeZone.forID("Europe/Paris"));

	public String extractQoS(File qualityFile) {

		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(qualityFile));

			for (String line = br.readLine(); line != null; line = br
					.readLine()) {

				// Le concours porte sur le mois de novembre uniquement
				// On filtre les lignes qui ne nous concerne pas
				// On ne prend pas le risque de parser les autres dates
				// car certaines n'ont pas de sens dans la TZ Paris
				// Exemple : 27/03/2011 02:24:25, car changement d'heure d'été
				// A 2h, on saute directement à 3h 
				if (line.indexOf("/11/") != -1) {

					String[] strings = line.split(";");

					if (strings.length != 3) {
						// TODO
					}
					DateTime debut = DTF.parseDateTime(strings[0]);
					DateTime fin = DTF.parseDateTime(strings[1]);
					String type = strings[2];
				}
			}

		} catch (FileNotFoundException e) {
			throw new QoSCheckerException(e);
		} catch (IOException e) {
			throw new QoSCheckerException(e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}

		return "";
	}

}
