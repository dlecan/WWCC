package com.dlecan.sqli.wwcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.perf4j.StopWatch;
import org.perf4j.slf4j.Slf4JStopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

public class QoSChecker {

	private static final DateTimeFormatter DTF = DateTimeFormat.forPattern(
			"dd/MM/yyyy HH:mm:ss").withZone(DateTimeZone.forID("Europe/Paris"));

	private static final Logger LOGGER = LoggerFactory
			.getLogger(QoSChecker.class);

	public String extractQoS(File qualityFile) {
		StopWatch stopWatch = new Slf4JStopWatch("extractQoS");

		SortedSetMultimap<Chocolat, Interval> intervalsChocolats = extractIntervals(qualityFile);

		intervalsChocolats = mergeIntervals(intervalsChocolats);

		tempsPendantLequelManqueChaqueTypeChocolat(intervalsChocolats);
		tempsPendantLequelManqueAuMoinsUnTypeChocolat(intervalsChocolats);

		stopWatch.stop();
		return "";
	}

	private SortedSetMultimap<Chocolat, Interval> mergeIntervals(
			SortedSetMultimap<Chocolat, Interval> intervalsChocolats) {
		StopWatch stopWatch = new Slf4JStopWatch("mergeIntervals");

		for (Chocolat chocolat : Chocolat.values()) {

			Collection<Interval> intervals = intervalsChocolats.get(chocolat);

			int nbIntervalsAvantFusion = intervals.size();

			Iterator<Interval> iteratorIntervals = intervals.iterator();

			List<Interval> newIntervals = Lists.newArrayList();

			// On a forcément un interval par chocolat, inutile de tester
			// si il y en a au moins un.
			Interval previousInterval = iteratorIntervals.next();

			while (iteratorIntervals.hasNext()) {

				Interval currentInterval = iteratorIntervals.next();

				if (previousInterval.contains(currentInterval)) {
					// L'interval 'current' ne sert à rien, il est inclus
					// dans un autre. On ne le garde pas
				} else if (previousInterval.abuts(currentInterval)
						|| previousInterval.overlaps(currentInterval)) {
					// Les 2 intervals se touchent ou se recouvrent.
					// On les fusionnent
					previousInterval = new Interval(
							previousInterval.getStart(), currentInterval
									.getEnd());
				} else {
					// Cas du "gap"
					// On garde l'interval le plus ancien et on saute au
					// suivant.
					newIntervals.add(previousInterval);
					previousInterval = currentInterval;
				}

			}

			LOGGER.debug(
					"Nb intervals pour le chocolat {} || Avant {} || Après {}",
					new Object[] { chocolat, nbIntervalsAvantFusion,
							newIntervals.size() });

		}

		stopWatch.stop();

		return intervalsChocolats;
	}

	private void tempsPendantLequelManqueChaqueTypeChocolat(
			Multimap<Chocolat, Interval> intervalsChocolats) {
		StopWatch stopWatch = new Slf4JStopWatch(
				"tempsPendantLequelManqueChaqueTypeChocolat");

		for (Chocolat chocolat : Chocolat.values()) {

			Collection<Interval> intervals = intervalsChocolats.get(chocolat);

			long dureeTotale = 0;

			for (Interval interval : intervals) {
				dureeTotale += interval.toDurationMillis();
			}

			long dureeTotalFinale = TimeUnit.SECONDS.convert(dureeTotale,
					TimeUnit.MILLISECONDS);

			LOGGER.info("Temps de rupture de chocolat {} : {} secondes",
					new Object[] { chocolat.toString(), dureeTotalFinale });

		}

		stopWatch.stop();
	}

	private void tempsPendantLequelManqueAuMoinsUnTypeChocolat(
			SortedSetMultimap<Chocolat, Interval> intervalsChocolats) {
		StopWatch stopWatch = new Slf4JStopWatch(
				"tempsPendantLequelManqueAuMoinsUnTypeChocolat");

		long dureeTotale = 0;

		long dureeTotalFinale = TimeUnit.SECONDS.convert(dureeTotale,
				TimeUnit.MILLISECONDS);

		LOGGER.info("Temps d'indisponibilité globale : {} secondes",
				dureeTotalFinale);

		stopWatch.stop();
	}

	private SortedSetMultimap<Chocolat, Interval> extractIntervals(
			File qualityFile) {
		StopWatch stopWatch = new Slf4JStopWatch("extractIntervals");
		TreeMultimap<Chocolat, Interval> result = TreeMultimap.create(Ordering
				.natural(), new OlderFirstIntervalComparator());

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

					String[] s = line.split(";");

					if (s.length != 3) {
						LOGGER
								.warn(
										"Skip line [{}] because it isn't well-formatted",
										line);
					}
					try {
						DateTime debut = DTF.parseDateTime(s[0]);
						DateTime fin = DTF.parseDateTime(s[1]);
						Interval interval = new Interval(debut, fin);
						// Reformatage de la date
						// Très risqué
						// char[] strChar0 = reformatString(s[0]);
						// hirondelle.date4j.DateTime debut = new
						// hirondelle.date4j.DateTime(String.valueOf(strChar0));
						// char[] strChar1 = reformatString(s[1]);
						// hirondelle.date4j.DateTime fin = new
						// hirondelle.date4j.DateTime(String.valueOf(strChar1));
						String type = s[2];

						Chocolat chocolat = Chocolat.fromType(type);
						result.put(chocolat, interval);

					} catch (IllegalArgumentException e) {
						LOGGER.warn("Skip line [{}] because : {}",
								new Object[] { line, e.getMessage() });
					}
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
		stopWatch.stop();
		return result;
	}

	private char[] reformatString(String m) {
		char[] strChar0 = new char[] {
		// yyyy
				m.charAt(6), m.charAt(7), m.charAt(8), m.charAt(9),
				// -
				'-',
				// MM
				m.charAt(3), m.charAt(4),
				// -
				'-',
				// dd
				m.charAt(0), m.charAt(1),
				// ' '
				' ',
				// HH
				m.charAt(11), m.charAt(12),
				// :
				':',
				// mm
				m.charAt(14), m.charAt(15),
				// :
				':',
				// ss
				m.charAt(17), m.charAt(18), };
		return strChar0;
	}

}
